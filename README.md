# MC Data Bridge

MC Data Bridge is a robust, high-performance Spigot/Paper plugin designed to seamlessly synchronize player data across multiple Minecraft servers. It ensures that players have a consistent experience, retaining their health, hunger, experience, inventory, and more as they move between linked servers.

## Features

  * **Fully Asynchronous:** All database operations are performed on a separate thread, ensuring that your server's main thread is never blocked. This means no lag or crashes, even if your database is slow to respond.
  * **Race-Condition Safe:** A database-level locking mechanism prevents data loss when players switch servers quickly. This guarantees that the most recent player data is always loaded.
  * **Version-Independent Item Serialization:** Player inventories are serialized using a robust NBT-based method, which prevents data loss when you update your Minecraft server to a new version.
  * **Cross-Server Player Data Sync:** Synchronizes core player data including:
      * Health
      * Food Level & Saturation
      * Experience (Total XP, current XP, and Level)
      * Inventory Contents
      * Armor Contents
      * Active Potion Effects
  * **Resilient Connection Pooling:** Uses HikariCP with optimized settings to ensure that the database connection is resilient to network issues and database restarts.
  * **Configurable & Flexible:** Easily connect to your MySQL database and create distinct synchronization groups for different sets of servers.

## Installation

1.  **Build the Plugin:**
      * Navigate to the plugin's root directory in your terminal.
      * Run `mvn clean package` to build the plugin.
      * The compiled JAR file (`mc-data-bridge-1.21.10.*.jar`) will be located in the `target/` directory.
2.  **Deploy to Servers:**
      * Copy the `mc-data-bridge-1.21.10.*.jar` file into the `plugins/` folder of each PaperMC server you wish to synchronize.

## Configuration

A `config.yml` file will be generated in the `plugins/mc-data-bridge/` folder after the first run. You must update this file with your MySQL database credentials. The file is pre-configured with optimized settings for performance and stability.

```yaml
# MySQL Database Configuration
database:
  host: localhost
  port: 3306
  database: minecraft
  username: user
  password: password

  # HikariCP Connection Pool Settings
  # These settings are optimized for resilience and performance.
  # It is recommended to leave these at their default values unless you are an experienced administrator.
  pool-settings:
    maximum-pool-size: 10
    minimum-idle: 10
    max-lifetime: 1800000 # 30 minutes
    connection-timeout: 5000 # 5 seconds
    idle-timeout: 600000 # 10 minutes

  # MySQL JDBC Optimizations
  # These are advanced settings for the MySQL driver.
  # Do not change these unless you know what you are doing.
  optimizations:
    cache-prep-stmts: true
    prep-stmt-cache-size: 250
    prep-stmt-cache-sql-limit: 2048
    use-server-prep-stmts: true
    use-local-session-state: true
    rewrite-batched-statements: true
    cache-result-set-metadata: true
    cache-server-configuration: true
    elide-set-auto-commits: true
    maintain-time-stats: false

# Set to true to enable verbose debugging messages in the server console.
# This can be useful for diagnosing issues, but should be false for normal operation.
debug: false
```

## Usage

1.  **Add the JAR:** Place the `mc-data-bridge-1.21.10.*.jar` file into the `plugins/` folder of all your PaperMC servers.
2.  **Configure:** Edit the `config.yml` in each server's `plugins/mc-data-bridge/` folder with the appropriate database credentials.
3.  **Restart Servers:** Restart your Minecraft servers to apply the plugin and configuration changes.
4.  **Enjoy\!** Players can now seamlessly switch between your linked servers, and their data will be synchronized automatically and safely.

## Important Notes

  * This plugin requires a **MySQL database** to function. Ensure your database server is accessible from your Minecraft servers.
  * The plugin will automatically create and update the `player_data` table in your specified database.