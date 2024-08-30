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
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRulesController;
import org.bukkit.Bukkit;
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

    @Override
    public void onEnable() {
        getLogger().info("hello!");
        LifestealerConfiguration configuration = new LifestealerConfiguration(this, "config.yml");
        LifestealerConfiguration.Values values;

        try {
            values = configuration.loadConfig();
            LifestealerMessages.loadMessages(configuration);
        } catch (SerializationException e) {
            getLogger().log(Level.SEVERE, "Couldn't load config", e);
            getLogger().severe("Disabling plugin...");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        this.userRulesController = new LifestealerUserRulesController(values.defaultUserRules(), values.userGroupRules());
        this.userController = new LifestealerUserController(this.userRulesController, values.banSettings());
        this.userManager = new LifestealerUserManager(new HashMap<>(), values.startingHearts());

        this.itemManager = new LifestealerHeartItemManager(values.heartItems(), values.itemToDropWhenPlayerDies());

        LifestealerCommand command = new LifestealerCommand(this.userController, this.userManager, this.itemManager);
        LifestealerCommandCommandAPIBackend commandAPIBackend = new LifestealerCommandCommandAPIBackend(command, this.itemManager);
        commandAPIBackend.registerCommand(this);

        this.heartDropRestrictionManager = new LifestealerHeartDropRestrictionManager(values.heartDropRestrictionActions());

        LifestealerUserListener listener = new LifestealerUserListener(this.itemManager,
                this.userController,
                this.userManager,
                this.heartDropRestrictionManager);

        Bukkit.getPluginManager().registerEvents(listener, this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifestealerPlaceholderExpansion(this, this.userController).register();
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
}
