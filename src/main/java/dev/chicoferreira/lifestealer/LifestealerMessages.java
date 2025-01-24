package dev.chicoferreira.lifestealer;

import dev.chicoferreira.lifestealer.configuration.LifestealerConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.spongepowered.configurate.serialize.SerializationException;

public enum LifestealerMessages {

    COMMAND_HEARTS_SET_SUCCESS,
    COMMAND_HEARTS_SET_SUCCESS_TARGET,
    COMMAND_HEARTS_ADD_SUCCESS,
    COMMAND_HEARTS_ADD_SUCCESS_TARGET,
    COMMAND_HEARTS_REMOVE_SUCCESS,
    COMMAND_HEARTS_REMOVE_SUCCESS_TARGET,
    COMMAND_ITEM_GIVE_SUCCESS,
    COMMAND_ITEM_GIVE_SUCCESS_TARGET,
    COMMAND_ITEM_TAKE_SUCCESS,
    COMMAND_ITEM_TAKE_SUCCESS_TARGET,
    COMMAND_ITEM_LIST_HEADER,
    COMMAND_ITEM_LIST_ITEM,
    CONSUME_HEART_SUCCESS,
    CONSUME_HEART_ALREADY_FULL,
    CONSUME_HEART_OVERFLOW_NOT_SNEAKING,
    COMMAND_BAN_SUCCESS,
    COMMAND_UNBAN_NOT_BANNED,
    COMMAND_UNBAN_SUCCESS,
    COMMAND_USER_INFO,
    COMMAND_USER_SET_RULE_MODIFIER_SUCCESS,
    COMMAND_USER_ADJUST_RULE_MODIFIER_SUCCESS,
    COMMAND_USER_RESET_RULE_MODIFIERS_SUCCESS,
    COMMAND_USER_SET_RULE_MODIFIER_SUCCESS_TARGET,
    COMMAND_USER_ADJUST_RULE_MODIFIER_SUCCESS_TARGET,
    COMMAND_USER_RESET_RULE_MODIFIERS_SUCCESS_TARGET,
    COMMAND_ERROR_RETRIEVING_USER,
    COMMAND_RELOAD_SUCCESS,
    COMMAND_RELOAD_ERROR,
    COMMAND_STORAGE_IMPORT_SUCCESS,
    COMMAND_STORAGE_IMPORT_ERROR,
    COMMAND_STORAGE_EXPORT_SUCCESS,
    COMMAND_STORAGE_EXPORT_ERROR;

    private PlayerNotification message;

    public void sendTo(CommandSender sender, TagResolver... resolvers) {
        message.sendTo(sender, TagResolver.resolver(resolvers));
    }

    public static void loadMessages(LifestealerConfiguration configuration) throws SerializationException {
        for (LifestealerMessages message : values()) {
            String configPath = message.name().toLowerCase().replace("_", " ");
            message.message = configuration.getPlayerNotification(configPath);
        }
    }
}
