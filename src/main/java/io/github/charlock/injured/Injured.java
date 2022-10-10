package io.github.charlock.injured;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class Injured extends JavaPlugin {
    private FileConfiguration config;
    private Map<UUID, InjuredPlayer> injuredPlayers;
    public HashSet<String> injuries;

    public Injured() {
        config = getConfig();
        injuredPlayers = new HashMap<UUID, InjuredPlayer>();
        injuries = new HashSet<String>();
        injuries.add("bleeding");
        injuries.add("crippled");
    }

    /**
     * Returns the PotionEffect for a given injury.
     */
    public PotionEffect getInjuryEffect(String injury) {
        if (injury.equalsIgnoreCase("bleed")) {
            return new PotionEffect(PotionEffectType.POISON, config.getInt("bleedDuration"), config.getInt("bleedInterval"), false, false, false);
        }
        return null;
    }

    // Functions related to accessing the injured players.

    /**
     * Returns whether or not a player is being tracked for injuries.
     */
    public boolean trackingPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        return injuredPlayers.containsKey(playerId);
    }

    /**
     * Returns whether or not a player has an injury.
     */
    public boolean hasInjury(Player player, InjuredPlayer.InjuredPlayerFlag injury) {
        if (trackingPlayer(player)) {
            return injuredPlayers.get(player.getUniqueId()).hasInjury(injury);
        }
        return false;
    }

    /**
     * Adds an injury to a player.
     */
    public void addInjury(Player player, InjuredPlayer.InjuredPlayerFlag injury) {
        if (!trackingPlayer(player)) {
            injuredPlayers.put(player.getUniqueId(), new InjuredPlayer(player));
        }
        InjuredPlayer currentInjuries = injuredPlayers.get(player.getUniqueId());
        currentInjuries.addInjury(InjuredPlayer.InjuredPlayerFlag.BLEEDING);
        injuredPlayers.replace(player.getUniqueId(), currentInjuries);
    }

    /**
     * Removes an injury from a player.
     */
    public void removeInjury(Player player, InjuredPlayer.InjuredPlayerFlag injury) {
        if (hasInjury(player, injury)) {
            InjuredPlayer currentInjuries = injuredPlayers.get(player.getUniqueId());
            currentInjuries.removeInjury(injury);
            injuredPlayers.replace(player.getUniqueId(), currentInjuries);
        }
    }

    /**
     * Clears every injury from a player.
     */
    public void clearInjuries(Player player) {
        if (trackingPlayer(player)) {
            InjuredPlayer currentInjuries = injuredPlayers.get(player.getUniqueId());
            currentInjuries.clearInjuries();
            injuredPlayers.replace(player.getUniqueId(), currentInjuries);
        }
    }


    @Override
    public void onEnable() {
        config.addDefault("bleedChance", 0.15);
        config.addDefault("bleedDuration", Integer.MAX_VALUE);
        config.addDefault("bleedInterval", 0);
        config.addDefault("bleedParticleCount", 25);
        config.options().copyDefaults(true);
        saveConfig();

        getServer().getPluginManager().registerEvents(new PlayerBleedListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);

        getCommand("injure").setExecutor(new CommandHandler(this));
        getCommand("uninjure").setExecutor(new CommandHandler(this));
    }

    @Override
    public void onDisable() {
        getLogger().warning("Injuries are no longer in effect. Plugin disabled.");
    }
}