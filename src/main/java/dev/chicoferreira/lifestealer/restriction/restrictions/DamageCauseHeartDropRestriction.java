package dev.chicoferreira.lifestealer.restriction.restrictions;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a restriction based on the player's death cause
 */
public class DamageCauseHeartDropRestriction implements LifestealerHeartDropRestriction {

    private final EntityDamageEvent.DamageCause damageCause;

    public DamageCauseHeartDropRestriction(EntityDamageEvent.DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    @Override
    public boolean shouldRestrictHeartDrop(@NotNull Player player, @NotNull LifestealerUser user, @NotNull PlayerDeathEvent event) {
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause == null) {
            return false;
        }

        return lastDamageCause.getCause() == damageCause;
    }
}
