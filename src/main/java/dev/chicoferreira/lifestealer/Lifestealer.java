package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.command.LifestealerCommand;
import dev.chicoferreira.lifestealer.command.LifestealerCommandCommandAPIBackend;
import dev.chicoferreira.lifestealer.configuration.LifestealerConfiguration;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemListener;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Lifestealer extends JavaPlugin {

    private LifestealerUserManager userManager;
    private LifestealerController controller;
    private LifestealerHeartItemManager itemManager;

    @Override
    public void onEnable() {
        getLogger().info("hello!");
        this.controller = new LifestealerController();
        this.userManager = new LifestealerUserManager(new HashMap<>());

        LifestealerConfiguration configuration = new LifestealerConfiguration(this, "config.yml");
        configuration.getConfig();

        this.itemManager = new LifestealerHeartItemManager(configuration.getHeartItems(), "default");

        LifestealerCommand command = new LifestealerCommand(this.controller, this.userManager, this.itemManager);
        LifestealerCommandCommandAPIBackend commandAPIBackend = new LifestealerCommandCommandAPIBackend(command, this.itemManager);
        commandAPIBackend.registerCommand(this);

        LifestealerHeartItemListener itemListener = new LifestealerHeartItemListener(this.itemManager, this.controller, this.userManager);
        Bukkit.getPluginManager().registerEvents(itemListener, this);
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
}
