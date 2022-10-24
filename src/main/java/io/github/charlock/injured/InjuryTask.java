package io.github.charlock.injured;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class InjuryTask extends BukkitRunnable implements Listener {
    private Player injuredPlayer;
    private Injury injury;
    private JavaPlugin injuredPlugin = InjuryCaptain.getCaptain().getPlugin();

    public InjuryTask(Player player, Injury injury) {
        this.injuredPlayer = player;
        this.injury = injury;
        this.injuredPlugin.getServer().getPluginManager().registerEvents(this, this.injuredPlugin);
    }

    @Override
    public void run() {
        injury.onEffect(injuredPlayer.getUniqueId());
    }

    @EventHandler
    public void onInjuryCancel(InjuryCancelEvent e) {
        Bukkit.getLogger().info("Heard cancel event!");
        if (e.getInjured().getName() == this.injuredPlayer.getName()){
            if (e.getInjury().injuryType == this.injury.injuryType) {
                this.cancel();
                InjuryCancelEvent.getHandlerList().unregister(this);
            }
        }
    }
}
