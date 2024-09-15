package dev.chicoferreira.lifestealer.configuration;

import dev.chicoferreira.lifestealer.user.persistent.sql.SQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.MariaDBConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.MySQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.PostgreSQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.file.H2ConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.file.SQLiteConnectionProvider;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.nio.file.Path;

import static dev.chicoferreira.lifestealer.configuration.SerializerUtils.require;

public class ConnectionProviderSerializer implements TypeDeserializer<SQLConnectionProvider> {

    private final Path basePath;

    public ConnectionProviderSerializer(Path basePath) {
        this.basePath = basePath;
    }

    @Override
    public SQLConnectionProvider deserialize(@NotNull Type type, ConfigurationNode node) throws SerializationException {
        String databaseTypeName = require(node.node("type"), String.class);
        return switch (databaseTypeName) {
            case "h2" -> new H2ConnectionProvider(basePath.resolve(require(node.node("path"), String.class)));
            case "sqlite" -> new SQLiteConnectionProvider(basePath.resolve(require(node.node("path"), String.class)));
            case "mariadb" -> new MariaDBConnectionProvider(require(node, SQLHikariConnectionProperties.class));
            case "mysql" -> new MySQLConnectionProvider(require(node, SQLHikariConnectionProperties.class));
            case "postgresql" -> new PostgreSQLConnectionProvider(require(node, SQLHikariConnectionProperties.class));
            default -> throw new SerializationException("Database type " + databaseTypeName + " is invalid");
        };
    }
}
