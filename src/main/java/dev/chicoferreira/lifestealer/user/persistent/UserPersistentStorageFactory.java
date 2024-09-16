package dev.chicoferreira.lifestealer.user.persistent;

import dev.chicoferreira.lifestealer.user.persistent.sql.SQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLUserPersistentStorage;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.MariaDBConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.MySQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.PostgreSQLConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.file.FileConnectionProperties;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.file.H2ConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.impl.file.SQLiteConnectionProvider;
import org.jetbrains.annotations.NotNull;

public class UserPersistentStorageFactory {

    public static @NotNull UserPersistentStorage create(UserPersistentStorageProperties properties) {
        SQLConnectionProvider connectionProvider = switch (properties.type()) {
            case MARIADB -> new MariaDBConnectionProvider((SQLHikariConnectionProperties) properties);
            case MYSQL -> new MySQLConnectionProvider((SQLHikariConnectionProperties) properties);
            case POSTGRESQL -> new PostgreSQLConnectionProvider((SQLHikariConnectionProperties) properties);
            case H2 -> new H2ConnectionProvider((FileConnectionProperties) properties);
            case SQLITE -> new SQLiteConnectionProvider((FileConnectionProperties) properties);
        };

        return new SQLUserPersistentStorage(connectionProvider);
    }

}
