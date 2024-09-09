package dev.chicoferreira.lifestealer.user.persistent.sql.impl.file;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnectionProvider extends FileConnectionProvider {

    public SQLiteConnectionProvider(Path path) {
        super(path);
    }

    @Override
    protected @NotNull Connection createConnection(Path path) throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + path.toString());
    }

    @Override
    public String getDriverClassName() {
        return "org.sqlite.JDBC";
    }
}
