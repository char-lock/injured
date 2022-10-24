package io.github.charlock.injured;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.EventPriority;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;

public class InjuryDeathListener implements Listener {
    InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    JavaPlugin injuredPlugin = injuryCaptain.getPlugin();

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerDeath(PlayerDeathEvent e) {
        // injuredPlugin.getLogger().info(e.deathMessage().toString());
        Player player = e.getEntity();
        String playerName = player.getName();
        if (e.deathMessage().toString().contains("death.attack.generic")) {
            injuredPlugin.getLogger().info(playerName + " killed by generic means ... checking for bleed.");
            if (injuryCaptain.hasInjury(player, new InjuryBleed(injuredPlugin))) {
                injuredPlugin.getLogger().info(playerName + " was bleeding. Changing death message.");
                TextComponent deathMessage = Component.text(
                    player.getName() + " has bled out."
                );
                e.deathMessage(deathMessage);
            }
        }

        if (injuryCaptain.trackingPlayer(player)) {
            injuredPlugin.getLogger().info("Clearing injuries for " + playerName + ".");
            injuryCaptain.clearInjuries(player);
        }
    }
}
