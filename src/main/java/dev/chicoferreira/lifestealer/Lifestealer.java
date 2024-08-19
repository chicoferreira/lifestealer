package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.command.LifestealerCommand;
import dev.chicoferreira.lifestealer.command.LifestealerCommandCommandAPIBackend;
import dev.chicoferreira.lifestealer.configuration.LifestealerConfiguration;
import dev.chicoferreira.lifestealer.heart.LifestealerUserRulesController;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemListener;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.logging.Level;

public class Lifestealer extends JavaPlugin {

    private LifestealerUserManager userManager;
    private LifestealerController controller;
    private LifestealerHeartItemManager itemManager;
    private LifestealerUserRulesController userRulesController;

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
        this.controller = new LifestealerController(this.userRulesController);
        this.userManager = new LifestealerUserManager(new HashMap<>(), values.startingHearts());

        this.itemManager = new LifestealerHeartItemManager(values.heartItems(), values.itemToDropWhenPlayerDies());

        LifestealerCommand command = new LifestealerCommand(this.controller, this.userManager, this.itemManager);
        LifestealerCommandCommandAPIBackend commandAPIBackend = new LifestealerCommandCommandAPIBackend(command, this.itemManager);
        commandAPIBackend.registerCommand(this);

        LifestealerHeartItemListener itemListener = new LifestealerHeartItemListener(this.itemManager, this.controller, this.userManager);
        Bukkit.getPluginManager().registerEvents(itemListener, this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new LifestealerPlaceholderExpansion(this).register();
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
     * Returns the controller instance. You can use this for many plugin logic things, such as setting the amount of hearts of a player.
     *
     * @return the controller instance
     */
    public LifestealerController getController() {
        return controller;
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
}
