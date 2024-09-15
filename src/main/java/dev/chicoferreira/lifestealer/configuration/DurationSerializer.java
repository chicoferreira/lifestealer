package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.DurationUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;

import java.lang.reflect.Type;
import java.time.Duration;

public class DurationSerializer implements TypeDeserializer<Duration> {
    @Override
    public Duration deserialize(@NotNull Type type, ConfigurationNode node) {
        String string = node.getString();
        if (string == null) {
            return null;
        }

        return DurationUtils.parse(string);
    }
}