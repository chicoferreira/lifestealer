package dev.chicoferreira.lifestealer.user;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Manages the {@link LifestealerUser} instances and persistence of them.
 */
public class LifestealerUserManager {

    private final Map<UUID, LifestealerUser> users;
    private int startingHearts;

    public LifestealerUserManager(Map<UUID, LifestealerUser> users, int startingHearts) {
        this.users = users;
        this.startingHearts = startingHearts;
    }

    /**
     * Gets a {@link LifestealerUser} instance of the user with the given UUID.
     * If the user does not exist, a new instance is created with the default amount of hearts.
     *
     * @param uuid the UUID of the user
     * @return a {@link LifestealerUser} instance of the user
     */
    public @NotNull LifestealerUser getUser(UUID uuid) {
        return users.computeIfAbsent(uuid, u -> new LifestealerUser(u, getStartingHearts(), null));
    }

    /**
     * Gets the starting amount of hearts for new users.
     *
     * @return the starting amount of hearts for new users
     */
    public int getStartingHearts() {
        return startingHearts;
    }

    /**
     * Sets a new starting amount of hearts for new users.
     *
     * @param startingHearts the starting amount of hearts for new users
     */
    public void setStartingHearts(int startingHearts) {
        this.startingHearts = startingHearts;
    }
}
