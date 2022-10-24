package io.github.charlock.injured;


import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
// import org.bukkit.event.entity.EntityDamageEvent;
// import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.attribute.Attribute;
import org.bukkit.Particle;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.bukkit.Color;


// import net.kyori.adventure.text.Component;
// import net.kyori.adventure.text.TextComponent;




/** Implements the bleeding injury. */
public class InjuryBleed extends Injury{
    private JavaPlugin injuredPlugin;
    private FileConfiguration config;
    private double damagePercent;
    private double chancePercent;

    public InjuryBleed(JavaPlugin plugin) {
        super(plugin, "BLEEDING", InjuryType.BLEEDING);
        injuredPlugin = plugin;
        config = plugin.getConfig();
        this.tickInterval = config.getInt("bleedInterval");
        this.damagePercent = config.getDouble("bleedDamage");
        this.chancePercent = config.getDouble("bleedChance");
    }


    @Override
    public boolean onRoll(DamageCause cause) {
        Random random = new Random();
        int chanceRange = 100;
        double chance = chancePercent * 100;
        while (chance < 1) {
            chance *= 10;
            chanceRange *= 10;
        }
        int thisRoll = random.nextInt(chanceRange);
        injuredPlugin.getLogger().info("Bleed rolled a " + String.valueOf(thisRoll) + " --  under " + String.valueOf(chance) + " injures player.");
        return thisRoll <= chance;
    }


    public void applyDamage(Player player) {
        // First, we need to calculate how much damage should be
        // applied to the given player.
        injuredPlugin.getLogger().info("Applying bleed damage to " + player.getName() + ".");
        double damage = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() * damagePercent;
        injuredPlugin.getLogger().info("Has " + String.valueOf(player.getHealth()) + " -- hitting for " + String.valueOf(damage) + ".");
        if (player.getHealth() >= damage) {
            player.setHealth(player.getHealth() - damage);
        } else {
            player.setHealth(0);
        }
        showParticles(player);
    }


    public void showParticles(Player player) {
        Location where = player.getEyeLocation();
        Vector dirOffset = player.getLocation().getDirection();
        // Moves bleeding effect into the player line-of-sight.
        dirOffset.setX(dirOffset.getX() * 0.25);
        dirOffset.setY(dirOffset.getY() * 0.25);
        dirOffset.setZ(dirOffset.getZ() * 0.25);
        where.setX(where.getX() + dirOffset.getX());
        where.setY(where.getY() + dirOffset.getY());
        where.setZ(where.getZ() + dirOffset.getZ());
        Particle.DustOptions blood = new Particle.DustOptions(Color.RED, 1.00f);
        player.spawnParticle(Particle.REDSTONE, where, 25, 0, 0, 0, blood);
    }


    @Override
    public void onEffect(UUID playerId) {
        injuredPlugin.getLogger().info(Bukkit.getPlayer(playerId).getName() + " is bleeding out.");
        applyDamage(Bukkit.getPlayer(playerId));
    }
}
