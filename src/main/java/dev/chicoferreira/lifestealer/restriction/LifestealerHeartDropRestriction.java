package dev.chicoferreira.lifestealer.restriction;

import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a restriction based on the player's death event
 */
public interface LifestealerHeartDropRestriction {

    /**
     * Checks if this restriction applies to the player
     *
     * @param player the player that died
     * @param user   the user related to the player
     * @param event  the death event
     * @return if this restriction applies to the player
     */
    boolean shouldRestrictHeartDrop(@NotNull Player player, @NotNull LifestealerUser user, @NotNull PlayerDeathEvent event);

}
