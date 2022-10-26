package io.github.charlock.injured.event.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerVelocityEvent;

import io.github.charlock.injured.InjuryCaptain;


/**
 * A Listener for the PlayerVelocityEvent in order to counteract any
 * additional plugins a user may have that would also affect the player's
 * walking speed.
 * 
 */
public class InjuryVelocityListener implements Listener {
    private final InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerVelocity(PlayerVelocityEvent e) {
        float playerWalkSpeed = e.getPlayer().getWalkSpeed();
        float targetSpeed = injuryCaptain.getInjuredSpeed(e.getPlayer().getUniqueId());
        if (playerWalkSpeed > targetSpeed) {
            e.getPlayer().setWalkSpeed(targetSpeed);
        } else if (playerWalkSpeed < targetSpeed) {
            this.injuryCaptain.setInjuredSpeed(e.getPlayer().getUniqueId(), playerWalkSpeed);
        }
    }
}
