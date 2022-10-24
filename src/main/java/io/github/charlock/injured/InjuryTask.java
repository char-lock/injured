package io.github.charlock.injured;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class InjuryTask extends BukkitRunnable {

    private Player injuredPlayer;
    private Injury injury;

    public InjuryTask(Player player, Injury injury) {
        this.injuredPlayer = player;
        this.injury = injury;
    }

    @Override
    public void run() {
        injury.onEffect(injuredPlayer);
    }
}
