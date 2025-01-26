package dev.chicoferreira.lifestealer.user;

import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Stores lifesteal information about a player, such has the amount of hearts they have, the heart cap, etc.
 */
@ConfigSerializable
public class LifestealerUser {

    private final @NotNull UUID uuid;
    private int hearts;
    private @Nullable Ban ban;
    private @NotNull LifestealerUserRules modifierRules;

    public LifestealerUser(@NotNull UUID uuid, int hearts, @Nullable Ban ban, @NotNull LifestealerUserRules modifierRules) {
        this.uuid = uuid;
        this.hearts = hearts;
        this.ban = ban;
        this.modifierRules = modifierRules;
    }

    /**
     * @return the UUID of the user
     */
    public @NotNull UUID getUuid() {
        return uuid;
    }

    /**
     * @return the amount of hearts the user has
     */
    public int getHearts() {
        return hearts;
    }

    /**
     * Gets the ban information of the user if they are banned, otherwise null.
     * This method is only intended to be used by the {@link LifestealerUserController}.
     * If you want to check if a player is banned, use {@link LifestealerUserController#getBan(LifestealerUser)}.
     *
     * @return the ban information of the user if they have a banning record, otherwise null
     */
    public @Nullable Ban getInternalBan() {
        return ban;
    }

    /**
     * Sets the ban information of the user.
     * Only intended to be used by the {@link LifestealerUserController} as this will not save the changes to the database.
     *
     * @param ban the ban information of the user
     */
    void setBan(@Nullable Ban ban) {
        this.ban = ban;
    }

    /**
     * Sets the amount of hearts the user has.
     * Only intended to be used by the {@link LifestealerUserController} as this will not save the changes to the database.
     * If you want to set the amount of hearts of a player, use {@link LifestealerUserController#setHearts(Player, LifestealerUser, int)}.
     *
     * @param hearts the amount of hearts to set
     */
    void setHearts(int hearts) {
        this.hearts = hearts;
    }


    /**
     * Gets the modifier rules of the user.
     * This rule will have its values summed with the values in the rule given
     * by the {@link dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesController}
     * which is based on the user's permissions to get the final rule value.
     * <p>
     * Use {@link LifestealerUserController#computeUserRules(Player, LifestealerUser)} to get the final rule value.
     */
    public @NotNull LifestealerUserRules getRulesModifier() {
        return modifierRules;
    }

    /**
     * Sets the modifier for the rules of the user internally without saving to the database.
     * Only intended to be used by the {@link LifestealerUserController} as this will not save the changes to the database.
     * If you want to change the rules modifier of a player, use {@link LifestealerUserController#setRulesModifier(LifestealerUser, LifestealerUserRules)}.
     *
     * @param rulesModifier the modifier rules of the user
     */
    void setRulesModifier(@NotNull LifestealerUserRules rulesModifier) {
        this.modifierRules = rulesModifier;
    }

    /**
     * A record containing information about a ban.
     *
     * @param start    the start of the ban
     * @param duration the duration of the ban
     */
    public record Ban(@NotNull Instant start, @NotNull Duration duration) {
        /**
         * Gets the end {@link Instant} of the ban.
         *
         * @return the end of the ban
         */
        public Instant end() {
            return start.plus(duration);
        }

        /**
         * Gets the end {@link ZonedDateTime} of the ban in the given zone id.
         *
         * @param zoneId the zone id to convert the end to
         * @return the end {@link ZonedDateTime} of the ban in the given zone id
         */
        public ZonedDateTime endZoned(ZoneId zoneId) {
            return end().atZone(zoneId);
        }

        /**
         * Gets the end {@link ZonedDateTime} of the ban in the system default zone id.
         *
         * @return the end {@link ZonedDateTime} of the ban in the system default zone id
         */
        public ZonedDateTime endZoned() {
            return endZoned(ZoneId.systemDefault());
        }

        /**
         * Checks if the ban is still active (the remaining time is greater than 0).
         *
         * @return true if the ban is still active, otherwise false
         */
        public boolean isBanActive() {
            return Instant.now().isBefore(end());
        }

        /**
         * Gets the remaining {@link Duration} of the ban.
         *
         * @return the remaining time of the ban
         */
        public Duration remaining() {
            return Duration.between(Instant.now(), end());
        }
    }
}
