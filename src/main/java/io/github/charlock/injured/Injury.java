package io.github.charlock.injured;


import java.util.UUID;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;


/** A generic parent class for all injuries. */
public abstract class Injury {
    public String injuryName;
    public InjuryType injuryType;
    public int tickInterval;
    public JavaPlugin pluginInjured;


    public Injury(JavaPlugin plugin, String name, InjuryType injType) {
        this.injuryName = name;
        this.pluginInjured = plugin;
        this.injuryType = injType;
    }

    /** Returns the stored name of the injury as a string. */
    public String getName() {
        return injuryName;
    }

    /** Returns the tick interval for the injury as an integer. */
    public int getInterval() {
        return tickInterval;
    }

    abstract void onEffect(UUID playerId);
    abstract boolean onRoll(DamageCause cause);
}
