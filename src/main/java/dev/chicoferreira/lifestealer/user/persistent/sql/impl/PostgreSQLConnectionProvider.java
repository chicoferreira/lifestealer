package dev.chicoferreira.lifestealer.user.persistent.sql.impl;

import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;

public class PostgreSQLConnectionProvider extends SQLHikariConnectionProvider {
    public PostgreSQLConnectionProvider(SQLHikariConnectionProperties properties) {
        super(properties);
    }

    @Override
    public String getProtocolJdbcDatabaseName() {
        return "postgresql";
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }
}
