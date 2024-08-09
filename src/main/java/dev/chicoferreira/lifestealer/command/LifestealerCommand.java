package dev.chicoferreira.lifestealer.command;

import dev.chicoferreira.lifestealer.LifestealerController;
import dev.chicoferreira.lifestealer.LifestealerUser;
import dev.chicoferreira.lifestealer.LifestealerUserManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifestealerCommand {

    private final LifestealerController controller;
    private final LifestealerUserManager userManager;

    public LifestealerCommand(LifestealerController controller, LifestealerUserManager userManager) {
        this.controller = controller;
        this.userManager = userManager;
    }

    public void subcommandHeartsSet(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        this.controller.setHearts(target, user, amount);

        sender.sendMessage("Set " + target.getName() + "'s health to " + amount + " hearts.");
    }

    public void subcommandHeartsAdd(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        int hearts = this.controller.addHearts(target, user, amount);

        sender.sendMessage("Added " + amount + " hearts to " + target.getName() + "'s health. New health: " + hearts + " hearts.");
    }

    public void subcommandHeartsRemove(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        int hearts = this.controller.removeHearts(target, user, amount);

        sender.sendMessage("Removed " + amount + " hearts from " + target.getName() + "'s health. New health: " + hearts + " hearts.");
    }
}
