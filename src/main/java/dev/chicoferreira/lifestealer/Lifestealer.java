package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.command.LifestealerCommand;
import dev.chicoferreira.lifestealer.command.LifestealerCommandCommandAPIBackend;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class Lifestealer extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("hello!");
        LifestealerController controller = new LifestealerController();
        LifestealerUserManager userManager = new LifestealerUserManager(new HashMap<>());

        LifestealerCommand command = new LifestealerCommand(controller, userManager);
        LifestealerCommandCommandAPIBackend commandAPIBackend = new LifestealerCommandCommandAPIBackend(command);
        commandAPIBackend.registerCommand(this);
    }
}
