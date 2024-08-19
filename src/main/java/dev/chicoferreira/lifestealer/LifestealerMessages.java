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
    CONSUME_HEART_SUCCESS,
    CONSUME_HEART_ALREADY_FULL;

    private PlayerNotification message;

    public void sendTo(CommandSender sender, TagResolver... resolvers) {
        message.sendTo(sender, resolvers);
    }

    public static void loadMessages(LifestealerConfiguration configuration) throws SerializationException {
        for (LifestealerMessages message : values()) {
            String configPath = message.name().toLowerCase().replace("_", " ");
            message.message = configuration.getPlayerNotification(configPath);
        }
    }
}
