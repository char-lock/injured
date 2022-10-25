package io.github.charlock.injured.event.listener;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.charlock.injured.InjuryCaptain;
import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.event.InjuryCancelEvent;
import io.github.charlock.injured.injury.Injury;


/**
 * A Listener for the PlayerQuitEvent in order to remove any scheduled
 * injuries from the list. We will still maintain their injuries, though.
 * 
 */
public class InjuryQuitListener implements Listener {
    private final InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private final InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID playerId = e.getPlayer().getUniqueId();
        this.injuredPlugin.debugInfo("(InjuryQuitListener) " + e.getPlayer().getName() + " has quit. Unscheduling injuries ...");
        if (this.injuryCaptain.trackingPlayer(playerId)) {
            for (Injury i : this.injuryCaptain.getInjuries(playerId)) {
                if (i.isScheduled() || i.getDuration() > 0) {
                    InjuryCancelEvent cancel = new InjuryCancelEvent(playerId, i.getType());
                    this.injuredPlugin.getServer().getPluginManager().callEvent(cancel);
                }
            }
        }
    }
}
