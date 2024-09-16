package dev.chicoferreira.lifestealer.user.persistent.sql.impl.file;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionProvider extends FileConnectionProvider {

    public H2ConnectionProvider(FileConnectionProperties properties) {
        super(properties);
    }

    @Override
    protected @NotNull Connection createConnection(Path path) throws SQLException {
        return DriverManager.getConnection("jdbc:h2:" + path.toAbsolutePath());
    }

    @Override
    public String getDriverClassName() {
        return "org.h2.Driver";
    }
}
