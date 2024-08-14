package dev.chicoferreira.lifestealer;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;

/**
 * Manages the {@link LifestealerUser} instances and persistence of them.
 */
public class LifestealerUserManager {

    private final Map<UUID, LifestealerUser> users;

    public LifestealerUserManager(Map<UUID, LifestealerUser> users) {
        this.users = users;
    }

    /**
     * Gets a {@link LifestealerUser} instance of the user with the given UUID.
     * If the user does not exist, a new instance is created with the default amount of hearts.
     *
     * @param uuid the UUID of the user
     * @return a {@link LifestealerUser} instance of the user
     */
    public @NotNull LifestealerUser getUser(UUID uuid) {
        return users.computeIfAbsent(uuid, u -> new LifestealerUser(u, LifestealerSettings.DEFAULT_HEARTS));
    }
}
