package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.PlayerNotification;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.title.Title;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.Optional;

public class PlayerNotificationSerializer implements TypeDeserializer<PlayerNotification> {
    @Override
    public PlayerNotification deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String simpleTextMessage = node.getString();
        if (simpleTextMessage != null) {
            return new PlayerNotification(simpleTextMessage);
        }

        String message = node.node("message").getString();
        String actionBarMessage = node.node("action bar").getString();
        String titleMessage = node.node("title").getString();
        String subtitleMessage = node.node("subtitle").getString();
        Title.Times titleTimes = node.node("title times").get(Title.Times.class);
        Sound sound = node.node("sound").get(Sound.class);

        return new PlayerNotification(
                Optional.ofNullable(message),
                Optional.ofNullable(actionBarMessage),
                Optional.ofNullable(titleMessage),
                Optional.ofNullable(subtitleMessage),
                Optional.ofNullable(titleTimes),
                Optional.ofNullable(sound)
        );
    }
}
