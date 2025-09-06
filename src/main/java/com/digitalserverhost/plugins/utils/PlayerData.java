package com.digitalserverhost.plugins.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import java.util.Arrays;
import java.io.IOException;

public class PlayerData {

    private double health;
    private int foodLevel;
    private float saturation;
    private float exhaustion;
    private int totalExperience;
    private float exp;
    private int level;
    private String inventoryContentsBase64;
    private String armorContentsBase64;
    private SerializablePotionEffect[] potionEffects;

    public PlayerData(double health, int foodLevel, float saturation, float exhaustion, int totalExperience, float exp, int level, ItemStack[] inventoryContents, ItemStack[] armorContents, PotionEffect[] potionEffects) {
        this.health = health;
        this.foodLevel = foodLevel;
        this.saturation = saturation;
        this.exhaustion = exhaustion;
        this.totalExperience = totalExperience;
        this.exp = exp;
        this.level = level;
        try {
            this.inventoryContentsBase64 = ItemStackSerializer.itemStackArrayToBase64(inventoryContents);
            this.armorContentsBase64 = ItemStackSerializer.itemStackArrayToBase64(armorContents);
        } catch (IllegalStateException e) {
            throw new RuntimeException("Error converting ItemStack array to Base64: " + e.getMessage(), e);
        }
        this.potionEffects = convertPotionEffectArrayToSerializable(potionEffects);
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public int getTotalExperience() {
        return totalExperience;
    }

    public float getExp() {
        return exp;
    }

    public int getLevel() {
        return level;
    }

    public ItemStack[] getInventoryContents() {
        if (inventoryContentsBase64 == null || inventoryContentsBase64.isEmpty()) {
            return new ItemStack[0];
        }
        try {
            return ItemStackSerializer.itemStackArrayFromBase64(inventoryContentsBase64);
        } catch (IOException e) {
            throw new RuntimeException("Error converting Base64 to ItemStack array: " + e.getMessage(), e);
        }
    }

    public ItemStack[] getArmorContents() {
        if (armorContentsBase64 == null || armorContentsBase64.isEmpty()) {
            return new ItemStack[0];
        }
        try {
            return ItemStackSerializer.itemStackArrayFromBase64(armorContentsBase64);
        } catch (IOException e) {
            throw new RuntimeException("Error converting Base64 to ItemStack array: " + e.getMessage(), e);
        }
    }

    public PotionEffect[] getPotionEffects() {
        return convertSerializablePotionEffectArrayToPotionEffect(potionEffects);
    }

    private SerializablePotionEffect[] convertPotionEffectArrayToSerializable(PotionEffect[] effects) {
        if (effects == null) {
            return null;
        }
        SerializablePotionEffect[] serializableEffects = new SerializablePotionEffect[effects.length];
        for (int i = 0; i < effects.length; i++) {
            serializableEffects[i] = new SerializablePotionEffect(effects[i]);
        }
        return serializableEffects;
    }

    private PotionEffect[] convertSerializablePotionEffectArrayToPotionEffect(SerializablePotionEffect[] serializableEffects) {
        if (serializableEffects == null) {
            return null;
        }
        PotionEffect[] effects = new PotionEffect[serializableEffects.length];
        for (int i = 0; i < serializableEffects.length; i++) {
            if (serializableEffects[i] != null) {
                effects[i] = serializableEffects[i].toPotionEffect();
            }
        }
        return effects;
    }

    @Override
    public String toString() {
        return "PlayerData{" +
                "health=" + health + ", " +
                "foodLevel=" + foodLevel + ", " +
                "saturation=" + saturation + ", " +
                "exhaustion=" + exhaustion + ", " +
                "totalExperience=" + totalExperience + ", " +
                "exp=" + exp + ", " +
                "level=" + level + ", " +
                "inventoryContentsBase64='" + inventoryContentsBase64 + "', " +
                "armorContentsBase64='" + armorContentsBase64 + "', " +
                "potionEffects=" + Arrays.toString(potionEffects) +
                "}\n";
    }
}
