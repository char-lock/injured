package io.github.charlock.injured.event.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.InjuryCaptain;


/**
 * A Listener for the PlayerVelocityEvent in order to counteract any
 * additional plugins a user may have that would also affect the player's
 * walking speed.
 * 
 */
public class InjuryMoveListener implements Listener {
    private final InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private final InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent e) {
        this.injuredPlugin.debugInfo("(InjuryVelocityListener) Detected velocity change for " + e.getPlayer().getName() + ". Checking speed ...");
        float playerWalkSpeed = e.getPlayer().getWalkSpeed();
        float targetSpeed = injuryCaptain.getInjuredSpeed(e.getPlayer().getUniqueId());
        this.injuredPlugin.debugInfo("(InjuryVelocityListener) Details for " + e.getPlayer().getName() + ": " );
        this.injuredPlugin.debugInfo("    Current Speed: " + String.valueOf(playerWalkSpeed));
        this.injuredPlugin.debugInfo("    Target Speed: " + String.valueOf(targetSpeed));
        if (playerWalkSpeed != targetSpeed) {
            this.injuredPlugin.debugInfo("(InjuryVelocityListener) Target is different from current. Updating ...");
            this.injuryCaptain.updateWalkSpeed(e.getPlayer().getUniqueId(), playerWalkSpeed);
        }
    }
}
