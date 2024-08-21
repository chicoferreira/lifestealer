package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.user.LifestealerUserController.BanSettings;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.util.List;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class BanSettingsSerializer implements TypeDeserializer<BanSettings> {
    @Override
    public BanSettings deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String kickMessage = require(node.node("kick message"), String.class);
        String joinMessage = require(node.node("join message"), String.class);
        List<String> commands = node.node("commands").getList(String.class);
        boolean external = node.node("external").getBoolean(false);
        return new BanSettings(kickMessage, joinMessage, commands, external);
    }
}
