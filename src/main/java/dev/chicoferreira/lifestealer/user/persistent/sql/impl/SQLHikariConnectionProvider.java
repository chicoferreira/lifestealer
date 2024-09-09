package dev.chicoferreira.lifestealer.user.persistent.sql.impl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLDriverConnectionProvider;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class SQLHikariConnectionProvider extends SQLDriverConnectionProvider {

    private HikariDataSource dataSource;

    public SQLHikariConnectionProvider(SQLHikariConnectionProperties properties) {
        super();
        init(properties);
    }

    private void init(SQLHikariConnectionProperties properties) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl("jdbc:" + getProtocolJdbcDatabaseName() + "://" + properties.address() + "/" + properties.database());
        config.setUsername(properties.username());
        config.setPassword(properties.password());
        config.setPoolName("lifestealer-pool");

        configure(config);

        properties.extraProperties().forEach(config::addDataSourceProperty);

        this.dataSource = new HikariDataSource(config);
    }

    public abstract String getProtocolJdbcDatabaseName();

    public void configure(HikariConfig config) {
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new SQLException("Failed to get connection", e);
        }
    }

    @Override
    public void close() {
        if (this.dataSource != null) {
            this.dataSource.close();
        }
    }
}
