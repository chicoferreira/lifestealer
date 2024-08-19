package dev.chicoferreira.lifestealer.command;

import dev.chicoferreira.lifestealer.LifestealerController;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.LifestealerUser;
import dev.chicoferreira.lifestealer.LifestealerUserManager;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LifestealerCommand {

    private final LifestealerController controller;
    private final LifestealerUserManager userManager;
    private final LifestealerHeartItemManager itemManager;

    public LifestealerCommand(LifestealerController controller, LifestealerUserManager userManager, LifestealerHeartItemManager itemManager) {
        this.controller = controller;
        this.userManager = userManager;
        this.itemManager = itemManager;
    }

    public void subcommandHeartsSet(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        LifestealerController.ChangeHeartsResult result = this.controller.setHearts(target, user, amount);

        LifestealerMessages.COMMAND_HEARTS_SET_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                Formatter.number("new", result.newHearts()),
                Formatter.number("previous", result.previousHearts()));

        LifestealerMessages.COMMAND_HEARTS_SET_SUCCESS_TARGET.sendTo(target,
                Formatter.number("new", result.newHearts()),
                Formatter.number("previous", result.previousHearts()));
    }

    public void subcommandHeartsAdd(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        LifestealerController.ChangeHeartsResult result = this.controller.addHearts(target, user, amount);

        LifestealerMessages.COMMAND_HEARTS_ADD_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                Formatter.number("added", result.difference()),
                Formatter.number("new", result.newHearts()),
                Formatter.number("previous", result.previousHearts()));

        LifestealerMessages.COMMAND_HEARTS_ADD_SUCCESS_TARGET.sendTo(target,
                Formatter.number("added", result.difference()),
                Formatter.number("new", result.newHearts()),
                Formatter.number("previous", result.previousHearts()));
    }

    public void subcommandHeartsRemove(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        LifestealerController.ChangeHeartsResult result = this.controller.removeHearts(target, user, amount);

        LifestealerMessages.COMMAND_HEARTS_REMOVE_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                Formatter.number("removed", -result.difference()),
                Formatter.number("new", result.newHearts()),
                Formatter.number("previous", result.previousHearts()));

        LifestealerMessages.COMMAND_HEARTS_REMOVE_SUCCESS_TARGET.sendTo(target,
                Formatter.number("removed", -result.difference()),
                Formatter.number("new", result.newHearts()),
                Formatter.number("previous", result.previousHearts()));
    }

    public void subcommandItemGive(CommandSender sender, LifestealerHeartItem item, int amount, Player target) {
        int rest = itemManager.giveHeartItems(target, item, amount);
        int given = amount - rest;

        LifestealerMessages.COMMAND_ITEM_GIVE_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                Placeholder.unparsed("item", item.typeName()),
                Formatter.number("amount", given),
                Formatter.number("rest", rest));

        LifestealerMessages.COMMAND_ITEM_GIVE_SUCCESS_TARGET.sendTo(target,
                Placeholder.unparsed("item", item.typeName()),
                Formatter.number("amount", given),
                Formatter.number("rest", rest));
    }

    public void subcommandItemTake(CommandSender sender, String itemTypeName, int amount, Player target) {
        int rest = itemManager.takeHeartItems(target, itemTypeName, amount);
        int taken = amount - rest;

        LifestealerMessages.COMMAND_ITEM_TAKE_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                Placeholder.unparsed("item", itemTypeName),
                Formatter.number("amount", taken),
                Formatter.number("rest", rest));

        LifestealerMessages.COMMAND_ITEM_TAKE_SUCCESS_TARGET.sendTo(target,
                Placeholder.unparsed("item", itemTypeName),
                Formatter.number("amount", taken),
                Formatter.number("rest", rest));
    }
}
