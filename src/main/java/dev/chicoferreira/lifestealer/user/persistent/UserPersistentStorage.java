package dev.chicoferreira.lifestealer.user.persistent;

import dev.chicoferreira.lifestealer.user.LifestealerUser;

import java.util.UUID;

/**
 * Interface for user persistent storage
 */
public interface UserPersistentStorage {

    /**
     * Returns a prettified name of the database
     *
     * @return the database name
     */
    String getDatabaseName();

    /**
     * Initializes the database (for example, connecting and creating necessary tables)
     *
     * @throws Exception if an error occurs
     */
    void init() throws Exception;

    /**
     * Closes the database and the database connection
     *
     * @throws Exception if an error occurs
     */
    void close() throws Exception;

    /**
     * Loads a user from the database. This operation is blocking.
     * Use {@link dev.chicoferreira.lifestealer.user.LifestealerUserManager#getOrLoadUser(UUID)} if you
     * want to get the user if it is already loaded or load it from the database if it is not loaded.
     *
     * @param uuid the uuid of the user to load
     * @return the loaded user or null if the user is not saved in the database
     * @throws Exception if an error occurs while loading the user
     */
    LifestealerUser loadUser(UUID uuid) throws Exception;

    /**
     * Saves a user to the database.
     * This operation is blocking. Use {@link dev.chicoferreira.lifestealer.user.LifestealerUserManager#saveUserAsync(LifestealerUser)}
     * if you want to save the user asynchronously.
     *
     * @param user the user to save
     * @throws Exception if an error occurs while saving the user
     */
    void saveUser(LifestealerUser user) throws Exception;

}
