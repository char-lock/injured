package io.github.charlock.injured;

import java.util.Set;
import java.util.HashSet;
import java.util.UUID;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class InjuredPlayer {
    enum InjuredPlayerFlag {
        BLEEDING,
        CRIPPLED
    }

    private final Set<InjuredPlayerFlag> injuries = new HashSet<>();
    private final UUID uuid;

    public InjuredPlayer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void addInjury(InjuredPlayerFlag injury) {
        Bukkit.getLogger().info("Adding injury " + injury.name() + " to " + this.getPlayer().getName() + ".");
        this.injuries.add(injury);
    }

    public boolean hasInjury(InjuredPlayerFlag injury) {
        return this.injuries.contains(injury);
    }

    public void removeInjury(InjuredPlayerFlag injury) {
        Bukkit.getLogger().info("Removing injury " + injury.name() + " from " + this.getPlayer().getName() + ".");
        if (this.hasInjury(injury)) {
            this.injuries.remove(injury);
        }
    }

    public void clearInjuries() {
        this.injuries.clear();
    }
}
