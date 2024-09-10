package dev.chicoferreira.lifestealer.restriction.restrictions;

import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestriction;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

/**
 * Represents a restriction based on the killer having the same IP as the player
 */
@ConfigSerializable
public class SameIpReasonHeartDropRestriction implements LifestealerHeartDropRestriction {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public boolean shouldRestrictHeartDrop(@NotNull Player player, @NotNull LifestealerUser user, @NotNull PlayerDeathEvent event) {
        DamageSource damageSource = event.getDamageSource();
        Entity causingEntity = damageSource.getCausingEntity();

        if (!(causingEntity instanceof Player damager)) {
            return false;
        }

        if (damager.getAddress() == null || player.getAddress() == null) {
            return false;
        }

        return damager.getAddress().getHostString().equals(player.getAddress().getHostString());
    }
}
