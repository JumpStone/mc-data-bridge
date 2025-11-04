package com.digitalserverhost.plugins.listeners;

import com.digitalserverhost.plugins.MCDataBridge;
import com.digitalserverhost.plugins.managers.DatabaseManager;
import com.digitalserverhost.plugins.utils.PlayerData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
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
        this.gson = new GsonBuilder().create();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (!waitForLockRelease(player)) {
                    kickPlayerForError(player, "Your data is currently being saved from another server. Please try again in a moment.");
                    return;
                }

                try (Connection connection = databaseManager.getConnection()) {
                    PreparedStatement statement = connection.prepareStatement("SELECT data FROM player_data WHERE uuid = ?");
                    statement.setString(1, player.getUniqueId().toString());
                    ResultSet resultSet = statement.executeQuery();

                    if (resultSet.next()) {
                        String json = resultSet.getString("data");
                        if (json == null || json.trim().isEmpty() || json.equals("{}")) {
                            return;
                        }
                        PlayerData data = gson.fromJson(json, PlayerData.class);
                        data.setLogger(plugin.getLogger());

                        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                            applyPlayerData(player, data);
                        }, 1L);
                    } else {
                        plugin.getLogger().info("No data found for new player " + player.getName() + ".");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Critical error loading data for player " + player.getName() + ": " + e.getMessage());
                kickPlayerForError(player, "Could not load your player data. Please relog.");
            }
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        final PlayerData data;
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
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to capture player data for " + player.getName() + ". Data will not be saved. Error: " + e.getMessage());
            return;
        }

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = databaseManager.getConnection()) {
                PreparedStatement lockStatement = connection.prepareStatement(
                        "INSERT INTO player_data (uuid, data, is_locked) VALUES (?, '', 1) " +
                        "ON DUPLICATE KEY UPDATE is_locked = 1"
                );
                lockStatement.setString(1, player.getUniqueId().toString());
                lockStatement.executeUpdate();

                String json = gson.toJson(data);
                PreparedStatement saveStatement = connection.prepareStatement(
                        "UPDATE player_data SET data = ?, is_locked = 0 WHERE uuid = ?"
                );
                saveStatement.setString(1, json);
                saveStatement.setString(2, player.getUniqueId().toString());
                saveStatement.executeUpdate();

            } catch (Exception e) {
                plugin.getLogger().severe("Error saving data for player " + player.getName() + ": " + e.getMessage());
            }
        });
    }

    private boolean waitForLockRelease(Player player) throws Exception {
        long timeout = System.currentTimeMillis() + 5000; // 5 second timeout
        while (System.currentTimeMillis() < timeout) {
            try (Connection connection = databaseManager.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("SELECT is_locked FROM player_data WHERE uuid = ?");
                statement.setString(1, player.getUniqueId().toString());
                ResultSet rs = statement.executeQuery();
                if (rs.next()) {
                    if (!rs.getBoolean("is_locked")) {
                        return true; // Lock is released
                    }
                } else {
                    return true; // No record exists, so it's not locked
                }
            }
            Thread.sleep(200); // Poll every 200ms
        }
        return false; // Timeout reached
    }

    private void kickPlayerForError(Player player, String message) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            player.kickPlayer("§c[DataBridge] " + message);
        });
    }

    private void applyPlayerData(Player player, PlayerData data) {
        ItemStack[] inventoryContents = data.getInventoryContents();
        ItemStack[] armorContents = data.getArmorContents();

        if (data.hasDeserializationError()) {
            kickPlayerForError(player, "A critical error occurred while deserializing your inventory. Please contact an administrator.");
            return;
        }

        player.setHealth(data.getHealth());
        player.setFoodLevel(data.getFoodLevel());
        player.setSaturation(data.getSaturation());
        player.setExhaustion(data.getExhaustion());
        player.setTotalExperience(data.getTotalExperience());
        player.setExp(data.getExp());
        player.setLevel(data.getLevel());

        player.getInventory().setContents(inventoryContents);
        player.getInventory().setArmorContents(armorContents);

        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        if (data.getPotionEffects() != null) {
            for (PotionEffect effect : data.getPotionEffects()) {
                if (effect != null) {
                    player.addPotionEffect(effect);
                }
            }
        }

        plugin.getLogger().info("Successfully applied data to player " + player.getName());
    }
}
