package io.github.charlock.injured;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.charlock.injured.event.InjuryCancelEvent;
import io.github.charlock.injured.event.listener.InjuryDamageListener;
import io.github.charlock.injured.event.listener.InjuryDeathListener;
import io.github.charlock.injured.event.listener.InjuryHealListener;
import io.github.charlock.injured.injury.Injury;
import io.github.charlock.injured.injury.Bleed;
import io.github.charlock.injured.injury.Crippled;
import io.github.charlock.injured.injury.InjuryType;


/**
 * A singleton controller for all injuries on the server.
 * 
 */
public class InjuryCaptain {
    private static InjuryCaptain injuryCaptain = null;
    private static InjuredPlugin injuredPlugin;
    private Map<UUID, InjuredPlayer> injuredPlayers;
    private Map<InjuryType, Injury> injuries;

   
    private InjuryCaptain() {
        injuredPlayers = new HashMap<UUID, InjuredPlayer>();
        injuries = new HashMap<InjuryType, Injury>();
    }

    /**
     * Updates the injury list with the corresponding class by type.
     * This is a separate process because it the InjuryCaptain needs
     * to know the plugin instance first.
     * 
     */
    public void updateInjuries() {
        injuries.put(InjuryType.BLEEDING, new Bleed());
        injuries.put(InjuryType.CRIPPLED, new Crippled());
    }

    /**
     * Returns the instance of the InjuryCaptain or creates it if it
     * doesn't already exist.
     * 
     */
    public static InjuryCaptain getCaptain() {
        if (injuryCaptain == null) {
            injuryCaptain = new InjuryCaptain();
        }
        return injuryCaptain;
    }

    /**
     * Returns the mapping of injury types to the injury classes.
     * 
     */
    public Map<InjuryType, Injury> getInjuryMapping() {
        return this.injuries;
    }

    /**
     * Updates the InjuryCaptain with the plugin instance.
     * 
     */
    public static void updatePlugin(InjuredPlugin plugin) {
        getCaptain();
        injuredPlugin = plugin;
        injuredPlugin.debugInfo("(InjuryCaptain) Got plugin instance. Updating event handlers ...");
        injuredPlugin.getServer().getPluginManager().registerEvents(new InjuryDamageListener(), injuredPlugin);
        injuredPlugin.getServer().getPluginManager().registerEvents(new InjuryDeathListener(), injuredPlugin);
        injuredPlugin.getServer().getPluginManager().registerEvents(new InjuryHealListener(), injuredPlugin);
    }

    /**
     * Returns the plugin instance.
     * 
     */
    public InjuredPlugin getPlugin() {
        return injuredPlugin;
    }

    /**
     * Returns whether or not a player is being tracked for injuries.
     * 
     * 
     * @param playerId      id of player to check
     * 
     */
    public boolean trackingPlayer(UUID playerId) {
        return injuredPlayers.containsKey(playerId);
    }

    /**
     * Returns whether or not a player is being tracked for injuries.
     * 
     * 
     * @param player        player to check
     * 
     */
    public boolean trackingPlayer(Player player) {
        return this.trackingPlayer(player.getUniqueId());
    }

    /**
     * Returns whether or not a player has an injury.
     * 
     * 
     * @param playerId      id of player to check for injury
     * 
     * @param injType       type of injury for which to check
     * 
     */
    public boolean hasInjury(UUID playerId, InjuryType injType) {
        if (this.trackingPlayer(playerId)) {
            return injuredPlayers.get(playerId).hasInjury(injType);
        }
        return false;
    }

    /**
     * Returns whether or not a player has an injury.
     * 
     * 
     * @param player        player to check for injury
     * 
     * @param injType       type of injury for which to check
     * 
     */
    public boolean hasInjury(Player player, InjuryType injType) {
        return this.hasInjury(player.getUniqueId(), injType);
    }

    /**
     * Returns whether or not a player has an injury.
     * 
     * 
     * @param playerId      id of player to check for injury
     * 
     * @param injury        injury for which to check
     * 
     */
    public boolean hasInjury(UUID playerId, Injury injury) {
        return this.hasInjury(playerId, injury.getType());
    }

    /**
     * Returns whether or not a player has an injury.
     * 
     * 
     * @param playerId      player to check for injury
     * 
     * @param injType       injury for which to check
     * 
     */
    public boolean hasInjury(Player player, Injury injury) {
        return this.hasInjury(player.getUniqueId(), injury.getType());
    }

    /**
     * Adds an injury to a player.
     * 
     * 
     * @param playerId      id of player to injure
     * 
     * @param injury        injury to apply to player
     * 
     */
    public void addInjury(UUID playerId, Injury injury) {
        this.getPlugin().debugInfo("(InjuryCaptain) Adding [" + injury.getName() + "] to " + Bukkit.getPlayer(playerId).getName() + " ...");
        if (!trackingPlayer(playerId)) {
            injuredPlayers.put(playerId, new InjuredPlayer(playerId));
        }
        InjuredPlayer currentInjuries = injuredPlayers.get(playerId);
        currentInjuries.addInjury(injury);
        injuredPlayers.replace(playerId, currentInjuries);
        injury.onEffect(playerId, true);
        if (injury.isScheduled()) {
            this.scheduleInjury(playerId, injury);
        }
    }

    /**
     * Adds an injury to a player.
     * 
     * 
     * @param player        player to injure
     * 
     * @param injury        injury to apply to player
     * 
     */
    public void addInjury(Player player, Injury injury) {
        this.addInjury(player.getUniqueId(), injury);
    }

    /**
     * Schedules an injury to reoccur at a set interval.
     * 
     * 
     * @param playerId      id of player to schedule injury
     * 
     * @param injury        injury with which to injure player
     * 
     */
    private void scheduleInjury(UUID playerId, Injury injury) {
        this.cancelInjury(playerId, injury.getType());
        if (injury.hasInterval()) {
            new InjuryTask(
                playerId,
                injury
            ).runTaskTimer(
                this.getPlugin(),
                injury.getInterval(),
                injury.getInterval()
            );
        }
    }

    /**
     * Removes an injury from a player.
     * 
     * 
     * @param playerId      id of player to uninjure
     * 
     * @param injType       type of injury to remove
     * 
     */
    public void removeInjury(UUID playerId, InjuryType injType) {
        if (this.hasInjury(playerId, injType)) {
            InjuredPlayer currentInjuries = injuredPlayers.get(playerId);
            currentInjuries.removeInjury(injType);
            injuredPlayers.replace(playerId, currentInjuries);
            cancelInjury(playerId, injType);
        }
    }

    /**
     * Removes an injury from a player.
     * 
     * 
     * @param player        player to uninjure
     * 
     * @param injType       type of injury to remove
     * 
     */
    public void removeInjury(Player player, InjuryType injType) {
        this.removeInjury(player.getUniqueId(), injType);
    }
    
    /**
     * Removes an injury from a player.
     * 
     * 
     * @param playerId      id of player to uninjure
     * 
     * @param injury        injury to remove
     * 
     */
    public void removeInjury(UUID playerId, Injury injury   ) {
        this.removeInjury(playerId, injury.getType());
    }

    /**
     * Removes an injury from a player.
     * 
     * 
     * @param player        player to uninjure
     * 
     * @param injury        injury to remove
     * 
     */
    public void removeInjury(Player player, Injury injury) {
        this.removeInjury(player.getUniqueId(), injury.getType());
    }

    /**
     * Sends an event to cancel a scheduled injury for a player.
     * 
     * 
     * @param playerId      id of player to cancel injury
     * 
     * @param injType       type of injury to cancel
     * 
     */
    private void cancelInjury(UUID playerId, InjuryType injType) {
        InjuryCancelEvent cancel = new InjuryCancelEvent(playerId, injType);
        this.getPlugin().getServer().getPluginManager().callEvent(cancel);
        if (cancel.isCancelled()) {
            this.getPlugin().debugInfo(
                "(InjuryCaptain) Could not unschedule ["
                + injType.name().toLowerCase() + "] for "
                + Bukkit.getPlayer(playerId).getName() + "."
            );
        } else {
            this.getPlugin().debugInfo(
                "(InjuryCaptain) Unscheduled ["
                + injType.name().toLowerCase() + "] for "
                + Bukkit.getPlayer(playerId).getName() + ".");
        }
    }

    /**
     * Clear and unschedule injuries for a player.
     * 
     * 
     * @param playerId      id of player to clear injuries
     * 
     */
    public void clearInjuries(UUID playerId) {
        if (this.trackingPlayer(playerId)) {
            InjuredPlayer currentInjuries = injuredPlayers.get(playerId);
            for (Injury i : currentInjuries.getInjuries()) {
                this.cancelInjury(playerId, i.getType());
            }
            currentInjuries.clearInjuries();
            injuredPlayers.replace(playerId, currentInjuries);
        }
    }

    /**
     * Clear and unschedule injuries for a player.
     * 
     * 
     * @param player        player to clear injuries
     * 
     */
    public void clearInjuries(Player player) {
        this.clearInjuries(player.getUniqueId());
    }
}