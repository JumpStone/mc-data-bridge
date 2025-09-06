package com.digitalserverhost.plugins;

import com.digitalserverhost.plugins.listeners.PlayerListener;
import com.digitalserverhost.plugins.managers.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.Statement;

public class MCDataBridge extends JavaPlugin {

    private DatabaseManager databaseManager;
    private boolean debugMode;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.debugMode = getConfig().getBoolean("debug", false);
        databaseManager = new DatabaseManager(getConfig());
        createServerTable();
        getServer().getPluginManager().registerEvents(new PlayerListener(databaseManager, this), this);
        getLogger().info("mc-data-bridge has been enabled!");
    }

    @Override
    public void onDisable() {
        databaseManager.close();
        getLogger().info("mc-data-bridge has been disabled!");
    }

    private void createServerTable() {
        try (Connection connection = databaseManager.getConnection()) {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS player_data (uuid VARCHAR(36) NOT NULL, data TEXT NOT NULL, PRIMARY KEY (uuid))");
        } catch (Exception e) {
            getLogger().severe("Error creating player_data table: " + e.getMessage());
        }
    }

    public boolean isDebugMode() {
        return debugMode;
    }
}
