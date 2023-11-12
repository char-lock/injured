package io.github.charlock.injured.event.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Collection;

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

    private void debugInfo(String msg) {
        this.injuredPlugin.debugInfo("(InjuryDamageListener) " + msg);
    }

    private Collection<Injury> getDefinedInjuries() {
        return this.injuryCaptain.getInjuryMapping().values();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getEntity().getType() != EntityType.PLAYER) return;
        this.debugInfo("Confirmed entity was a player. Checking injuries against cause ...");
        Player injuredPlayer = (Player)e.getEntity();
        String injuredName = injuredPlayer.getName();
        boolean playerFell = e.getCause() == DamageCause.FALL;
        boolean wasFallHighEnough = playerFell && injuredPlayer.getFallDistance() >= 6;
        // TODO: Rewrite the injury check to use a dictionary lookup of cause to injury.
        for (Injury knownInjury : this.getDefinedInjuries()) {
            // We can move on if the injury doesn't have the cause we're looking for.
            if (!knownInjury.getCauses().contains(e.getCause())) continue;
            // Special check for the `Crippled` injury to ensure the height was high enough.
            if (knownInjury.getType() == InjuryType.CRIPPLED && playerFell && !wasFallHighEnough) {
                this.debugInfo(injuredName + " fell, but it was not high enough to cause injury.");
                continue;
            }
            this.debugInfo("Phew! " + injuredName
                + " has been hit by a damage that could cause ["
                + knownInjury.getName() + "]. Rolling ...");
            boolean isInjured = knownInjury.rollInjury();
            if (!isInjured) {
                this.debugInfo(injuredName + " rolled high enough not to be injured by ["
                    + knownInjury.getName() + "].");
                continue;
            }
            this.debugInfo(injuredName
                + " rolled low enough to be injured with ["
                + knownInjury.getName() + "]. Adding ...");
            this.injuryCaptain.addInjury(injuredPlayer.getUniqueId(), knownInjury);
            knownInjury.sendInjuryMessage(injuredPlayer);
        }
    }
}
