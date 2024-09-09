package dev.chicoferreira.lifestealer.user.persistent.sql;

import java.sql.Connection;
import java.sql.SQLException;

public interface SQLConnectionProvider {

    Connection getConnection() throws SQLException;

    void close() throws SQLException;

}
