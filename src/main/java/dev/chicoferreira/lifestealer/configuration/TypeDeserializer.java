package dev.chicoferreira.lifestealer.configuration;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.TypeSerializer;

import java.lang.reflect.Type;

/**
 * A utility interface for {@link TypeSerializer} that already has a default implementation for serialization
 * Used in the other serializers class to already implement the serialize method as we only need the deserialize method
 *
 * @param <T> The type to deserialize
 */
public interface TypeDeserializer<T> extends TypeSerializer<T> {

    @Override
    default void serialize(@NotNull Type type, @Nullable T obj, @NotNull ConfigurationNode node) {
        throw new UnsupportedOperationException();
    }
}
