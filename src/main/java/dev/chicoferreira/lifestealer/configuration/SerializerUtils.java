package dev.chicoferreira.lifestealer.configuration;

import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SerializerUtils {
    public static <V> @NotNull V require(ConfigurationNode node, Class<V> clazz) throws SerializationException {
        if (node.virtual()) {
            String keyPath = Arrays.stream(node.path().array())
                    .map(Object::toString)
                    .collect(Collectors.joining("."));
            throw new NoSuchElementException("Required key '" + keyPath + "' (" + clazz.getSimpleName() + ") is missing");
        }
        V value = node.get(clazz);
        if (value == null) {
            String keyPath = Arrays.stream(node.path().array())
                    .map(Object::toString)
                    .collect(Collectors.joining("."));
            throw new IllegalArgumentException("Invalid type for key '" + keyPath + "' (" + clazz.getSimpleName() + ")");
        }
        return value;
    }
}
