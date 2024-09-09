package dev.chicoferreira.lifestealer.user.persistent.sql.impl.file;

import dev.chicoferreira.lifestealer.user.persistent.sql.SQLDriverConnectionProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class FileConnectionProvider extends SQLDriverConnectionProvider {

    private final Path path;
    private NonClosableConnectionWrapper connection;

    public FileConnectionProvider(Path path) {
        super();
        this.path = path;
    }

    protected abstract @NotNull Connection createConnection(Path path) throws SQLException;

    @Override
    public Connection getConnection() throws SQLException {
        if (this.connection == null || this.connection.isClosed()) {
            this.connection = new NonClosableConnectionWrapper(createConnection(this.path));
        }
        return connection;
    }

    @Override
    public void close() throws SQLException {
        if (this.connection != null) {
            this.connection.shutdown();
        }
    }
}
