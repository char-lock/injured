package io.github.charlock.injured;

import java.util.HashSet;
import java.util.Random;
// import java.util.UUID;
// import java.util.Vector;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
// import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


public class PlayerBleedListener implements Listener {
    private final Injured injured;

    private FileConfiguration config;
    private double bleedChance;
    // private int bleedAmplifier; // How often an effect is applied.
    // private int bleedDuration; // How long the effect lasts in ticks.
    private int bleedParticleCount;
    private HashSet<EntityDamageEvent.DamageCause> bleedCauses;
    private PotionEffect bleedEffect;
    private Random injuryDice = new Random();

    public PlayerBleedListener(Injured plugin) {
        injured = plugin;
        config = injured.getConfig();
        bleedChance = config.getDouble("bleedChance");
        // bleedAmplifier = config.getInt("bleedInterval");
        // bleedDuration = config.getInt("bleedDuration");
        bleedParticleCount = config.getInt("bleedParticleCount");
        bleedEffect = injured.getInjuryEffect("bleed");
        bleedCauses = new HashSet<>();
        bleedCauses.add(EntityDamageEvent.DamageCause.CONTACT);
        bleedCauses.add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        bleedCauses.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
        bleedCauses.add(EntityDamageEvent.DamageCause.PROJECTILE);
        bleedCauses.add(EntityDamageEvent.DamageCause.MAGIC);
        bleedCauses.add(EntityDamageEvent.DamageCause.THORNS);
        bleedCauses.add(EntityDamageEvent.DamageCause.CUSTOM);
    }


    /**
     * Returns an integer representing the range of numbers from which to roll.
     */
    private int getBleedSelection() {
        int bleedValues = 1;
        if (bleedChance < 1.00) {
            bleedValues = (int) Math.floor(1.00 / bleedChance);
        }
        return bleedValues;
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent e) {
        Entity damagee = e.getEntity();
        EntityDamageEvent.DamageCause cause = e.getCause();

        // Check to ensure that we're dealing with a player,
        // as bleed should only be applied to a player, and
        // this particular event is a bit broad on application.
        if (damagee.getType() == EntityType.PLAYER) {
            Player player = (Player) damagee;
            String playerName = player.getName();
            Location where = player.getEyeLocation();
            Vector dirOffset = player.getLocation().getDirection();
            dirOffset.setX(dirOffset.getX() * 0.25);
            dirOffset.setY(dirOffset.getY() * 0.25);
            dirOffset.setZ(dirOffset.getZ() * 0.25);
            where.setX(where.getX() + dirOffset.getX());
            where.setY(where.getY() + dirOffset.getY());
            where.setZ(where.getZ() + dirOffset.getZ());

            // UUID playerId = player.getUniqueId();
            // Now, we need to check the cause of the damage,
            // As there are only a handful of damages that
            // can proc bleed.
            if (bleedCauses.contains(cause)) {
                injured.getLogger().info(playerName + " has been hit by something that can cause bleed.");
                // It's time to make sure that the player isn't already bleeding.
                // There's no need to apply bleeding again if the player is already there.
                boolean isBleeding = false;
                if (injured.trackingPlayer(player)) {
                    if (injured.hasInjury(player, InjuredPlayer.InjuredPlayerFlag.BLEEDING)) {
                        injured.getLogger().info(playerName + " is already bleeding. No need to apply again.");
                        isBleeding = true;
                    }
                }
                if (!isBleeding) {
                    int bleedRoll = injuryDice.nextInt(getBleedSelection()) + 1;
                    if (bleedRoll == 1) {
                        // Life dropped an epic husband, the rarest of its loot table.
                        // And I was lucky enough to win the bleed roll.
                        injured.getLogger().info(playerName + " rolled a 1 for bleed; applying now.");
                        injured.addInjury(player, InjuredPlayer.InjuredPlayerFlag.BLEEDING);
                        player.addPotionEffect(bleedEffect);
                    } else {
                        injured.getLogger().info(playerName + " avoided getting bleed this time. Rolled " + Integer.toString(bleedRoll));
                    }
                }
            } else if (cause == EntityDamageEvent.DamageCause.POISON) {
                // This is to handle the particle distribution when the player
                // is hurt by bleeding.
                if (injured.hasInjury(player, InjuredPlayer.InjuredPlayerFlag.BLEEDING)) {
                    Particle.DustOptions blood = new Particle.DustOptions(Color.RED, 1.00f);
                    player.spawnParticle(Particle.REDSTONE, where, bleedParticleCount, 0, 0, 0, blood);
                    // Also going to go ahead and handle the issue of leaving the player with half of a heart.
                    double displayedHealth = player.getHealth();
                    // injured.getLogger().info("Bleed damage applied -- " + playerName + " at " + Double.toString(player.getHealth()));
                    if (displayedHealth <= 2.00) {
                        player.damage(player.getHealth());
                        player.setLastDamageCause( new EntityDamageEvent(player, EntityDamageEvent.DamageCause.THORNS, player.getHealth()));
                    }
                }
            }
        }
    }
}