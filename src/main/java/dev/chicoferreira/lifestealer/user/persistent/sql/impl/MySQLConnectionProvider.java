package dev.chicoferreira.lifestealer.user.persistent.sql.impl;

import com.zaxxer.hikari.HikariConfig;
import dev.chicoferreira.lifestealer.user.persistent.sql.SQLHikariConnectionProperties;

public class MySQLConnectionProvider extends SQLHikariConnectionProvider {

    public MySQLConnectionProvider(SQLHikariConnectionProperties properties) {
        super(properties);
    }

    @Override
    public String getProtocolJdbcDatabaseName() {
        return "mysql";
    }

    @Override
    public String getDriverClassName() {
        return "";
    }

    @Override
    public void configure(HikariConfig config) {
        super.configure(config);

        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
    }
}
