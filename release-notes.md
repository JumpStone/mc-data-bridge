# Release Notes - v2.1.0

**MC Data Bridge v2.1.0** - **Official Minecraft 1.21.11 Support** | Secure MySQL Player Data Synchronization

- Now fully compatible with **Minecraft 1.21.11**. Securely syncs all player data (inventory, health, XP, hunger) using robust MySQL locking across your BungeeCord or Velocity network.

---

Deploy this JAR to all your PaperMC and Spigot servers, connecting to the corresponding proxy plugin (BungeeCord or Velocity) and a centralized MySQL database.

####⭐ What's New in v2.1.0\* **Minecraft 1.21.11 Compatibility:** Full support for the latest Minecraft 1.21.11 release. This ensures all player data structures, including any new or updated serialization components introduced in the latest version, are correctly captured and applied without errors.

####🚀 Core Features & Stability\* **Complete Player Data Synchronization:** Securely saves and loads all essential player attributes across your network, including:

- Inventory and Armor
- Health and Max Health
- Experience Level and Progress
- **Full Hunger Attributes:** Food Level, Saturation, and Exhaustion.
- Potion Effects
- Ender Chest, Advancements, and Statistics (configurable).

- **Robust Data Integrity (MySQL Locking):** Employs a server-ID-based database locking mechanism to prevent race conditions and data corruption when players switch servers quickly or disconnect abruptly.
- **Lag-Free Performance:** All database operations (lock acquisition, save, and load) are performed **asynchronously** off the main server thread to prevent server lag.
- **Admin Utility:** Use the `/databridge unlock <player>` command to manually clear player data locks if a connection is unexpectedly terminated.
