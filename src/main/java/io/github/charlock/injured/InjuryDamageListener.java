package io.github.charlock.injured;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Arrays;

import org.bukkit.event.EventPriority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * A Listener for EntityDamageEvent that applies injuries as necessary.
 * 
 * @see Listener
 * 
 */
public class InjuryDamageListener implements Listener{
    private InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private JavaPlugin injuredPlugin = injuryCaptain.getPlugin();
    private Map<InjuryType, HashSet<DamageCause>> injuryCauses;
    private Map<InjuryType, Injury> injuries;

    /** Constructor for InjuryDamageListener */
    public InjuryDamageListener() {
        injuryCauses = new HashMap<InjuryType, HashSet<DamageCause>>();
        injuries = new HashMap<InjuryType, Injury>();
        HashSet<DamageCause> bleedCauses = new HashSet<DamageCause>(Arrays.asList(
            DamageCause.CONTACT,
            DamageCause.ENTITY_ATTACK,
            DamageCause.ENTITY_SWEEP_ATTACK,
            DamageCause.PROJECTILE,
            DamageCause.MAGIC,
            DamageCause.THORNS,
            DamageCause.CUSTOM
        ));
        this.injuryCauses.put(InjuryType.BLEEDING, bleedCauses);
        injuries.put(InjuryType.BLEEDING, new InjuryBleed(injuredPlugin));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent e){
        Entity damagee = e.getEntity();
        DamageCause cause = e.getCause();

        // Check to ensure that we are dealing with a player.
        if (damagee.getType() == EntityType.PLAYER) {
            Player player = (Player) damagee;
            String playerName = player.getName();
            // Checking to ensure the player doesn't have an injury already.
            if (!(cause == DamageCause.CUSTOM && injuryCaptain.hasInjury(player, injuries.get(InjuryType.BLEEDING)))) {
            // Check for injuries and apply as necessary.
                injuredPlugin.getLogger().info(playerName + " hit. Checking if an injury should be rolled.");
                for (InjuryType i : injuryCauses.keySet()) {
                    if (injuryCauses.get(i).contains(cause)) {
                        injuredPlugin.getLogger().info(playerName + " has been hit by something that can cause " + i.name());
                        if (injuries.get(i).onRoll(cause)) {
                            injuredPlugin.getLogger().info("Bad luck! Applying bleed to " + playerName + ".");
                            injuryCaptain.addInjury(player, injuries.get(i));
                        } else {
                            injuredPlugin.getLogger().info("Phew! " + playerName + " rolled high enough to not get bleed.");    
                        }
                    }
                }
            }   
        }
    }
}
