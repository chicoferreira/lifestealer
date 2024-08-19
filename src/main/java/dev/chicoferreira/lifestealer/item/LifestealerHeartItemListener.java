package dev.chicoferreira.lifestealer.item;

import dev.chicoferreira.lifestealer.LifestealerController;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.LifestealerUser;
import dev.chicoferreira.lifestealer.LifestealerUserManager;
import dev.chicoferreira.lifestealer.events.LifestealerPostConsumeHeartEvent;
import dev.chicoferreira.lifestealer.events.LifestealerPostPlayerDeathEvent;
import dev.chicoferreira.lifestealer.events.LifestealerPreConsumeHeartEvent;
import dev.chicoferreira.lifestealer.events.LifestealerPrePlayerDeathEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class LifestealerHeartItemListener implements Listener {

    private final LifestealerHeartItemManager heartItemManager;
    private final LifestealerController controller;
    private final LifestealerUserManager userManager;

    public LifestealerHeartItemListener(LifestealerHeartItemManager heartItemManager, LifestealerController controller, LifestealerUserManager userManager) {
        this.heartItemManager = heartItemManager;
        this.controller = controller;
        this.userManager = userManager;
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

        LifestealerController.ChangeHeartsResult result = controller.addHearts(event.getPlayer(), user, hearts);

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

        LifestealerUser user = userManager.getUser(event.getEntity().getUniqueId());

        LifestealerHeartItem itemToDropWhenPlayerDies = heartItemManager.getItemToDropWhenPlayerDies();

        ItemStack itemStack = heartItemManager.generateItem(itemToDropWhenPlayerDies);
        int hearts = heartItemManager.getHearts(itemStack);

        LifestealerPrePlayerDeathEvent prePlayerDeathEvent = new LifestealerPrePlayerDeathEvent(event, event.getEntity(), user, itemStack, hearts);
        if (!prePlayerDeathEvent.callEvent()) {
            return;
        }

        hearts = prePlayerDeathEvent.getHeartsToRemove();
        itemStack = prePlayerDeathEvent.getItemStackToDrop();

        event.getDrops().add(itemStack);

        LifestealerController.ChangeHeartsResult result = controller.removeHearts(event.getEntity(), user, hearts);

        LifestealerPostPlayerDeathEvent postPlayerDeathEvent = new LifestealerPostPlayerDeathEvent(event, event.getEntity(), user, itemStack, hearts, result);
        postPlayerDeathEvent.callEvent();
        // TODO: ban the player if the player died with the minimum amount of hearts
    }
}
