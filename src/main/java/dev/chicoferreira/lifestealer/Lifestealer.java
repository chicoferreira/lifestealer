package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.command.LifestealerCommand;
import dev.chicoferreira.lifestealer.command.LifestealerCommandCommandAPIBackend;
import dev.chicoferreira.lifestealer.configuration.LifestealerConfiguration;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.chicoferreira.lifestealer.restriction.LifestealerHeartDropRestrictionManager;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import dev.chicoferreira.lifestealer.user.LifestealerUserListener;
import dev.chicoferreira.lifestealer.user.LifestealerUserManager;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorage;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLUserPersistentStorage;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesController;
import net.kyori.adventure.text.Component;
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

    @Override
    public void onEnable() {
        getLogger().info("hello!");
        LifestealerConfiguration configuration = new LifestealerConfiguration(this, "config.yml");
        LifestealerConfiguration.Values values;

        try {
            values = configuration.loadConfig();
            LifestealerMessages.loadMessages(configuration);
            DurationUtils.setDurationFormat(values.durationFormat());
        } catch (SerializationException e) {
            getLogger().log(Level.SEVERE, "Couldn't load config", e);
            getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        SQLConnectionProvider connectionProvider = values.connectionProvider();
        this.userPersistentStorage = new SQLUserPersistentStorage(connectionProvider);
        try {
            this.userPersistentStorage.init();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Couldn't initialize user persistent storage", e);
            getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        getLogger().info("Connected to database (" + this.userPersistentStorage.getDatabaseName() + ")");

        this.executor = new LifestealerExecutor(this);

        this.userRulesController = new LifestealerUserRulesController(values.defaultUserRules(), values.userGroupRules());
        this.userManager = new LifestealerUserManager(new HashMap<>(), this.userPersistentStorage, this.executor, values.startingHearts());
        this.userController = new LifestealerUserController(this.userManager, this.userRulesController, values.banSettings());

        this.itemManager = new LifestealerHeartItemManager(values.heartItems(), values.itemToDropWhenPlayerDies());

        LifestealerCommand command = new LifestealerCommand(this.userController, this.userManager, this.itemManager, this.getLogger());
        LifestealerCommandCommandAPIBackend commandAPIBackend = new LifestealerCommandCommandAPIBackend(command, this.itemManager);
        commandAPIBackend.registerCommand(this);

        this.heartDropRestrictionManager = new LifestealerHeartDropRestrictionManager(values.heartDropRestrictionActions());

        LifestealerUserListener listener = new LifestealerUserListener(
                this.executor,
                this.getLogger(),
                this.itemManager,
                this.userController,
                this.userManager,
                this.heartDropRestrictionManager,
                values.errorKickMessage()
        );

        Bukkit.getPluginManager().registerEvents(listener, this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifestealerPlaceholderExpansion(this, this.userController).register();
        }

        // Load all online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            try {
                this.userManager.getOrLoadUser(onlinePlayer.getUniqueId());
            } catch (Exception e) {
                onlinePlayer.kick(Component.text("An error occurred while loading your user."));
                getLogger().log(Level.SEVERE, "Couldn't load user for player " + onlinePlayer.getName(), e);
            }
        }
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
}
