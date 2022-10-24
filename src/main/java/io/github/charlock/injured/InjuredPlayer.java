package io.github.charlock.injured;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class InjuredPlayer {
    private final UUID uuid;
    private Map<InjuryType, Injury> injuries = new HashMap<InjuryType, Injury>();

    public InjuredPlayer(Player player) {
        this.uuid = player.getUniqueId();
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    public Collection<Injury> getInjuries() {
        return injuries.values();
    }

    public void addInjury(Injury injury, boolean overwrite) {
        if (injuries.containsKey(injury.injuryType)) {
            if (overwrite) {
                injuries.replace(injury.injuryType, injury);
            }
        } else {
            injuries.put(injury.injuryType, injury);
        }
    }

    public void addInjury(Injury injury) {
        this.addInjury(injury, false);
    }

    public void clearInjuries() {
        injuries.clear();
    }

    public void removeInjury(InjuryType injType) {
        if (injuries.containsKey(injType)) {
            injuries.remove(injType);
        }
    }

    public void removeInjury(Injury injury) {
        this.removeInjury(injury.injuryType);
    }

    public boolean hasInjury(Injury injury) {
        if (injuries.containsKey(injury.injuryType)) {
            if (injuries.get(injury.injuryType) == injury) {
                return true;
            }
        }
        return false;
    }
}
