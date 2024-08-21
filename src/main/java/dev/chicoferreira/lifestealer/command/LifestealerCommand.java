package dev.chicoferreira.lifestealer.command;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import dev.chicoferreira.lifestealer.user.LifestealerUserManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class LifestealerCommand {

    private final LifestealerUserController controller;
    private final LifestealerUserManager userManager;
    private final LifestealerHeartItemManager itemManager;

    public LifestealerCommand(LifestealerUserController controller, LifestealerUserManager userManager, LifestealerHeartItemManager itemManager) {
        this.controller = controller;
        this.userManager = userManager;
        this.itemManager = itemManager;
    }

    public void subcommandHeartsSet(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getUser(target.getUniqueId());
        LifestealerUserController.ChangeHeartsResult result = this.controller.setHearts(target, user, amount);

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
        LifestealerUserController.ChangeHeartsResult result = this.controller.addHearts(target, user, amount);

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
        LifestealerUserController.ChangeHeartsResult result = this.controller.removeHearts(target, user, amount);

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

    public void subcommandBanUser(CommandSender sender, Player target) {
        LifestealerUser targetUser = this.userManager.getUser(target.getUniqueId());

        LifestealerUser.Ban ban = this.controller.banUser(target, targetUser);
        subcommandBanSendSuccessMessage(sender, target, ban);
    }

    public void subcommandBanUserDuration(CommandSender sender, @NotNull Player target, @NotNull Duration duration) {
        LifestealerUser targetUser = this.userManager.getUser(target.getUniqueId());

        LifestealerUser.Ban ban = this.controller.banUser(target, targetUser, duration);
        subcommandBanSendSuccessMessage(sender, target, ban);
    }

    private void subcommandBanSendSuccessMessage(CommandSender sender, Player target, LifestealerUser.Ban ban) {
        LifestealerMessages.COMMAND_BAN_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                DurationUtils.formatDuration("remaining", ban.duration()),
                Formatter.date("date", ban.endZoned()));
    }

    public void subcommandUnbanUser(CommandSender sender, OfflinePlayer target) {
        LifestealerUser targetUser = this.userManager.getUser(target.getUniqueId());
        Component targetName = this.getPlayerName(target);

        if (!this.controller.isBanned(targetUser)) {
            LifestealerMessages.COMMAND_UNBAN_NOT_BANNED.sendTo(sender, Placeholder.component("target", targetName));
            return;
        }

        this.controller.unbanUser(targetUser);
        LifestealerMessages.COMMAND_UNBAN_SUCCESS.sendTo(sender, Placeholder.component("target", targetName));
    }

    private Component getPlayerName(OfflinePlayer player) {
        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            return onlinePlayer.name();
        }

        String name = player.getName();
        if (name == null) {
            return Component.text(player.getUniqueId().toString());
        }

        return Component.text(name);
    }
}
