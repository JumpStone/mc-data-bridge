# MC Data Bridge

MC Data Bridge is a Spigot/Paper plugin designed to seamlessly synchronize player data across multiple Minecraft servers. This allows players to move between linked servers (e.g., a Towny server and a Resource server) and retain their health, hunger, experience, inventory, armor, and active potion effects.

## Features

*   **Cross-Server Player Data Sync:** Synchronizes core player data including:
    *   Health
    *   Food Level & Saturation
    *   Experience (Total XP, current XP, and Level)
    *   Inventory Contents
    *   Armor Contents
    *   Active Potion Effects
*   **Configurable Database Connection:** Easily connect to your MySQL database.
*   **Flexible Synchronization Groups:** Create distinct synchronization groups by using different database credentials or table names for different sets of servers. For example, you can have one database for your "Towny" servers and another for your "vanilla" servers, ensuring data is synced only within those specific groups.

## Installation

1.  **Build the Plugin:**
    *   Navigate to the plugin's root directory in your terminal.
    *   Run `mvn clean package` to build the plugin.
    *   The compiled JAR file (`mc-data-bridge-1.21.8.1-shaded.jar`) will be located in the `target/` directory.
2.  **Deploy to Servers:**
    *   Copy the `mc-data-bridge-1.21.8.1-shaded.jar` file into the `plugins/` folder of each PaperMC server you wish to synchronize.

## Configuration

A `config.yml` file will be generated in the `plugins/mc-data-bridge/` folder after the first run (or you can create it manually). You **must** update this file with your MySQL database credentials.

```yaml
database:
  host: localhost
  port: 3306
  database: minecraft
  username: user
  password: password
debug: false
```

*   **`database.host`**: Your MySQL database host (e.g., `localhost`, `127.0.0.1`, a remote IP, or a [Docker named volume](https://docs.docker.com/storage/volumes/#create-and-manage-volumes) if your database is running in Docker).
*   **`database.port`**: The port your MySQL database is running on (default is `3306`).
*   **`database.database`**: The name of the database to use for player data.
*   **`database.username`**: The username for connecting to your database.
*   **`database.password`**: The password for the database user.
*   **`debug`**: Set to `true` to enable verbose debugging messages in the server console. Set to `false` for normal operation.

### Synchronizing Multiple Server Groups

To sync player data between specific groups of servers (e.g., a Towny server and a Towny resource server, separate from a Creative server), simply configure the `database.database` (or even entirely different `database.host`, `username`, `password`) to match for the servers you want to link.

**Example:**

*   **Towny Servers (Towny-Main, Towny-Resource):**
    ```yaml
    database:
      host: my_db_host
      port: 3306
      database: towny_player_data # All Towny servers use this database
      username: towny_user
      password: towny_password
    debug: false
    ```
*   **Creative Servers (Creative-Build, Creative-Minigame):**
    ```yaml
    database:
      host: my_db_host
      port: 3306
      database: creative_player_data # All Creative servers use this database
      username: creative_user
      password: creative_password
    debug: false
    ```

## Usage

1.  **Add the JAR:** Place the `mc-data-bridge-1.21.8.1-shaded.jar` file into the `plugins/` folder of all your PaperMC servers.
2.  **Configure:** Edit the `config.yml` in each server's `plugins/mc-data-bridge/` folder with the appropriate database credentials for your desired synchronization groups.
3.  **Restart Servers:** Restart your Minecraft server containers (or the servers themselves) to apply the plugin and configuration changes.
4.  **Enjoy!** Players can now seamlessly switch between your linked servers, and their data will be synchronized automatically.

## Important Notes

*   This plugin requires a **MySQL database** to function. Ensure your database server is accessible from your Minecraft servers.
*   The plugin will automatically create the `player_data` table in your specified database if it doesn't already exist.
*   You might still see warnings about deprecated APIs during compilation. These are generally harmless and do not prevent the plugin from functioning.
