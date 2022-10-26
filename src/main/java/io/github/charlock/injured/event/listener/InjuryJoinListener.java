package io.github.charlock.injured.event.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.InjuryCaptain;
import io.github.charlock.injured.InjuryTimerTask;
import io.github.charlock.injured.injury.Injury;


/**
 * A Listener for PlayerJoinEvent in order to reschedule any injuries
 * a player may have had at the time they last logged out.
 * 
 */
public class InjuryJoinListener implements Listener {
    private final InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private final InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();

    @EventHandler
    public void onLogin(PlayerJoinEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();
        this.injuredPlugin.debugInfo("(InjuryLoginListener) " + e.getPlayer().getName() + " has logged in. Checking injuries ...");
        if (this.injuryCaptain.trackingPlayer(playerId)) {
            this.injuredPlugin.debugInfo("(InjuryLoginListener) " + e.getPlayer().getName() + " was injured when they quit. Rescheduling injuries ...");
            for (Injury i : this.injuryCaptain.getInjuries(playerId)) {
                i.sendInjuryMessage(e.getPlayer());
                if (i.isScheduled()) {
                    this.injuryCaptain.scheduleInjury(playerId, i);
                }
                if (i.getDuration() > 0) {
                    new InjuryTimerTask(playerId, i.getType()).runTaskLater(this.injuredPlugin, i.getDuration());
                }
            }
        } else {
            this.injuryCaptain.trackPlayer(e.getPlayer().getUniqueId());
        }
        this.injuredPlugin.debugInfo("(InjuryLoginListener) Checking speed at login ...");
        if (e.getPlayer().getWalkSpeed() != 0.2f) {
            e.getPlayer().setWalkSpeed(0.2f);
        }
    }
}
