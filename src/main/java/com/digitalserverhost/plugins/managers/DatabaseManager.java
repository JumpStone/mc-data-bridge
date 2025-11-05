package com.digitalserverhost.plugins.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final HikariDataSource dataSource;

    public DatabaseManager(FileConfiguration config) {
        HikariConfig hikariConfig = new HikariConfig();

        // Standard connection details
        String jdbcUrl = "jdbc:mysql://" + config.getString("database.host") + ":" + config.getInt("database.port") + "/" + config.getString("database.database");
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(config.getString("database.username"));
        hikariConfig.setPassword(config.getString("database.password"));

        // Add all properties from the config
        if (config.isConfigurationSection("database.properties")) {
            for (String key : config.getConfigurationSection("database.properties").getKeys(false)) {
                String value = config.getString("database.properties." + key);
                hikariConfig.addDataSourceProperty(key, value);
            }
        }

        // Pool settings from config
        hikariConfig.setMaximumPoolSize(config.getInt("database.pool-settings.maximum-pool-size", 10));
        hikariConfig.setMinimumIdle(config.getInt("database.pool-settings.minimum-idle", 10));
        hikariConfig.setMaxLifetime(config.getInt("database.pool-settings.max-lifetime", 1800000)); // 30 minutes
        hikariConfig.setConnectionTimeout(config.getInt("database.pool-settings.connection-timeout", 5000)); // 5 seconds
        hikariConfig.setIdleTimeout(config.getInt("database.pool-settings.idle-timeout", 600000)); // 10 minutes

        // MySQL-specific optimizations
        hikariConfig.addDataSourceProperty("cachePrepStmts", config.getBoolean("database.optimizations.cache-prep-stmts", true));
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", config.getInt("database.optimizations.prep-stmt-cache-size", 250));
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", config.getInt("database.optimizations.prep-stmt-cache-sql-limit", 2048));
        hikariConfig.addDataSourceProperty("useServerPrepStmts", config.getBoolean("database.optimizations.use-server-prep-stmts", true));
        hikariConfig.addDataSourceProperty("useLocalSessionState", config.getBoolean("database.optimizations.use-local-session-state", true));
        hikariConfig.addDataSourceProperty("rewriteBatchedStatements", config.getBoolean("database.optimizations.rewrite-batched-statements", true));
        hikariConfig.addDataSourceProperty("cacheResultSetMetadata", config.getBoolean("database.optimizations.cache-result-set-metadata", true));
        hikariConfig.addDataSourceProperty("cacheServerConfiguration", config.getBoolean("database.optimizations.cache-server-configuration", true));
        hikariConfig.addDataSourceProperty("elideSetAutoCommits", config.getBoolean("database.optimizations.elide-set-auto-commits", true));
        hikariConfig.addDataSourceProperty("maintainTimeStats", config.getBoolean("database.optimizations.maintain-time-stats", false));

        dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
