package dev.chicoferreira.lifestealer.restriction.restrictions;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a restriction based on the player's current world name
 */
public class WorldSpecificHeartDropRestriction implements LifestealerHeartDropRestriction {

    private final String worldName;

    public WorldSpecificHeartDropRestriction(String worldName) {
        this.worldName = worldName;
    }

    @Override
    public boolean shouldRestrictHeartDrop(@NotNull Player player, @NotNull LifestealerUser user, @NotNull PlayerDeathEvent event) {
        return player.getWorld().getName().equals(worldName);
    }
}
