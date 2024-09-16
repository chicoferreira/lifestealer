package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageProperties;
import dev.chicoferreira.lifestealer.user.persistent.UserPersistentStorageType;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.file.FileConnectionProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.nio.file.Path;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class StoragePropertiesSerializer implements TypeDeserializer<UserPersistentStorageProperties> {

    private final Path basePath;

    public StoragePropertiesSerializer(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public UserPersistentStorageProperties deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        String databaseTypeName = require(node.node("type"), String.class);
        return switch (databaseTypeName) {
            case "h2" ->
                    new FileConnectionProperties(UserPersistentStorageType.H2, basePath.resolve(require(node.node("path"), String.class)));
            case "sqlite" ->
                    new FileConnectionProperties(UserPersistentStorageType.SQLITE, basePath.resolve(require(node.node("path"), String.class)));
            case "mariadb", "mysql", "postgresql" -> require(node, SQLHikariConnectionProperties.class);
            default -> throw new SerializationException("Database type " + databaseTypeName + " is invalid");
        };
    }
}
