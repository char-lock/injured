package io.github.charlock.injured.event.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
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
        Player deadPlayer = e.getEntity();
        // We need to check generic deaths to see if the player died via bleeding.
        // This will allow us to swap out the message to be more appropriate.
        boolean isGenericDeath = e.deathMessage().toString().contains("death.attack.generic");
        boolean wasPlayerBleeding = this.injuryCaptain.hasInjury(deadPlayer, InjuryType.BLEEDING);
        if (isGenericDeath && wasPlayerBleeding) {
            String playerName = deadPlayer.getName();
            TextComponent deathMessage = Component.text(playerName + " has bled out.");
            e.deathMessage(deathMessage);
        }
        // This is a special check to fix a crippled player's walk speed.
        boolean wasPlayerCrippled = this.injuryCaptain.hasInjury(deadPlayer, InjuryType.CRIPPLED);
        if (wasPlayerCrippled) {
            float slowPercent = (float)this.injuredPlugin.getConfig().getDouble("injuries.crippled.slowPercent");
            float correctedWalkSpeed = deadPlayer.getWalkSpeed() / slowPercent;
            deadPlayer.setWalkSpeed(correctedWalkSpeed);
        }
        // We need to clear the player of all injuries now that they have died.
        // At least, if we were tracking them.
        boolean isTrackingInjuries = this.injuryCaptain.trackingPlayer(deadPlayer);
        if (isTrackingInjuries) this.injuryCaptain.clearInjuries(deadPlayer);
    }

}
