package io.github.charlock.injured;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import io.github.charlock.injured.event.InjuryCancelEvent;
import io.github.charlock.injured.injury.InjuryType;


/**
 * A task representing the remedy of an injury after its duration has
 * ended.
 * 
 */
public class InjuryTimerTask extends BukkitRunnable implements Listener {
    private final InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private final InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();
    private UUID injured;
    private InjuryType injuryType;

    /**
     * Constructor for InjuryTimerTask.
     * 
     * 
     * @param playerId      id of player to schedule timer
     * 
     * @param injType       type of injury to remedy once time is up.
     * 
     */
    public InjuryTimerTask(UUID playerId, InjuryType injType) {
        this.injured = playerId;
        this.injuryType = injType;
        this.injuredPlugin.getServer().getPluginManager().registerEvents(this, this.injuredPlugin);
    }

    /**
     * Routine for remedying the Crippled injury.
     * 
     */
    private void remedyCrippled() {
        this.injuryCaptain.getInjuryMapping().get(InjuryType.CRIPPLED).sendRemedyMessage(Bukkit.getPlayer(this.injured));
        float slowPercent = (float)this.injuredPlugin.getConfig().getDouble("injuries.crippled.slowPercent");
        Bukkit.getPlayer(this.injured).setWalkSpeed(Bukkit.getPlayer(this.injured).getWalkSpeed() / slowPercent);
        this.injuryCaptain.removeInjury(this.injured, InjuryType.CRIPPLED);
    }

    /**
     * Routine for remedying the Bleed injury.
     * 
     */
    private void remedyBleed() {
        this.injuryCaptain.getInjuryMapping().get(InjuryType.BLEEDING).sendRemedyMessage(Bukkit.getPlayer(this.injured));
        this.injuryCaptain.removeInjury(this.injured, InjuryType.BLEEDING);
    }

    @Override
    public void run() {
        if (this.injuryType == InjuryType.BLEEDING) {
            remedyBleed();
        } else if (this.injuryType == InjuryType.CRIPPLED) {
            remedyCrippled();
        }
    }

    @EventHandler
    public void onInjuryCancel(InjuryCancelEvent e) {
        this.injuredPlugin.debugInfo("(InjuryTimerTask) Heard cancel event.");
        if (this.injured == e.getInjuredId() && this.injuryType == e.getInjuryType()) {
            this.cancel();
            InjuryCancelEvent.getHandlerList().unregister(this);
        }
    }
}
