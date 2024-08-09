package dev.chicoferreira.lifestealer;

import java.util.UUID;

/**
 * Stores lifesteal information about a player, such has the amount of hearts they have, the heart cap, etc.
 */
public class LifestealerUser {

    private final UUID uuid;
    private int hearts;

    public LifestealerUser(UUID uuid, int hearts) {
        this.uuid = uuid;
        this.hearts = hearts;
    }

    /**
     * @return the UUID of the user
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return the amount of hearts the user has
     */
    public int getHearts() {
        return hearts;
    }

    /**
     * Sets the amount of hearts the user has. Only intended to be used by the {@link LifestealerController}.
     * If you want to set the amount of hearts of a player, use {@link LifestealerController#setHearts(org.bukkit.entity.Player, LifestealerUser, int)}.
     *
     * @param hearts the amount of hearts to set
     */
    void setHearts(int hearts) {
        this.hearts = hearts;
    }
}
