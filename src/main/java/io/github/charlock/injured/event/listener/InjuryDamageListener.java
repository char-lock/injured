package io.github.charlock.injured.event.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.InjuryCaptain;
import io.github.charlock.injured.injury.Injury;
import io.github.charlock.injured.injury.InjuryType;


/**
 * A Listener for EntityDamageEvent that applies injuries as necessary.
 * 
 * @see Listener
 * 
 */
public class InjuryDamageListener implements Listener {
    private InjuryCaptain injuryCaptain;
    private InjuredPlugin injuredPlugin;

    public InjuryDamageListener() {
        injuryCaptain = InjuryCaptain.getCaptain();
        injuredPlugin = this.injuryCaptain.getPlugin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity().getType() == EntityType.PLAYER) {
            this.injuredPlugin.debugInfo(
                "(InjuryDamageListener) Confirmed entity was a player. "
                + "Checking injuries against cause ..."
            );
            for (Injury i : this.injuryCaptain.getInjuryMapping().values()) {
                this.injuredPlugin.debugInfo(
                    "(InjuryDamageListener) Checking if we should roll"
                    + " for [" + i.getName() + "] ..."
                );
                if (i.getCauses().contains(e.getCause())) {
                    if (i.getType() == InjuryType.CRIPPLED && e.getCause() == DamageCause.FALL && e.getEntity().getFallDistance() < 6) {
                        this.injuredPlugin.debugInfo(
                            "(InjuryDamageListener) "
                            + ((Player)e.getEntity()).getName()
                            + " has not fallen enough blocks to be injured."
                        );
                    } else {
                        this.injuredPlugin.debugInfo(
                            "(InjuryDamageListener) "
                            + ((Player)e.getEntity()).getName()
                            + " has been hit by a damage that could cause ["
                            + i.getName() + "]. Rolling ..."
                        );
                        if (i.rollInjury()) {
                            this.injuredPlugin.debugInfo(
                                "(InjuryDamageListener) Uh oh -- "
                                + ((Player)e.getEntity()).getName()
                                + " has rolled low enough to be injured with ["
                                + i.getName() + "]. Adding ..."
                            );
                            this.injuryCaptain.addInjury(((Player)e.getEntity()).getUniqueId(), i);
                            i.sendInjuryMessage((Player)e.getEntity());
                        } else {
                            this.injuredPlugin.debugInfo(
                                "(InjuryDamageListener) Phew! "
                                + ((Player)e.getEntity()).getName()
                                + " rolled high enough not to be injured by ["
                                + i.getName() + "]."
                            );
                        }
                    }
                }
            }
        }
    }
}