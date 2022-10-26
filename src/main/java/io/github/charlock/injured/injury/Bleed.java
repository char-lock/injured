package io.github.charlock.injured.injury;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;


/**
 * An injury caused by taking cutting types of damage, Bleed will
 * apply configured damage at a configured interval for the
 * configured duration, or until healed with a bandage.
 * 
 */
public class Bleed extends Injury {
    private FileConfiguration userConfig;
    private double damagePercent;
    private int damageInterval;
    private String onInjuryMessage;
    private String onRemedyMessage;
    private boolean soundOnInjury;
    private boolean soundOnDamage;

    /** 
     * Constructor for Bleed to ensure all of the configured settings
     * are used properly.
     *  
     */
    public Bleed() {
        super(InjuryType.BLEEDING);
        this.userConfig = this.getPlugin().getConfig();
        this.setCauses(new HashSet<DamageCause>(Arrays.asList(
            DamageCause.CONTACT,
            DamageCause.ENTITY_ATTACK,
            DamageCause.ENTITY_SWEEP_ATTACK,
            DamageCause.PROJECTILE,
            DamageCause.MAGIC,
            DamageCause.THORNS,
            DamageCause.CUSTOM
        )));
        this.setDuration(userConfig.getInt("injuries.bleed.tickDuration"));
        this.setChancePercent(userConfig.getDouble("injuries.bleed.chancePercent"));
        this.damagePercent = userConfig.getDouble("injuries.bleed.damagePercent");
        this.damageInterval = userConfig.getInt("injuries.bleed.damageInterval");
        this.onInjuryMessage = userConfig.getString("injuries.bleed.onInjuryMessage");
        this.onRemedyMessage = userConfig.getString("injuries.bleed.onRemedyMessage");
        this.soundOnInjury = userConfig.getBoolean("injuries.bleed.soundOnInjure");
        this.soundOnDamage = userConfig.getBoolean("injuries.bleed.soundOnDamage");
    }

    /**
     * Displays the particle effect for the bleed injury on the given
     * player.
     * 
     * 
     * @param playerId      the id of the player to show particles
     * 
     */
    public void showParticles(UUID playerId) {
        Location where = Bukkit.getPlayer(playerId).getEyeLocation();
        Vector dirOffset = Bukkit.getPlayer(playerId).getLocation().getDirection();
        // Calculate the offset required to put the particle effect in
        // the player's field of view.
        dirOffset.setX(dirOffset.getX() * 0.25);
        dirOffset.setY(dirOffset.getY() * 0.25);
        dirOffset.setZ(dirOffset.getZ() * 0.25);
        // Move the position by the offset.(
        where.setX(where.getX() + dirOffset.getX());
        where.setY(where.getY() + dirOffset.getY());
        where.setZ(where.getZ() + dirOffset.getZ());
        // Define the options for the particle.
        Particle.DustOptions bloodOptions = new Particle.DustOptions(Color.RED, 1.00f);
        // And, finally, spawn the particles.
        Bukkit.getPlayer(playerId).spawnParticle(Particle.REDSTONE, where, 25, 0, 0, 0, bloodOptions);
    }

    /**
     * Displays the particle effect for the bleed injury on the given
     * player.
     * 
     * 
     * @param player        the player to show particles
     * 
     */
    public void showParticles(Player player) {
        this.showParticles(player.getUniqueId());
    }

    /**
     * Damages a player for the configured amount of damage.
     * 
     * @param playerId       the UUID for the player to damage.
     * 
     */
    public void applyDamage(UUID playerId) {
        // Retrieve all player information that we'll need for to damage them.
        // We are specifically not storing the player in memory, as we don't
        // want to make a copy every single time we damage them.
        String playerName = Bukkit.getPlayer(playerId).getName();
        double playerMaxHealth = Bukkit.getPlayer(playerId).getAttribute(
            Attribute.GENERIC_MAX_HEALTH
        ).getValue();
        double playerHealth = Bukkit.getPlayer(playerId).getHealth();
        // Calculate the damage based solely on the player's maximum health
        // and the configured percentage.
        double damage = playerMaxHealth * this.damagePercent; 
        this.getPlugin().debugInfo("(Injured.Bleed) Damaging " + playerName + " ...");
        this.getPlugin().debugInfo("(Injured.Bleed) " + playerName + " has " + String.valueOf(playerHealth) + " hp; bleeding them for " + String.valueOf(damage) + " ...");
        // The setHealth method throws an error if set below zero, so
        // we need to check for that.
        if (playerHealth >= damage) {
            Bukkit.getPlayer(playerId).setHealth(playerHealth - damage);
        } else if (playerHealth > 0.00) {
            Bukkit.getPlayer(playerId).setHealth(0.00);
        }
        // Finally, show the particles to the player.
        showParticles(playerId);
    }

    /**
     * Damages a player for the configured amount of damage.
     * 
     * @param player       the player to damage.
     * 
     */
    public void applyDamage(Player player) {
        this.applyDamage(player.getUniqueId());
    }

    /**
     * Returns the interval for the injury, if it exists.
     */

    // Overrides for Injury
    @Override
    public void sendInjuryMessage(Player player) {
        player.sendMessage(ChatColor.RED + this.onInjuryMessage);
    }

    @Override
    public void sendRemedyMessage(Player player) {
        player.sendMessage(ChatColor.YELLOW + this.onRemedyMessage);
    }

    @Override
    public boolean hasInterval() {
        return true;
    }

    @Override
    public int getInterval() {
        return this.damageInterval;
    }

    @Override
    public void onEffect(UUID playerId, boolean first) {
        if (Bukkit.getPlayer(playerId).getGameMode() != GameMode.CREATIVE
            && Bukkit.getPlayer(playerId).getGameMode() != GameMode.SPECTATOR) {
            this.getPlugin().debugInfo("(Injured.Bleed) " + Bukkit.getPlayer(playerId).getName() + " is bleeding.");
            this.applyDamage(playerId);
            if (first && this.soundOnInjury) {
                Bukkit.getPlayer(playerId).playSound(Bukkit.getPlayer(playerId).getEyeLocation(), Sound.BLOCK_HONEY_BLOCK_FALL, SoundCategory.AMBIENT, 1.0f, 1.0f);
            } else if (this.soundOnDamage) {
                Bukkit.getPlayer(playerId).playSound(Bukkit.getPlayer(playerId).getEyeLocation(), Sound.BLOCK_HONEY_BLOCK_SLIDE, SoundCategory.AMBIENT, 1.0f, 1.0f);
            }
        } else {
            this.getPlugin().debugInfo("(Injured.Bleed) " + Bukkit.getPlayer(playerId).getName() + " is in Creative mode. Will not damage them.");
        }
    }

    @Override
    public void onEffect(UUID playerId) {
        this.onEffect(playerId, false);
    }

    @Override
    public void onEffect(Player player) {
        this.onEffect(player.getUniqueId(), false);
    }

    @Override
    public boolean isScheduled() {
        return true;
    }
}
