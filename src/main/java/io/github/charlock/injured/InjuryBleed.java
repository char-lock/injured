package io.github.charlock.injured;


import java.util.Random;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
// import org.bukkit.event.entity.EntityDamageEvent;
// import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

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
        double damage = player.getHealth() * damagePercent;

        
    }


    @Override
    public void onEffect(Player player) {
        injuredPlugin.getLogger().info(player.getName() + " is bleeding out.");
        applyDamage(player);
    }
}
