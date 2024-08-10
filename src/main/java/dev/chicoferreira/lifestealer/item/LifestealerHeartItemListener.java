package dev.chicoferreira.lifestealer.item;

import dev.chicoferreira.lifestealer.LifestealerController;
import dev.chicoferreira.lifestealer.LifestealerUser;
import dev.chicoferreira.lifestealer.LifestealerUserManager;
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

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onItemRightClick(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        int hearts = heartItemManager.getHearts(item);
        if (hearts == 0) {
            return;
        }

        LifestealerUser user = userManager.getUser(event.getPlayer().getUniqueId());

        item.subtract();
        controller.addHearts(event.getPlayer(), user, hearts);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getKeepInventory()) {
            return;
        }

        LifestealerUser user = userManager.getUser(event.getEntity().getUniqueId());

        ItemStack itemToDropWhenPlayerDies = heartItemManager.getItemStackToDropWhenPlayerDies();
        event.getDrops().add(itemToDropWhenPlayerDies);

        int hearts = heartItemManager.getHearts(itemToDropWhenPlayerDies);

        controller.removeHearts(event.getEntity(), user, hearts); // remove the amount of hearts dropped
    }
}
