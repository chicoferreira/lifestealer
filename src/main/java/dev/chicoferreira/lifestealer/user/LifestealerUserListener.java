package dev.chicoferreira.lifestealer.user;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.Lifestealer;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.events.*;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropAction;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
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
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LifestealerUserListener implements Listener {

    private final Logger logger;
    private final Lifestealer lifestealer;
    private Component errorKickMessage;

    public LifestealerUserListener(Logger logger, Lifestealer lifestealer, Component errorKickMessage) {
        this.logger = logger;
        this.lifestealer = lifestealer;
        this.errorKickMessage = errorKickMessage;
    }

    public void setErrorKickMessage(Component errorKickMessage) {
        this.errorKickMessage = errorKickMessage;
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        try {
            lifestealer.getUserManager().getOrLoadUser(event.getUniqueId());
        } catch (Exception e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, errorKickMessage);
            logger.log(Level.SEVERE, "An error occurred while loading user data", e);
        }
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        try {
            LifestealerUser user = lifestealer.getUserManager().getOrLoadUser(player.getUniqueId());

            LifestealerUser.Ban ban;
            user.writeLock();
            try {
                ban = this.lifestealer.getUserController().getBanIfNotExternalSettingEnabled(user);
            } finally {
                user.writeUnlock();
            }
            if (ban != null) {
                Component kickMessageComponent = getBanMessageComponent(player, ban);
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessageComponent);
            }
        } catch (Exception e) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, errorKickMessage);
            logger.log(Level.SEVERE, "An error occurred while loading user data", e);
        }
    }

    public @NotNull Component getBanMessageComponent(Player player, LifestealerUser.Ban ban) {
        return MiniMessage.builder().build().deserialize(
                lifestealer.getUserController().getBanSettings().joinMessage(),
                Placeholder.component("player", player.name()),
                Formatter.date("date", ban.endZoned()),
                DurationUtils.formatDurationTag("remaining", ban.remaining()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LifestealerUser user = lifestealer.getUserManager().getOnlineUser(player);

        user.readLock();
        try {
            lifestealer.getUserController().updatePlayerHearts(player, user);
        } finally {
            user.readUnlock();
        }
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

        int hearts = lifestealer.getItemManager().getHearts(item);
        if (hearts == 0) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        LifestealerUser user = lifestealer.getUserManager().getOnlineUser(player);

        LifestealerPreConsumeHeartEvent preConsumeHeartEvent = new LifestealerPreConsumeHeartEvent(player, user, item, hearts);
        if (!preConsumeHeartEvent.callEvent()) {
            return;
        }

        hearts = preConsumeHeartEvent.getAmount(); // developers can change the amount of hearts

        LifestealerUserController.ChangeHeartsResult result;
        user.writeLock();
        try {
            LifestealerUserRules rules = lifestealer.getUserController().computeUserRules(player, user);

            if (lifestealer.getUserController().isOverflowing(user, rules, hearts) && !player.isSneaking()) {
                LifestealerMessages.CONSUME_HEART_OVERFLOW_NOT_SNEAKING.sendTo(player,
                        Formatter.number("overflow", lifestealer.getUserController().getOverflowAmount(user, rules, hearts))
                );
                return;
            }

            result = lifestealer.getUserController().addHearts(user, rules, hearts);
        } finally {
            user.writeUnlock();
        }

        if (!result.hasChanged()) {
            LifestealerMessages.CONSUME_HEART_ALREADY_FULL.sendTo(player);
            return;
        }

        user.readLock();
        try {
            lifestealer.getUserController().updatePlayerHearts(player, user);
        } finally {
            user.readUnlock();
        }

        item.subtract();
        LifestealerMessages.CONSUME_HEART_SUCCESS.sendTo(player, Formatter.number("amount", result.difference()));

        LifestealerPostConsumeHeartEvent postConsumeHeartEvent = new LifestealerPostConsumeHeartEvent(player, user, hearts, item, result);
        postConsumeHeartEvent.callEvent();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }

        Player player = event.getEntity();
        LifestealerHeartItem itemToDropWhenPlayerDies = lifestealer.getItemManager().getItemToDropWhenPlayerDies();

        ItemStack itemStack = lifestealer.getItemManager().generateItem(itemToDropWhenPlayerDies);
        int hearts = lifestealer.getItemManager().getHearts(itemStack);

        LifestealerUser user = lifestealer.getUserManager().getOnlineUser(player);
        LifestealerPrePlayerDeathEvent prePlayerDeathEvent = new LifestealerPrePlayerDeathEvent(event, player, user, itemStack, hearts);
        if (!prePlayerDeathEvent.callEvent()) {
            return;
        }

        hearts = prePlayerDeathEvent.getHeartsToRemove();
        itemStack = prePlayerDeathEvent.getItemStackToDrop();

        LifestealerHeartDropAction action = lifestealer.getHeartDropRestrictionManager().evaluateHeartDropAction(player, user, event);
        if (action.shouldDropItem()) {
            event.getDrops().add(itemStack);
        }

        if (!action.shouldRemoveHearts()) {
            return;
        }

        LifestealerUser.Ban ban = null;

        user.writeLock();
        try {
            LifestealerUserRules rules = lifestealer.getUserController().computeUserRules(player, user);
            LifestealerUserController.ChangeHeartsResult result = lifestealer.getUserController().removeHearts(user, rules, hearts);
            lifestealer.getUserController().updatePlayerHearts(player, user);

            LifestealerPostPlayerDeathEvent postPlayerDeathEvent = new LifestealerPostPlayerDeathEvent(event, player, user, itemStack, hearts, result);
            postPlayerDeathEvent.callEvent();

            int heartsWithoutClamp = result.previousHearts() - hearts;

            if (heartsWithoutClamp < rules.minHearts()) { // if the player would have less than the minimum amount of hearts
                Duration banDuration = rules.banTime();

                LifestealerPreUserBanEvent banEvent = new LifestealerPreUserBanEvent(player, user, banDuration);
                if (!banEvent.callEvent()) {
                    return;
                }

                banDuration = banEvent.getBanDuration();

                ban = lifestealer.getUserController().setUserBanned(user, banDuration);
            }
        } finally {
            user.writeUnlock();
        }

        if (ban != null) {
            lifestealer.getUserController().postBanOperations(player, user, ban);
            Duration banDuration = ban.duration();
            Bukkit.getGlobalRegionScheduler().execute(this.lifestealer, () -> {
                lifestealer.getUserController().executeBanCommands(player, banDuration);
            });
        }
    }
}
