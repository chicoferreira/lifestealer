package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.command.LifestealerCommand;
import dev.chicoferreira.lifestealer.command.LifestealerCommandBackend;
import dev.chicoferreira.lifestealer.configuration.LifestealerConfiguration;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestrictionManager;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import dev.chicoferreira.lifestealer.user.LifestealerUserListener;
import dev.chicoferreira.lifestealer.user.LifestealerUserManager;
import dev.chicoferreira.lifestealer.user.persistent.ImportExportStorage;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorage;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageFactory;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageProperties;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesController;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.logging.Level;

public class Lifestealer extends JavaPlugin {

    private LifestealerUserManager userManager;
    private LifestealerUserController userController;
    private LifestealerHeartItemManager itemManager;
    private LifestealerUserRulesController userRulesController;
    private LifestealerHeartDropRestrictionManager heartDropRestrictionManager;
    private LifestealerExecutor executor;
    private UserPersistentStorage userPersistentStorage;
    private ImportExportStorage importExportStorage;
    private LifestealerConfiguration configuration;
    private LifestealerUserListener userListener;

    @Override
    public void onEnable() {
        getLogger().info("hello!");
        this.configuration = new LifestealerConfiguration(this, "config.yml");
        LifestealerConfiguration.Values values;

        try {
            values = this.configuration.loadConfig();
            LifestealerMessages.loadMessages(this.configuration);
            DurationUtils.setFormats(values.durationFormats());
        } catch (SerializationException e) {
            getLogger().log(Level.SEVERE, "Couldn't load config", e);
            getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        UserPersistentStorageProperties connectionProvider = values.storageProperties();
        this.userPersistentStorage = UserPersistentStorageFactory.create(connectionProvider);
        try {
            this.userPersistentStorage.init();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Couldn't initialize user persistent storage", e);
            getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Connected to database (" + this.userPersistentStorage.getDatabaseName() + ")");

        this.importExportStorage = new ImportExportStorage(getDataFolder().toPath(), this.userPersistentStorage);

        this.executor = new LifestealerExecutor(this);

        this.userRulesController = new LifestealerUserRulesController(values.defaultUserRules(), values.userGroupRules());
        this.userManager = new LifestealerUserManager(new HashMap<>(), this.userPersistentStorage, this.executor, values.startingHearts());
        this.userController = new LifestealerUserController(this.userManager, this.userRulesController, values.banSettings());

        this.itemManager = new LifestealerHeartItemManager(values.heartItemSettings());

        LifestealerCommand command = new LifestealerCommand(this);
        LifestealerCommandBackend commandAPIBackend = new LifestealerCommandBackend(command, this);
        commandAPIBackend.registerCommand(this);

        this.heartDropRestrictionManager = new LifestealerHeartDropRestrictionManager(values.heartDropRestrictionActions());

        this.userListener = new LifestealerUserListener(
                this.executor,
                this.getLogger(),
                this.itemManager,
                this.userController,
                this.userManager,
                this.heartDropRestrictionManager,
                values.errorKickMessage()
        );

        Bukkit.getPluginManager().registerEvents(this.userListener, this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifestealerPlaceholderExpansion(this, this.userController).register();
        }

        // Load all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                LifestealerUser user = this.userManager.getOrLoadUser(player.getUniqueId());
                LifestealerUser.Ban ban = this.userController.getBanIfNotExternalSettingEnabled(user);
                if (ban != null) {
                    player.kick(this.userListener.getBanMessageComponent(player, ban));
                }
                this.userController.updatePlayerHearts(player, user);
            } catch (Exception e) {
                player.kick(values.errorKickMessage());
                getLogger().log(Level.SEVERE, "Couldn't load user for player " + player.getName(), e);
            }
        }
    }

    public void reloadConfiguration() throws SerializationException {
        this.configuration.reloadConfig();
        LifestealerConfiguration.Values values = this.configuration.loadConfig();
        LifestealerMessages.loadMessages(this.configuration);
        DurationUtils.setFormats(values.durationFormats());

        this.userRulesController.setDefaultRule(values.defaultUserRules());
        this.userRulesController.setGroupRules(values.userGroupRules());
        this.userController.setBanSettings(values.banSettings());
        this.userManager.setStartingHearts(values.startingHearts());
        this.itemManager.updateSettings(values.heartItemSettings());
        this.heartDropRestrictionManager.setActions(values.heartDropRestrictionActions());
        this.userListener.setErrorKickMessage(values.errorKickMessage());
    }

    @Override
    public void onDisable() {
        try {
            if (this.executor != null) {
                getLogger().info("Waiting for all save tasks to finish...");
                if (!this.executor.shutdown()) {
                    getLogger().warning("Some tasks didn't finish in time, forcing shutdown...");
                }
            }
        } catch (InterruptedException e) {
            getLogger().log(Level.SEVERE, "An error occurred while shutting down", e);
        }

        if (this.userPersistentStorage != null) {
            try {
                this.userPersistentStorage.close();
            } catch (Exception e) {
                getLogger().log(Level.SEVERE, "An error occurred while shutting down database", e);
            }
        }
    }

    /**
     * Returns the user manager instance. You can use this to get a {@link LifestealerUser} instance of a player.
     *
     * @return the user manager
     */
    public LifestealerUserManager getUserManager() {
        return userManager;
    }

    /**
     * Returns the user controller instance. You can use this for many plugin logic things, such as setting the amount of hearts of a player.
     *
     * @return the controller instance
     */
    public LifestealerUserController getUserController() {
        return userController;
    }

    /**
     * Returns the item manager instance. You can use this to get the item stack to drop when a player dies or register new item types.
     *
     * @return the item manager instance
     */
    public LifestealerHeartItemManager getItemManager() {
        return itemManager;
    }

    /**
     * Returns the user rules controller instance. You can use this to get the user rules of the plugin.
     * The rules can be used to get the minimum and maximum amount of hearts a player can have, or their ban time.
     *
     * @return the user rules controller instance
     */
    public LifestealerUserRulesController getUserRulesController() {
        return userRulesController;
    }

    /**
     * Returns the heart drop restriction manager instance. You can use this to compute if a player should drop their hearts when they die.
     *
     * @return the heart drop restriction manager instance
     */
    public LifestealerHeartDropRestrictionManager getHeartDropRestrictionManager() {
        return heartDropRestrictionManager;
    }

    /**
     * Returns the executor instance. You can use this to run tasks in the main or async thread.
     *
     * @return the executor instance
     */
    public LifestealerExecutor getExecutor() {
        return executor;
    }

    /**
     * Returns the user persistent storage instance. You can use this to save and load users from the database.
     *
     * @return the user persistent storage instance
     */
    public UserPersistentStorage getUserPersistentStorage() {
        return userPersistentStorage;
    }

    /**
     * Returns the import/export storage instance. You can use this to import and export users from the database.
     *
     * @return the import/export storage instance
     */
    public ImportExportStorage getImportExportStorage() {
        return importExportStorage;
    }
}
