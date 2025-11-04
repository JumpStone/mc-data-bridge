# Release Notes - MC Data Bridge `1.21.10.2`

This is a critical stability and performance update. This version refactors the core data handling to be fully asynchronous, preventing server lag and ensuring data integrity across servers. We strongly recommend all users update.

## ✅ Fixes & Improvements

### Stability & Data Integrity (High Priority)

* **Asynchronous Database Operations:** All database calls (loading/saving data, table creation) are now performed asynchronously. This resolves major performance issues and prevents server lag or crashes from database-related delays.
* **Race Condition Fix:** A database-level locking mechanism (`is_locked` flag) has been implemented. This prevents data loss when players switch servers quickly, ensuring the most recent data is always loaded.
* **Version-Independent Item Serialization:** Replaced the fragile `BukkitObjectOutputStream` with a robust, version-independent NBT-based system. This is a critical fix to **prevent inventories from being wiped after a Minecraft version update.**
* **Robust Error Handling:** If a player's data cannot be loaded on join, they will be safely kicked with an error message. This prevents a player's valid data from being accidentally overwritten by an empty inventory.

### Performance & Configuration

* **Upgraded Connection Pooling:** The database connection manager now uses HikariCP with a full set of advanced, configurable settings for optimal performance and resilience.
* **Updated `config.yml`:** The configuration file has been expanded to include new options for fine-tuning the HikariCP connection pool and MySQL JDBC driver.
* **Dependency & Build Updates:**
    * Updated core dependencies, including `mysql-connector-j` (9.5.0), `spigot-api` (1.21.10), and `item-nbt-api`.
    * Corrected the plugin shading process. `gson` is now correctly set as `provided` to prevent conflicts with other plugins, and `item-nbt-api` is properly relocated.

## ⚠️ Important Notes for Upgrading

* **Database Schema Update:** On startup, the plugin will automatically add `is_locked` and `last_updated` columns to your `player_data` table. This is required for the new data-locking feature.
* **Configuration Update:** Your `config.yml` will be updated with the new connection pool settings. It is recommended to review these new options, though the defaults are optimized for most servers.