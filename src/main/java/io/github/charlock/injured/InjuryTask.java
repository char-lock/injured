package io.github.charlock.injured;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.charlock.injured.event.InjuryCancelEvent;
import io.github.charlock.injured.injury.Injury;


/**
 * A scheduled task to apply an injuries effects at a set interval.
 * 
 */
public class InjuryTask extends BukkitRunnable implements Listener {
    private InjuredPlugin injuredPlugin;
    
    private Injury injury;
    private UUID injured;

    /**
     * Constructor for an InjuryTask.
     * 
     * 
     * @param playerId      id of player for which to schedule injury
     * 
     * @param injury        injury to schedule.
     *  
     */
    public InjuryTask(UUID playerId, Injury injury) {
        this.injuredPlugin = InjuryCaptain.getCaptain().getPlugin();
        this.injured = playerId;
        this.injury = injury;
        this.injuredPlugin.getServer().getPluginManager().registerEvents(this, this.injuredPlugin);
    }

    /**
     * Constructor for an InjuryTask.
     * 
     * 
     * @param player        player for which to schedule injury
     * 
     * @param injury        injury to schedule.
     *  
     */
    public InjuryTask(Player player, Injury injury) {
        this(player.getUniqueId(), injury);
    }

    // Overrides for BukkitRunnable methods
    @Override
    public void run() {
        this.injury.onEffect(this.injured);
    }

    // EventHandler for InjuryCancelEvent
    @EventHandler
    public void onInjuryCancel(InjuryCancelEvent e) {
        this.injuredPlugin.debugInfo("(InjuryTask) Heard a cancel event.");
        if (e.getInjuredId() == this.injured) {
            if (e.getInjuryType() == this.injury.getType()) {
                this.cancel();
                InjuryCancelEvent.getHandlerList().unregister(this);
            }
        }
    }
}
