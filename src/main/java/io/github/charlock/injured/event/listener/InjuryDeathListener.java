package io.github.charlock.injured.event.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.InjuryCaptain;
import io.github.charlock.injured.injury.InjuryType;

/**
 * A Listener for PlayerDeathEvent that replaces the death message if
 * the player's death was the result of an injury and clears them of
 * all injuries.
 * 
 * @see Listener
 * 
*/
public class InjuryDeathListener implements Listener {
    private InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (e.deathMessage().toString().contains("death.attack.generic")) {
            this.injuredPlugin.debugInfo(
                "(InjuryDeathListener) " + e.getEntity().getName()
                + " was killed by a generic attack. Checking injuries ..."
            );
            if (injuryCaptain.hasInjury(e.getEntity(), InjuryType.BLEEDING)) {
                this.injuredPlugin.debugInfo(
                    "(InjuryDeathListener) "
                    + e.getEntity().getName() + " was bleeding at the time"
                    + " of death; changing death message ..."
                );
                TextComponent deathMessage = Component.text(
                    e.getEntity().getName() + " has bled out."
                );
                e.deathMessage(deathMessage);
            }
        }

        if (this.injuryCaptain.trackingPlayer(e.getEntity())) {
            this.injuredPlugin.debugInfo("Clearing all injuries from " + e.getEntity().getName() + " ...");
            if (this.injuryCaptain.hasInjury(e.getEntity(), InjuryType.CRIPPLED)) {
                float slowPercent = (float)this.injuredPlugin.getConfig().getDouble("injuries.crippled.slowPercent");
                e.getEntity().setWalkSpeed(e.getEntity().getWalkSpeed() / slowPercent);
            }
            this.injuryCaptain.clearInjuries(e.getEntity());
        }
    }
}
