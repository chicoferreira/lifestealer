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

    public @NotNull LifestealerUser getUser(UUID uuid) {
        return users.computeIfAbsent(uuid, u -> new LifestealerUser(u, Settings.DEFAULT_HEARTS));
    }
}
