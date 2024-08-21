package dev.chicoferreira.lifestealer.user;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Stores lifesteal information about a player, such has the amount of hearts they have, the heart cap, etc.
 */
public class LifestealerUser {

    private final @NotNull UUID uuid;
    private int hearts;
    private @Nullable Ban ban;

    public LifestealerUser(@NotNull UUID uuid, int hearts, @Nullable Ban ban) {
        this.uuid = uuid;
        this.hearts = hearts;
        this.ban = ban;
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
    @Nullable Ban getBan() {
        return ban;
    }

    /**
     * Sets the ban information of the user.
     * Only intended to be used by the {@link LifestealerUserController}.
     *
     * @param ban the ban information of the user
     */
    void setBan(@Nullable Ban ban) {
        this.ban = ban;
    }

    /**
     * Sets the amount of hearts the user has.
     * Only intended to be used by the {@link LifestealerUserController}.
     * If you want to set the amount of hearts of a player, use {@link LifestealerUserController#setHearts(org.bukkit.entity.Player, LifestealerUser, int)}.
     *
     * @param hearts the amount of hearts to set
     */
    void setHearts(int hearts) {
        this.hearts = hearts;
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
