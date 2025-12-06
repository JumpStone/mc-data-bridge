## TODO.md: MC Data Bridge Version 2.0.3

### 💡 Feature: Granular Data Synchronization Toggles

The highest priority is allowing administrators full control over what data is synchronized.

* [ ] **Create New Configuration Toggles (Total of 9)**
    * [ ] Add `sync-data` section to `config.yml`.
    * [ ] Add and **enable by default** toggles for all **existing** core data:
        * [ ] `health`
        * [ ] `food-level`
        * [ ] `experience`
        * [ ] `inventory`
        * [ ] `armor`
        * [ ] `potion-effects`
    * [ ] Add and **DISABLE by default** toggles for all **new** data components:
        * [ ] `ender-chest`
        * [ ] `location`
        * [ ] `advancements`
    * [ ] Update `MCDataBridge.java` to load and expose all `sync-data` settings.
* [ ] **Conditional Data Capture (`PlayerData.java`)**
    * [ ] Implement logic in `PlayerData` constructor to skip capturing data if the corresponding toggle is disabled.
* [ ] **Conditional Data Application (`PlayerListener.java`)**
    * [ ] Modify `PlayerListener.applyPlayerData()` to only apply data if the corresponding sync toggle is enabled.

### 📚 Feature: Data Expansion (New Components)

Integrate major missing player data components into the synchronization system.

* [ ] **Ender Chest Sync**
    * [ ] Update `PlayerData.java` to capture and serialize the player's Ender Chest contents.
    * [ ] Update `PlayerListener.applyPlayerData()` to apply the Ender Chest contents conditionally (must be on the main thread).
* [ ] **Advancements & Recipes Sync**
    * [ ] Update `PlayerData.java` to implement serialization/deserialization for player advancements and discovered recipes.
    * [ ] Update `PlayerListener.applyPlayerData()` to grant advancements/discover recipes conditionally.
* [ ] **Player Location and World Capture (Admin Logging Only)**
    * [ ] Update `PlayerData.java` to store `world` name, `x`, `y`, `z` coordinates, and `pitch`/`yaw` upon save.
    * [ ] **Crucial:** Ensure that `PlayerListener.applyPlayerData()` **NEVER** attempts to teleport the player using this data. The feature is for logging/debugging only.

***

### ⚙️ Optimization & Database Refinements

Improve backend performance, reduce technical debt, and ensure database efficiency.

* [ ] **Pre-compile Prepared Statements (Performance)**
    * [ ] Refactor `DatabaseManager.java` to initialize and store reusable `PreparedStatement` objects for high-traffic SQL queries.
* [ ] **Configurable Lock Heartbeat**
    * [ ] Add a configurable option in `config.yml` (e.g., `lock-heartbeat-seconds`).
    * [ ] Update `PlayerListener.onPlayerJoin()` to use this configured value for the lock update task.
* [ ] **Database Column Type Review**
    * [ ] Change the column type for `data` in the `player_data` table schema from `LONGTEXT` to a more appropriate binary type like **`MEDIUMBLOB`** or **`LONGBLOB`**.
    * [ ] Add a migration check/warning in `MCDataBridge.onEnable()` if the column type needs updating.
* [ ] **Legacy Potion Effect Migration**
    * [ ] Create a one-time migration routine in `MCDataBridge.onEnable()` to detect and convert legacy uppercase potion effect names (if possible) in the `data` field.
* [ ] **Modern Message Handling**
    * [ ] Replace non-Adventure console log messages with **MiniMessage** formatted output, utilizing the Adventure API throughout for consistent styling.

***

### ✨ Quality of Life (QoL) & Admin Tools

Features to improve server stability, admin control, and player experience.

* [ ] **Server/World Blacklist**
    * [ ] Add a new configuration section (`sync-blacklist`) for lists of server and world names where data synchronization should be completely disabled.
    * [ ] Implement logic in `PlayerListener` to check blacklists before triggering save/load events.
* [ ] **Admin Unlock Command**
    * [ ] Register a new command (e.g., `/databridge unlock <player>`).
    * [ ] Implement the command logic to manually call `DatabaseManager.releaseLock()` for a stuck player UUID.
    * [ ] Implement a robust permission check for the command.
* [ ] **Fallback on Lock Failure**
    * [ ] Update `PlayerListener.onAsyncPlayerPreLogin()` lock failure logic.
    * [ ] Instead of kicking the player after the max attempts, redirect them to a configured fallback lobby via the proxy's API.
* [ ] **Documentation**
    * [ ] Update `README.md` to reflect all new syncable data and the proxy requirement.
    * [ ] Draft the final `release-notes.md` for version 2.0.3.