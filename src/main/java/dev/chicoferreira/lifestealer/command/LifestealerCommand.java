package dev.chicoferreira.lifestealer.command;

import dev.chicoferreira.lifestealer.DurationUtils;
import dev.chicoferreira.lifestealer.LifestealerMessages;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItem;
import dev.chicoferreira.lifestealer.item.LifestealerHeartItemManager;
import dev.chicoferreira.lifestealer.user.LifestealerUser;
import dev.chicoferreira.lifestealer.user.LifestealerUserController;
import dev.chicoferreira.lifestealer.user.LifestealerUserManager;
import dev.chicoferreira.lifestealer.user.rules.LifestealerUserRules;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.TagPattern;
import net.kyori.adventure.text.minimessage.tag.resolver.Formatter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LifestealerCommand {

    private final LifestealerUserController controller;
    private final LifestealerUserManager userManager;
    private final LifestealerHeartItemManager itemManager;
    private final Logger logger;

    public LifestealerCommand(LifestealerUserController controller, LifestealerUserManager userManager, LifestealerHeartItemManager itemManager, Logger logger) {
        this.controller = controller;
        this.userManager = userManager;
        this.itemManager = itemManager;
        this.logger = logger;
    }

    public void subcommandHeartsSet(CommandSender sender, int amount, Player target) {
        LifestealerUser user = this.userManager.getOnlineUser(target);
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
        LifestealerUser user = this.userManager.getOnlineUser(target);
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
        LifestealerUser user = this.userManager.getOnlineUser(target);
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

    public void subcommandItemList(CommandSender sender) {
        List<LifestealerHeartItem> heartItems = this.itemManager.getHeartItems();

        LifestealerMessages.COMMAND_ITEM_LIST_HEADER.sendTo(sender, Formatter.number("amount", heartItems.size()));

        for (LifestealerHeartItem item : heartItems) {
            LifestealerMessages.COMMAND_ITEM_LIST_ITEM.sendTo(sender,
                    Placeholder.unparsed("item", item.typeName()),
                    Formatter.number("amount", item.heartAmount()),
                    Placeholder.component("itemstack", generateItemComponent(item.baseItemStack())));
        }
    }

    private static @NotNull Component generateItemComponent(ItemStack itemStack) {
        return itemStack.displayName().hoverEvent(itemStack).color(NamedTextColor.WHITE);
    }

    public void subcommandBanUser(CommandSender sender, Player target) {
        LifestealerUser targetUser = this.userManager.getOnlineUser(target);

        LifestealerUser.Ban ban = this.controller.banUser(target, targetUser);
        subcommandBanSendSuccessMessage(sender, target, ban);
    }

    public void subcommandBanUserDuration(CommandSender sender, @NotNull Player target, @NotNull Duration duration) {
        LifestealerUser targetUser = this.userManager.getOnlineUser(target);

        LifestealerUser.Ban ban = this.controller.banUser(target, targetUser, duration);
        subcommandBanSendSuccessMessage(sender, target, ban);
    }

    private void subcommandBanSendSuccessMessage(CommandSender sender, Player target, LifestealerUser.Ban ban) {
        LifestealerMessages.COMMAND_BAN_SUCCESS.sendTo(sender,
                Placeholder.component("target", target.name()),
                DurationUtils.formatDurationTag("duration", ban.duration()),
                Formatter.date("date", ban.endZoned()));
    }

    public void subcommandUnbanUser(CommandSender sender, OfflinePlayer target) {
        try {
            LifestealerUser targetUser = this.userManager.getOrLoadUser(target.getUniqueId());
            Component targetName = this.getPlayerName(target);

            if (!this.controller.isBanned(targetUser)) {
                LifestealerMessages.COMMAND_UNBAN_NOT_BANNED.sendTo(sender, Placeholder.component("target", targetName));
                return;
            }

            this.controller.unbanUser(targetUser);
            LifestealerMessages.COMMAND_UNBAN_SUCCESS.sendTo(sender, Placeholder.component("target", targetName));
        } catch (Exception e) {
            LifestealerMessages.COMMAND_ERROR_RETRIEVING_USER.sendTo(sender);
            logger.log(Level.SEVERE, "An error occurred while retrieving user", e);
        }
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

    public void subcommandUserInfo(CommandSender sender, @NotNull OfflinePlayer player) {
        try {
            LifestealerUser user = this.userManager.getOrLoadUser(player.getUniqueId());
            LifestealerUser.Ban ban = this.controller.getBan(user);

            LifestealerUserRules rules = player instanceof Player onlinePlayer
                    ? this.controller.computeUserRules(onlinePlayer, user)
                    : null;

            int modifierMaxHearts = user.getRulesModifier().maxHearts();
            int modifierMinHearts = user.getRulesModifier().minHearts();
            int modifierReturnHearts = user.getRulesModifier().returnHearts();
            Duration modifierBanTime = user.getRulesModifier().banTime();
            LifestealerMessages.COMMAND_USER_INFO.sendTo(sender,
                    Placeholder.component("player", this.getPlayerName(player)),
                    Formatter.number("hearts", user.getHearts()),
                    either("banned", ban != null),
                    DurationUtils.formatDurationTag("remaining", ban != null ? ban.duration() : Duration.ZERO),
                    Formatter.date("date", ban != null ? ban.endZoned() : ZonedDateTime.now()),
                    either("online", player.isOnline() && player instanceof Player),
                    Formatter.number("maxhearts", rules != null ? rules.maxHearts() : 0),
                    Formatter.number("minhearts", rules != null ? rules.minHearts() : 0),
                    Formatter.number("returnhearts", rules != null ? rules.returnHearts() : 0),
                    DurationUtils.formatDurationTag("bantime", rules != null ? rules.banTime() : Duration.ZERO),
                    Formatter.number("modifiermaxhearts", modifierMaxHearts),
                    Formatter.number("modifierminhearts", modifierMinHearts),
                    Formatter.number("modifierreturnhearts", modifierReturnHearts),
                    DurationUtils.formatDurationTag("modifierbantime", modifierBanTime),
                    Formatter.number("permissionmaxhearts", rules != null ? rules.maxHearts() - modifierMaxHearts : 0),
                    Formatter.number("permissionminhearts", rules != null ? rules.minHearts() - modifierMinHearts : 0),
                    Formatter.number("permissionreturnhearts", rules != null ? rules.returnHearts() - modifierReturnHearts : 0),
                    DurationUtils.formatDurationTag("permissionbantime", rules != null ? rules.banTime().minus(modifierBanTime) : Duration.ZERO)
            );
        } catch (Exception e) {
            LifestealerMessages.COMMAND_ERROR_RETRIEVING_USER.sendTo(sender);
            logger.log(Level.SEVERE, "An error occurred while retrieving user", e);
        }
    }

    public enum LifestealerRuleModifier {
        MAXHEARTS("maxhearts"),
        MINHEARTS("minhearts"),
        BANTIME("bantime"),
        RETURNHEARTS("returnhearts");

        private final String rule;

        LifestealerRuleModifier(String rule) {
            this.rule = rule;
        }

        public static @Nullable LifestealerRuleModifier fromName(String modifierName) {
            for (LifestealerRuleModifier modifier : values()) {
                if (modifier.getRule().equalsIgnoreCase(modifierName)) {
                    return modifier;
                }
            }
            return null;
        }

        public String getRule() {
            return rule;
        }
    }

    public void subcommandUserSetRuleModifier(CommandSender sender, OfflinePlayer target, LifestealerRuleModifier rule, int value) {
        try {

            LifestealerUser targetUser = this.userManager.getOrLoadUser(target.getUniqueId());
            LifestealerUserRules modifierRules = targetUser.getRulesModifier();

            LifestealerUserRules newModifierRules = modifierRules.with(builder -> switch (rule) {
                case MAXHEARTS -> builder.maxHearts(value);
                case MINHEARTS -> builder.minHearts(value);
                case BANTIME -> builder.banTime(Duration.ofSeconds(value));
                case RETURNHEARTS -> builder.returnHearts(value);
            });

            this.controller.setRulesModifier(targetUser, newModifierRules);

            LifestealerMessages.COMMAND_USER_SET_RULE_MODIFIER_SUCCESS.sendTo(sender,
                    Placeholder.component("target", this.getPlayerName(target)),
                    Placeholder.component("rule", Component.text(rule.getRule())),
                    Formatter.number("value", value));

            if (target.isOnline() && target instanceof Player targetPlayer) {
                LifestealerMessages.COMMAND_USER_SET_RULE_MODIFIER_SUCCESS_TARGET.sendTo(targetPlayer,
                        Placeholder.component("rule", Component.text(rule.getRule())),
                        Formatter.number("value", value));
            }
        } catch (Exception e) {
            LifestealerMessages.COMMAND_ERROR_RETRIEVING_USER.sendTo(sender);
            logger.log(Level.SEVERE, "An error occurred while retrieving user", e);
        }
    }

    public void subcommandUserAdjustRuleModifier(CommandSender sender, OfflinePlayer target, LifestealerRuleModifier rule, int value) {
        try {
            LifestealerUser targetUser = this.userManager.getOrLoadUser(target.getUniqueId());
            LifestealerUserRules modifierRules = targetUser.getRulesModifier();

            LifestealerUserRules newModifierRules = modifierRules.withSum(builder -> switch (rule) {
                case MAXHEARTS -> builder.maxHearts(value);
                case MINHEARTS -> builder.minHearts(value);
                case BANTIME -> builder.banTime(Duration.ofSeconds(value));
                case RETURNHEARTS -> builder.returnHearts(value);
            });

            this.controller.setRulesModifier(targetUser, newModifierRules);

            String adjustment = value > 0 ? "+" + value : String.valueOf(value);

            LifestealerMessages.COMMAND_USER_ADJUST_RULE_MODIFIER_SUCCESS.sendTo(sender,
                    Placeholder.component("target", this.getPlayerName(target)),
                    Placeholder.component("rule", Component.text(rule.getRule())),
                    Placeholder.unparsed("adjustment", adjustment));

            if (target.isOnline() && target instanceof Player targetPlayer) {
                LifestealerMessages.COMMAND_USER_ADJUST_RULE_MODIFIER_SUCCESS_TARGET.sendTo(targetPlayer,
                        Placeholder.component("rule", Component.text(rule.getRule())),
                        Placeholder.unparsed("adjustment", adjustment));
            }
        } catch (Exception e) {
            LifestealerMessages.COMMAND_ERROR_RETRIEVING_USER.sendTo(sender);
            logger.log(Level.SEVERE, "An error occurred while retrieving user", e);
        }
    }

    public void subcommandUserResetRuleModifiers(CommandSender sender, OfflinePlayer target) {
        try {
            LifestealerUser targetUser = this.userManager.getOrLoadUser(target.getUniqueId());

            LifestealerUserRules newModifierRules = LifestealerUserRules.zeroed();
            this.controller.setRulesModifier(targetUser, newModifierRules);

            LifestealerMessages.COMMAND_USER_RESET_RULE_MODIFIERS_SUCCESS.sendTo(sender,
                    Placeholder.component("target", this.getPlayerName(target)));

            if (target.isOnline() && target instanceof Player targetPlayer) {
                LifestealerMessages.COMMAND_USER_RESET_RULE_MODIFIERS_SUCCESS_TARGET.sendTo(targetPlayer);
            }
        } catch (Exception e) {
            LifestealerMessages.COMMAND_ERROR_RETRIEVING_USER.sendTo(sender);
            logger.log(Level.SEVERE, "An error occurred while retrieving user", e);
        }
    }

    public static TagResolver either(@TagPattern String name, boolean value) {
        return TagResolver.resolver(name, (args, context) -> {
            final String ifFalse = args.popOr("Missing false branch").toString();
            final String ifTrue = args.popOr("Missing true branch").toString();

            return Tag.selfClosingInserting(context.deserialize(value ? ifTrue : ifFalse));
        });
    }
}
