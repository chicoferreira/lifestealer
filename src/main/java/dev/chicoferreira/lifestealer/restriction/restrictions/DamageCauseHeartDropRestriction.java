package dev.chicoferreira.lifestealer.restriction.restrictions;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Required;
import org.spongepowered.configurate.objectmapping.meta.Setting;

/**
 * Represents a restriction based on the player's death cause
 */
@ConfigSerializable
public record DamageCauseHeartDropRestriction(
        @Setting(value = "cause") @Required EntityDamageEvent.DamageCause damageCause)
        implements LifestealerHeartDropRestriction {

    @Override
    public boolean shouldRestrictHeartDrop(@NotNull Player player, @NotNull LifestealerUser user, @NotNull PlayerDeathEvent event) {
        EntityDamageEvent lastDamageCause = player.getLastDamageCause();
        if (lastDamageCause == null) {
            return false;
        }

        return lastDamageCause.getCause() == damageCause;
    }
}
