package com.digitalserverhost.plugins.utils;


import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class SerializablePotionEffect {
    private String typeKey;
    private int duration;
    private int amplifier;
    private boolean ambient;
    private boolean particles;
    private boolean icon;

    // No-arg constructor for Gson
    public SerializablePotionEffect() {}

    public SerializablePotionEffect(PotionEffect effect) {
        if (effect == null) {
            return;
        }
        this.typeKey = effect.getType().getName();
        this.duration = effect.getDuration();
        this.amplifier = effect.getAmplifier();
        this.ambient = effect.isAmbient();
        this.particles = effect.hasParticles();
        this.icon = effect.hasIcon();
    }

    public PotionEffect toPotionEffect() {
        if (typeKey == null) {
            return null;
        }
        PotionEffectType effectType = PotionEffectType.getByName(typeKey);
        if (effectType == null) {
            return null;
        }
        return new PotionEffect(effectType, duration, amplifier, ambient, particles, icon);
    }

    @Override
    public String toString() {
        return "SerializablePotionEffect{" +
                "typeKey='" + typeKey + "'" +
                ", duration=" + duration +
                ", amplifier=" + amplifier +
                ", ambient=" + ambient +
                ", particles=" + particles +
                ", icon=" + icon +
                '}';
    }
}