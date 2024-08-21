package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.DurationUtils;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationSerializer implements TypeDeserializer<Duration> {
    @Override
    public Duration deserialize(Type type, ConfigurationNode node) throws SerializationException {
        String string = node.getString();
        if (string == null) {
            return null;
        }

        return DurationUtils.parse(string);
    }
}