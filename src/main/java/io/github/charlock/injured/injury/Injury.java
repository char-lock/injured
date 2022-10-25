package io.github.charlock.injured.injury;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.InjuryCaptain;


/**
 * A generic parent class to encompass all injuries.
 * 
 */
public abstract class Injury {
    private final InjuryCaptain injuryCaptain;
    private final JavaPlugin pluginInjured;
    private final InjuryType injuryType;
    private Set<DamageCause> causes;
    private double chancePercent;
    private int tickDuration;

    /**
     * Constructor to create an Injury.
     * 
     */
    public Injury(InjuryType injType) {
        this.injuryCaptain = InjuryCaptain.getCaptain();
        this.pluginInjured = this.injuryCaptain.getPlugin();
        this.causes = new HashSet<DamageCause>();
        this.injuryType = injType;
        this.tickDuration = Integer.MAX_VALUE;
    }

    public InjuredPlugin getPlugin() {
        return (InjuredPlugin)this.pluginInjured;
    }

    /**
     * Returns what DamageCauses could result in this injury.
     * 
     */
    public Collection<DamageCause> getCauses() {
        return this.causes;
    }

    /**
     * Sets the causes for this injury.
     * 
     * 
     * @param causes        HashSet of DamageCauses for this injury
     * 
     */
    public void setCauses(HashSet<DamageCause> causes) {
        this.causes = causes;
    }

    /**
     * Returns the chance of getting the injury as a value between 0
     * and 1, inclusive.
     * 
     */
    public double getChancePercent() {
        return this.chancePercent;
    }

    /**
     * Sets the chance of getting the injury. Any value below 0 will
     * default to 0, and any value above 1 will default to 1.
     * 
     * 
     * @param chance        the chance of getting injured as a value
     *                      between 0 and 1.
     * 
     */
    public void setChancePercent(double chance) {
        if (chance < 0) {
            this.chancePercent = 0.00;
        } else if (chance > 1) {
            this.chancePercent = 1.00;
        } else {
            this.chancePercent = chance;
        }
    }

    /** 
     * Returns the duration of the injury in ticks.
     * 
     */
    public int getDuration() {
        return this.tickDuration;
    }

    /**
     * Sets the duration of the injury in ticks. Any value less than
     * 0 will be set to an infinite duration.
     * 
     * 
     * @param duration      duration of injury in ticks.
     * 
     */
    public void setDuration(int duration) {
        if (duration < -1) {
            this.tickDuration = -1;
        } else {
            this.tickDuration = duration;
        }
    }

    /** 
     * Returns whether or not the injury should be processed.
     * 
     */
    public boolean rollInjury() {
        if (chancePercent < 0) return false;
        int chanceRange = 100;
        double chance = chancePercent * 100;
        if (chance % 1 > 0) {
            // We'll keep multiplying the range and the chance
            // by 10 until we get an integer.
            chance *= 10;
            chanceRange *= 10;
        }
        Random random = new Random();
        int roll = random.nextInt(chanceRange);
        return (roll < chance);
    }

    /**
     * Returns the name of the injury as defined by the type.
     * 
     */
    public String getName() {
        return this.injuryType.name().toLowerCase();
    }

    /**
     * Returns the type of injury.
     * 
     */
    public InjuryType getType() {
        return this.injuryType;
    }

    /**
     * Starts the effects of the injury for the given player.
     * 
     * 
     * @param playerId      the UUID for the target player.
     * 
     * @param first         whether or not this is the first time
     *                      this injury is hurting the player
     * 
     */
    abstract public void onEffect(UUID playerId, boolean first);

    /**
     * Starts the effects of the injury for the given player.
     * 
     * 
     * @param playerId      the UUID for the target player.
     * 
     */
    abstract public void onEffect(UUID playerId);

    /**
     * Starts the effects of the injury for the given player.
     * 
     * 
     * @param player        the target player
     * 
     */
    abstract public void onEffect(Player player);

    /**
     * Checks whether or not the given DamageCause can cause the
     * injury.
     * 
     * 
     * @param cause         cause to check
     * 
     */
    public boolean isCausedBy(DamageCause cause) {
        return causes.contains(cause);
    }

    /**
     * Returns whether or not this injury has reapplying effects.
     * 
     */
    public abstract boolean hasInterval();

    /**
     * Returns the interval for the injury, if it exists.
     * 
     */
    public abstract int getInterval();

    /**
     * Sends a message to a player that they have been injured.
     * 
     * 
     * @param player        player to message
     * 
     */
    public abstract void sendInjuryMessage(Player player);

    /**
     * Sends a message to a player that they have been healed.
     * 
     * 
     * @param player        player to message
     * 
     */
    public abstract void sendRemedyMessage(Player player);

    /**
     * Returns whether or not the injury should be scheduled for future effects.
     * 
     */
    public abstract boolean isScheduled();
}
