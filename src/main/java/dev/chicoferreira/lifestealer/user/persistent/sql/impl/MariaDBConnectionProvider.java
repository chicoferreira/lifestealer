package dev.chicoferreira.lifestealer.user.persistent.sql.impl;

import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;

public class MariaDBConnectionProvider extends SQLHikariConnectionProvider {

    public MariaDBConnectionProvider(SQLHikariConnectionProperties properties) {
        super(properties);
    }

    @Override
    public String getProtocolJdbcDatabaseName() {
        return "mariadb";
    }

    @Override
    public String getDriverClassName() {
        return "org.mariadb.jdbc.Driver";
    }
}
