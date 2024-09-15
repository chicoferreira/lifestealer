package dev.chicoferreira.lifestealer.user;

import dev.chicoferreira.lifestealer.LifestealerExecutor;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorage;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Manages the {@link LifestealerUser} instances and persistence of them.
 */
public class LifestealerUserManager {

    private final Map<UUID, LifestealerUser> users;
    private final UserPersistentStorage persistentStorage;
    private final LifestealerExecutor executor;
    private int startingHearts;

    public LifestealerUserManager(Map<UUID, LifestealerUser> users, UserPersistentStorage persistentStorage, LifestealerExecutor executor, int startingHearts) {
        this.users = users;
        this.persistentStorage = persistentStorage;
        this.executor = executor;
        this.startingHearts = startingHearts;
    }

    /**
     * Loads a user from the database. This does not cache the user in memory.
     * Can be run asynchronously.
     *
     * @param uuid the uuid of the user to load
     * @return the loaded user or null if the user is not saved in the database
     * @throws Exception if an error occurs while loading the user
     */
    public @Nullable LifestealerUser loadUser(UUID uuid) throws Exception {
        return persistentStorage.loadUser(uuid);
    }

    /**
     * Gets or loads a user from the database synchronously.
     * If the user is not already loaded, it will be loaded from the database and cached in memory.
     * If the user is not saved in the database, a new user will be created and cached in memory.
     * <p>
     * This method is not thread-safe and should only be called from the main thread.
     * If you need to call this method from a different thread, check the example in {@link LifestealerUserListener#onPreLogin(AsyncPlayerPreLoginEvent)}.
     *
     * @param uuid the uuid of the user to get or load
     * @return the user if it's already loaded, otherwise loads it from the database and puts it in memory
     * @throws Exception if an error occurs while loading the user
     */
    public @NotNull LifestealerUser getOrLoadUser(UUID uuid) throws Exception {
        LifestealerUser user = users.get(uuid);
        if (user == null) {
            user = loadUser(uuid);
            if (user != null) {
                users.put(uuid, user);
            }
        }

        if (user == null) {
            user = createUser(uuid);
        }

        return user;
    }

    /**
     * Creates a new user for that UUID with the default values and caches it in memory.
     * This method does not save the user to the database.
     * This method is not thread-safe and should only be called from the main thread.
     *
     * @param uuid the uuid of the user to create
     * @return the created user
     */
    public @NotNull LifestealerUser createUser(UUID uuid) {
        LifestealerUser user = new LifestealerUser(uuid, getStartingHearts(), null, LifestealerUserRules.zeroed());
        users.put(uuid, user);
        return user;
    }

    /**
     * Registers a loaded user to memory.
     * This method is not thread-safe and should only be called from the main thread.
     *
     * @param lifestealerUser the user to register
     */
    public void registerLoadedUser(LifestealerUser lifestealerUser) {
        users.put(lifestealerUser.getUuid(), lifestealerUser);
    }

    /**
     * Gets the {@link LifestealerUser} of a player that is online.
     * This method is guaranteed to return a user if the player is online because of {@link LifestealerUserListener#onPreLogin(AsyncPlayerPreLoginEvent)}.
     *
     * @param player the player to get the user from
     * @return the user of the player
     */
    public @NotNull LifestealerUser getOnlineUser(Player player) {
        // we know that the player already passed the pre login event that fetches the user from the database
        return users.get(player.getUniqueId());
    }

    /**
     * Gets the {@link LifestealerUser} of a player that is online.
     * This method is guaranteed to return a user if that UUID is from a player is that online because of {@link LifestealerUserListener#onPreLogin(AsyncPlayerPreLoginEvent)}.
     * This method is annotated with {@link Nullable} because the UUID might not be from a player that is online.
     *
     * @param uuid the uuid of the user to get
     * @return the user of the player
     */
    public @Nullable LifestealerUser getOnlineUser(UUID uuid) {
        return users.get(uuid);
    }

    /**
     * Checks if a user hasn't changed from the default values.
     *
     * @param user the user to check
     * @return true if the user is the default user, false otherwise
     */
    public boolean isDefaultUser(LifestealerUser user) {
        return user.getHearts() == getStartingHearts()
                && user.getInternalBan() == null
                && user.getRulesModifier().equals(LifestealerUserRules.zeroed());
    }

    /**
     * Saves a user to the database asynchronously.
     * This method is thread-safe and can be called from any thread.
     *
     * @param user the user to save
     * @return a {@link CompletableFuture} that will be completed when the user is saved
     */
    public CompletableFuture<Void> saveUserAsync(LifestealerUser user) {
        return CompletableFuture.runAsync(() -> {
            try {
                saveUserSync(user);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, this.executor.async());
    }

    /**
     * Saves a user to the database synchronously.
     * This method is thread-safe and can be called from any thread, but it will block the current thread.
     *
     * @param user the user to save
     * @throws Exception if an error occurs while saving the user
     */
    public void saveUserSync(LifestealerUser user) throws Exception {
        if (isDefaultUser(user)) {
            persistentStorage.deleteUser(user);
        } else {
            persistentStorage.saveUser(user);
        }
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
     * Sets the starting amount of hearts for new users.
     * Used when reloading the configuration.
     *
     * @param startingHearts the starting amount of hearts for new users
     */
    public void setStartingHearts(int startingHearts) {
        this.startingHearts = startingHearts;
    }
}
