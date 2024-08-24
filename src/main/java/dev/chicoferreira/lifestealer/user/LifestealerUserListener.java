package dev.chicoferreira.lifestealer.user;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.events.*;
import dev.chicoferreira.lifestealer.heart.LifestealerUserRules;
import dev.chicoferreira.lifestealer.heart.LifestealerUserRulesController;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public class LifestealerUserListener implements Listener {

    private final LifestealerUserManager userManager;
    private final LifestealerHeartItemManager heartItemManager;
    private final LifestealerUserController userController;
    private final LifestealerUserRulesController userRulesController;

    public LifestealerUserListener(LifestealerHeartItemManager heartItemManager, LifestealerUserController userController, LifestealerUserManager userManager, LifestealerUserRulesController userRulesController) {
        this.heartItemManager = heartItemManager;
        this.userController = userController;
        this.userManager = userManager;
        this.userRulesController = userRulesController;
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        LifestealerUser user = userManager.getUser(player.getUniqueId());

        if (!userController.getBanSettings().external()) {
            LifestealerUser.Ban ban = userController.getBan(user);

            if (ban != null) {
                Component kickMessageComponent = MiniMessage.builder().build().deserialize(
                        userController.getBanSettings().joinMessage(),
                        Placeholder.component("player", player.name()),
                        Formatter.date("date", ban.endZoned()),
                        DurationUtils.formatDuration("remaining", ban.remaining()));
                event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessageComponent);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        LifestealerUser user = userManager.getUser(player.getUniqueId());

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

        LifestealerUser user = userManager.getUser(event.getPlayer().getUniqueId());

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
        LifestealerUser user = userManager.getUser(player.getUniqueId());

        LifestealerHeartItem itemToDropWhenPlayerDies = heartItemManager.getItemToDropWhenPlayerDies();

        ItemStack itemStack = heartItemManager.generateItem(itemToDropWhenPlayerDies);
        int hearts = heartItemManager.getHearts(itemStack);

        LifestealerPrePlayerDeathEvent prePlayerDeathEvent = new LifestealerPrePlayerDeathEvent(event, player, user, itemStack, hearts);
        if (!prePlayerDeathEvent.callEvent()) {
            return;
        }

        hearts = prePlayerDeathEvent.getHeartsToRemove();
        itemStack = prePlayerDeathEvent.getItemStackToDrop();

        event.getDrops().add(itemStack);

        LifestealerUserController.ChangeHeartsResult result = userController.removeHearts(player, user, hearts);

        LifestealerPostPlayerDeathEvent postPlayerDeathEvent = new LifestealerPostPlayerDeathEvent(event, player, user, itemStack, hearts, result);
        postPlayerDeathEvent.callEvent();

        int heartsWithoutClamp = result.previousHearts() - hearts;

        LifestealerUserRules rules = userRulesController.computeRules(player::hasPermission);

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
