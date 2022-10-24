package io.github.charlock.injured;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.java.JavaPlugin;


public class InjuryCaptain {
    private static InjuryCaptain injuryCaptain = null;
    private static JavaPlugin injuredPlugin;
    private Map<UUID, InjuredPlayer> injuredPlayers;
    private Map<UUID, HashMap<InjuryType, BukkitTask>> injurySchedules;
    private Map<InjuryType, Injury> injuries; 

    private InjuryCaptain() {
        injuredPlayers = new HashMap<UUID, InjuredPlayer>();
        injurySchedules = new HashMap<UUID, HashMap<InjuryType, BukkitTask>>();
        injuries = new HashMap<InjuryType, Injury>();
    }

    public void updateInjuries() {
        injuries.put(InjuryType.BLEEDING, new InjuryBleed(injuredPlugin));
    }

    public static InjuryCaptain getCaptain() {
        if (injuryCaptain == null) {
            injuryCaptain = new InjuryCaptain();
        }
        return injuryCaptain;
    }

    public static void updatePlugin(JavaPlugin plugin) {
        getCaptain();
        injuredPlugin = plugin;
    }

    public JavaPlugin getPlugin() {
        return injuredPlugin;
    }

    /** Returns whether or not a player is being tracked for injuries. */
    public boolean trackingPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        return injuredPlayers.containsKey(playerId);
    }

    /** Returns whether or not a player has an injury. */
    public boolean hasInjury(Player player, Injury injury) {
        if (trackingPlayer(player)) {
            return injuredPlayers.get(player.getUniqueId()).hasInjury(injury);
        }
        return false;
    }

    /** Adds an injury to a player. */
    public void addInjury(Player player, Injury injury) {
        injuredPlugin.getLogger().info("InjuryCaptain: Adding " + injury.injuryType.name() + " to " + player.getName() + ".");
        UUID pUuid = player.getUniqueId();
        if (!trackingPlayer(player)) {
            injuredPlayers.put(pUuid, new InjuredPlayer(player));
        }
        InjuredPlayer currentInjuries = injuredPlayers.get(pUuid);
        currentInjuries.addInjury(injury);
        injuredPlayers.replace(pUuid, currentInjuries);
        injury.onEffect(pUuid);
        scheduleInjury(pUuid, injury);
    }

    /** Schedules an injury to reoccur. */
    private void scheduleInjury(UUID playerId, Injury injury) {
        injuredPlugin.getLogger().info("Scheduling " + Bukkit.getPlayer(playerId).getName() + " for " + injury.injuryType.name() + ".");

        HashMap<InjuryType, BukkitTask> schedule;
        if (injurySchedules.containsKey(playerId)) {
            schedule = injurySchedules.get(playerId);
        } else {
            schedule = new HashMap<InjuryType, BukkitTask>();
            injurySchedules.put(playerId, schedule);
        }
        
        if (schedule.containsKey(injury.injuryType)) {
            cancelInjury(playerId, injury);
            injurySchedules.get(playerId).remove(injury.injuryType);
        }

        BukkitTask injuryTask = new InjuryTask(
                Bukkit.getPlayer(playerId),
                injury
            ).runTaskTimer(
                injuredPlugin,
                injury.tickInterval,
                injury.tickInterval
            );
        schedule.put(injury.injuryType, injuryTask);
        injurySchedules.replace(playerId, schedule);
    }

    /** Removes an injury from a player. */
    public void removeInjury(Player player, Injury injury) {
        UUID pUuid = player.getUniqueId();
        if (hasInjury(player, injury)) {
            InjuredPlayer currentInjuries = injuredPlayers.get(pUuid);
            currentInjuries.removeInjury(injury);
            injuredPlayers.replace(pUuid, currentInjuries);
            cancelInjury(pUuid, injury);
        }
    }

    /** Cancels a scheduled injury. */
    private void cancelInjury(UUID playerId, Injury injury) {
        if (injurySchedules.containsKey(playerId)) {
            if (injurySchedules.get(playerId).containsKey(injury.injuryType)) {
                InjuryCancelEvent cancelEvent = new InjuryCancelEvent(Bukkit.getPlayer(playerId), injury);
                injuredPlugin.getServer().getPluginManager().callEvent(cancelEvent);
                if (cancelEvent.isCancelled()) {
                    injuredPlugin.getLogger().warning(
                        "Could not remove " + injury.injuryType.name()
                      + " from " + Bukkit.getPlayer(playerId).getName() + "!"
                    );
                } else {
                    injuredPlugin.getLogger().info(
                        "Removed " + injury.injuryType.name()
                        + " from " + Bukkit.getPlayer(playerId).getName()
                    );
                }
            }
        }
    }

    /** Clears every injury from a player and cancels their schedules. */
    public void clearInjuries(Player player) {
        UUID pUuid = player.getUniqueId();
        if (trackingPlayer(player)) {
            InjuredPlayer currentInjuries = injuredPlayers.get(pUuid);
            currentInjuries.clearInjuries();
            injuredPlayers.replace(pUuid, currentInjuries);
        }
        if (injurySchedules.containsKey(pUuid)) {
            for (InjuryType i : injurySchedules.get(pUuid).keySet()) {
                Injury toCancel = injuries.get(i);
                InjuryCancelEvent cancelEvent = new InjuryCancelEvent(player, toCancel);
                injuredPlugin.getServer().getPluginManager().callEvent(cancelEvent);
                if (cancelEvent.isCancelled()) {
                    injuredPlugin.getLogger().warning(
                        "Could not remove " + toCancel.injuryType.name()
                      + " from " + player.getName() + "!"
                    );
                } else {
                    injuredPlugin.getLogger().info(
                        "Removed " + toCancel.injuryType.name()
                        + " from " + player.getName()
                    );
                }
            }
        }
    }
} 
