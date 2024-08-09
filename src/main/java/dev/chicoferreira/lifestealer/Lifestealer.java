package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.command.LifestealerCommand;
import dev.chicoferreira.lifestealer.command.LifestealerCommandCommandAPIBackend;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Lifestealer extends JavaPlugin {

    private LifestealerUserManager userManager;
    private LifestealerController controller;

    @Override
    public void onEnable() {
        getLogger().info("hello!");
        this.controller = new LifestealerController();
        this.userManager = new LifestealerUserManager(new HashMap<>());

        LifestealerCommand command = new LifestealerCommand(this.controller, this.userManager);
        LifestealerCommandCommandAPIBackend commandAPIBackend = new LifestealerCommandCommandAPIBackend(command);
        commandAPIBackend.registerCommand(this);
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
}
