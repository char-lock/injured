package io.github.charlock.injured;


// import java.awt.Component;
// import java.util.Map;
// import java.util.UUID;

// import net.kyori.adventure.text.TextComponent;
// import org.bukkit.entity.Entity;
// import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;


public class PlayerDeathListener implements Listener {
    private final Injured injured;

    public PlayerDeathListener(Injured plugin) {
        injured = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        String playerName = player.getName();
        // UUID playerId = player.getUniqueId();
        // injured.getLogger().info(e.deathMessage().toString());
        boolean messageGagged = false;
        if (e.deathMessage().toString().contains("death.attack.magic")) {
            injured.getLogger().info(playerName + " killed by magic ... checking for bleed.");
            if (injured.hasInjury(player, InjuredPlayer.InjuredPlayerFlag.BLEEDING)){
                injured.getLogger().info(playerName + " was bleeding. Gagging message.");
                // TODO: Custom death message for bleeding.
                e.deathMessage(null);
                messageGagged = true;
            }
        }

        if (injured.trackingPlayer(player)) {
            if (messageGagged) {
                injured.getLogger().info("Death detected. Clearing injuries from " + playerName);
                injured.clearInjuries(player);
            }
        }
    }
}