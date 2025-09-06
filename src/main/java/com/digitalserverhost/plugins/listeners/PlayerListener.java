package com.digitalserverhost.plugins.listeners;

import com.digitalserverhost.plugins.MCDataBridge;
import com.digitalserverhost.plugins.managers.DatabaseManager;
import com.digitalserverhost.plugins.utils.PlayerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PlayerListener implements Listener {

    private final DatabaseManager databaseManager;
    private final MCDataBridge plugin;
    private final Gson gson;

    public PlayerListener(DatabaseManager databaseManager, MCDataBridge plugin) {
        this.databaseManager = databaseManager;
        this.plugin = plugin;
        this.gson = new GsonBuilder()
                .setPrettyPrinting() // Make JSON readable
                .create();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") joined. Attempting to load data...");
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("SELECT data FROM player_data WHERE uuid = ?");
            statement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String json = resultSet.getString("data");
                if (plugin.isDebugMode()) {
                    plugin.getLogger().info("Loaded JSON data for " + player.getName() + ":\n" + json);
                }
                PlayerData data = gson.fromJson(json, PlayerData.class);
                if (plugin.isDebugMode()) {
                    plugin.getLogger().info("Deserialized PlayerData for " + player.getName() + ": " + data.toString());
                }

                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    player.setHealth(data.getHealth());
                    player.setFoodLevel(data.getFoodLevel());
                    player.setSaturation(data.getSaturation());
                    player.setExhaustion(data.getExhaustion());
                    player.setTotalExperience(data.getTotalExperience());
                    player.setExp(data.getExp());
                    player.setLevel(data.getLevel());
                    try {
                        player.getInventory().setContents(data.getInventoryContents());
                        player.getInventory().setArmorContents(data.getArmorContents());
                    } catch (RuntimeException e) {
                        plugin.getLogger().severe("Error applying inventory/armor for player " + player.getName() + ": " + e.getMessage());
                    }

                    // Clear existing effects first to prevent duplication or conflicts
                    for (PotionEffect effect : player.getActivePotionEffects()) {
                        player.removePotionEffect(effect.getType());
                    }

                    // Apply saved effects
                    if (data.getPotionEffects() != null) {
                        for (PotionEffect effect : data.getPotionEffects()) {
                            if (effect != null) {
                                player.addPotionEffect(effect);
                            }                            
                        }
                    }
                    if (plugin.isDebugMode()) {
                        plugin.getLogger().info("Applied data to player " + player.getName() +
                            ": health=" + player.getHealth() +
                            ", food=" + player.getFoodLevel() +
                            ", xp=" + player.getTotalExperience() +
                            ", effects=" + player.getActivePotionEffects().size());
                    }
                });
            } else {
                plugin.getLogger().info("No data found for player " + player.getName() + ".");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Error loading data for player " + player.getName() + ": " + e.getMessage());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getLogger().info("Player " + player.getName() + " (" + player.getUniqueId() + ") quit. Attempting to save data...");
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("Saving data for player " + player.getName() +
                ": health=" + player.getHealth() +
                ", food=" + player.getFoodLevel() +
                ", xp=" + player.getTotalExperience() +
                ", effects=" + player.getActivePotionEffects().size());
        }
        PlayerData data = null;
        String json = null; // Declare json here
        try {
            data = new PlayerData(
                    player.getHealth(),
                    player.getFoodLevel(),
                    player.getSaturation(),
                    player.getExhaustion(),
                    player.getTotalExperience(),
                    player.getExp(),
                    player.getLevel(),
                    player.getInventory().getContents(),
                    player.getInventory().getArmorContents(),
                    player.getActivePotionEffects().toArray(new PotionEffect[0])
            );
            json = gson.toJson(data); // Assign value here
        } catch (RuntimeException e) {
            plugin.getLogger().severe("Error creating PlayerData for player " + player.getName() + ": " + e.getMessage());
            return; // Stop saving if PlayerData creation fails
        }
        if (plugin.isDebugMode()) {
            plugin.getLogger().info("Player data to be serialized for " + player.getName() + ": " + data.toString());
            plugin.getLogger().info("Serialized JSON data for " + player.getName() + ":\n" + json);
        }
        try (Connection connection = databaseManager.getConnection()) {
            PreparedStatement statement = connection.prepareStatement("REPLACE INTO player_data (uuid, data) VALUES (?, ?)");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, json);
            statement.executeUpdate();
            plugin.getLogger().info("Data saved for player " + player.getName() + ".");
        } catch (Exception e) {
            plugin.getLogger().severe("Error saving data for player " + player.getName() + ": " + e.getMessage());
        }
    }
}
