package dev.chicoferreira.lifestealer.user;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.LifestealerExecutor;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.events.*;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropAction;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestrictionManager;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LifestealerUserListener implements Listener {

    private final LifestealerExecutor executor;
    private final Logger logger;
    private final LifestealerUserManager userManager;
    private final LifestealerHeartItemManager heartItemManager;
    private final LifestealerUserController userController;
    private final LifestealerHeartDropRestrictionManager heartDropRestrictionManager;
    private Component errorKickMessage;

    public LifestealerUserListener(LifestealerExecutor executor, Logger logger, LifestealerHeartItemManager heartItemManager, LifestealerUserController userController, LifestealerUserManager userManager, LifestealerHeartDropRestrictionManager heartDropRestrictionManager, Component errorKickMessage) {
        this.executor = executor;
        this.logger = logger;
        this.heartItemManager = heartItemManager;
        this.userController = userController;
        this.userManager = userManager;
        this.heartDropRestrictionManager = heartDropRestrictionManager;
        this.errorKickMessage = errorKickMessage;
    }

    public void setErrorKickMessage(Component errorKickMessage) {
        this.errorKickMessage = errorKickMessage;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        // This runs in a separate thread so it's safe to do blocking operations
        try {
            if (userManager.getOnlineUser(event.getUniqueId()) != null) {
                // user already loaded
                return;
            }
            LifestealerUser lifestealerUser = userManager.loadUser(event.getUniqueId());
            if (lifestealerUser == null) {
                // this already needs to run in the main thread (createUser is not thread-safe)
                executor.sync().execute(() -> userManager.createUser(event.getUniqueId()));
            } else {
                // same here
                executor.sync().execute(() -> userManager.registerLoadedUser(lifestealerUser));
            }
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, errorKickMessage);
            logger.log(Level.SEVERE, "An error occurred while loading user data", e);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        try {
            LifestealerUser user = userManager.getOrLoadUser(player.getUniqueId());
            if (!userController.getBanSettings().external()) {
                LifestealerUser.Ban ban = userController.getBan(user);

                if (ban != null) {
                    Component kickMessageComponent = MiniMessage.builder().build().deserialize(
                            userController.getBanSettings().joinMessage(),
                            Placeholder.component("player", player.name()),
                            Formatter.date("date", ban.endZoned()),
                            DurationUtils.formatDurationTag("remaining", ban.remaining()));
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessageComponent);
                }
            }
        } catch (Exception e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, errorKickMessage);
            logger.log(Level.SEVERE, "An error occurred while loading user data", e);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LifestealerUser user = userManager.getOnlineUser(player);

        userController.updatePlayerHearts(player, user);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemRightClick(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        int hearts = heartItemManager.getHearts(item);
        if (hearts == 0) {
            return;
        }

        event.setCancelled(true);

        LifestealerUser user = userManager.getOnlineUser(event.getPlayer());

        LifestealerPreConsumeHeartEvent preConsumeHeartEvent = new LifestealerPreConsumeHeartEvent(event.getPlayer(), user, item, hearts);
        if (!preConsumeHeartEvent.callEvent()) {
            return;
        }

        hearts = preConsumeHeartEvent.getAmount(); // developers can change the amount of hearts

        LifestealerUserController.ChangeHeartsResult result = userController.addHearts(event.getPlayer(), user, hearts);

        if (result.hasChanged()) {
            item.subtract();
            LifestealerMessages.CONSUME_HEART_SUCCESS.sendTo(event.getPlayer(), Formatter.number("amount", hearts));
        } else {
            LifestealerMessages.CONSUME_HEART_ALREADY_FULL.sendTo(event.getPlayer());
        }

        LifestealerPostConsumeHeartEvent postConsumeHeartEvent = new LifestealerPostConsumeHeartEvent(event.getPlayer(), user, hearts, item, result);
        postConsumeHeartEvent.callEvent();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }

        Player player = event.getEntity();
        LifestealerUser user = userManager.getOnlineUser(player);

        LifestealerHeartItem itemToDropWhenPlayerDies = heartItemManager.getItemToDropWhenPlayerDies();

        ItemStack itemStack = heartItemManager.generateItem(itemToDropWhenPlayerDies);
        int hearts = heartItemManager.getHearts(itemStack);

        LifestealerPrePlayerDeathEvent prePlayerDeathEvent = new LifestealerPrePlayerDeathEvent(event, player, user, itemStack, hearts);
        if (!prePlayerDeathEvent.callEvent()) {
            return;
        }

        hearts = prePlayerDeathEvent.getHeartsToRemove();
        itemStack = prePlayerDeathEvent.getItemStackToDrop();

        LifestealerHeartDropAction action = heartDropRestrictionManager.evaluateHeartDropAction(player, user, event);

        if (action.shouldDropItem()) {
            event.getDrops().add(itemStack);
        }

        if (!action.shouldRemoveHearts()) {
            return;
        }

        LifestealerUserController.ChangeHeartsResult result = userController.removeHearts(player, user, hearts);

        LifestealerPostPlayerDeathEvent postPlayerDeathEvent = new LifestealerPostPlayerDeathEvent(event, player, user, itemStack, hearts, result);
        postPlayerDeathEvent.callEvent();

        int heartsWithoutClamp = result.previousHearts() - hearts;

        LifestealerUserRules rules = userController.computeUserRules(player, user);

        if (heartsWithoutClamp < rules.minHearts()) { // if the player would have less than the minimum amount of hearts
            Duration banDuration = rules.banTime();

            LifestealerPreUserBanEvent banEvent = new LifestealerPreUserBanEvent(player, user, banDuration);
            if (!banEvent.callEvent()) {
                return;
            }

            banDuration = banEvent.getBanDuration();

            userController.banUser(player, user, banDuration);
        }
    }
}
