package io.github.charlock.injured;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.charlock.injured.event.InjuryCancelEvent;
import io.github.charlock.injured.event.listener.InjuryDamageListener;
import io.github.charlock.injured.event.listener.InjuryDeathListener;
import io.github.charlock.injured.event.listener.InjuryHealListener;
import io.github.charlock.injured.event.listener.InjuryJoinListener;
import io.github.charlock.injured.event.listener.InjuryMoveListener;
import io.github.charlock.injured.event.listener.InjuryQuitListener;
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

   
    /** 
     * Constructor for InjuryCaptain
     * 
     */
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
        injuredPlugin.getServer().getPluginManager().registerEvents(new InjuryQuitListener(), injuredPlugin);
        injuredPlugin.getServer().getPluginManager().registerEvents(new InjuryJoinListener(), injuredPlugin);
        injuredPlugin.getServer().getPluginManager().registerEvents(new InjuryMoveListener(), injuredPlugin);
    }

    /**
     * Returns the plugin instance.
     * 
     */
    public InjuredPlugin getPlugin() {
        return injuredPlugin;
    }

    /**
     * Adds a player to the tracked list.
     * 
     * 
     * @param playerId      id of player to add
     * 
     */
    public void trackPlayer(UUID playerId) {
        if (!this.trackingPlayer(playerId)) {
            this.injuredPlayers.put(playerId, new InjuredPlayer(playerId));
        }
    }

    /**
     * Adds a player to the tracked list.
     * 
     * 
     * @param player        player to add
     * 
     */
    public void trackPlayer(Player player) {
        this.trackPlayer(player.getUniqueId());
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
     * @param player        player to check for injury
     * 
     * @param inj           injury for which to check
     * 
     */
    public boolean hasInjury(Player player, Injury injury) {
        return this.hasInjury(player.getUniqueId(), injury.getType());
    }

    /**
     * Returns the target walk speed for an injured player.
     * 
     * 
     * @param playerId      id of player to check
     * 
     */
    public float getInjuredSpeed(UUID playerId) {
        if (this.trackingPlayer(playerId)) {
            return this.injuredPlayers.get(playerId).getInjuredSpeed();
        } else {
            return Bukkit.getPlayer(playerId).getWalkSpeed();
        }
    }

    /**
     * Returns the target walk speed for an injured player.
     * 
     * 
     * @param player        player to check
     * 
     */
    public float getInjuredSpeed(Player player) {
        return this.getInjuredSpeed(player.getUniqueId());
    }

    /**
     * Sets the target walk speed for an injured player.
     * 
     * 
     * @param playerId      id of player to change speed
     * 
     * @param walkSpeed     new target walkSpeed
     * 
     */
    public void setInjuredSpeed(UUID playerId, float walkSpeed) {
        if (this.trackingPlayer(playerId)) {
            InjuredPlayer currentPlayer = this.injuredPlayers.get(playerId);
            currentPlayer.setInjuredSpeed(walkSpeed);
            this.injuredPlayers.replace(playerId, currentPlayer);
        }
    }

    /**
     * Sets the target walk speed for an injured player.
     * 
     * 
     * @param player        player to change speed
     * 
     * @param walkSpeed     new target walkSpeed
     * 
     */
    public void setInjuredSpeed(Player player, float walkSpeed) {
        this.setInjuredSpeed(player.getUniqueId(), walkSpeed);
    }

    /**
     * Sets the baseline walk speed for an injured player.
     * 
     * 
     * @param playerId      id of player to change speed
     * 
     * @param walkSpeed     new baseline speed
     * 
     */
    public void setOriginalSpeed(UUID playerId, float walkSpeed) {
        if (this.trackingPlayer(playerId)) {
            InjuredPlayer currentPlayer = this.injuredPlayers.get(playerId);
            currentPlayer.setOriginalSpeed(walkSpeed);
            this.injuredPlayers.replace(playerId, currentPlayer);
        }
    }

    /**
     * Sets the baseline walk speed for an injured player.
     * 
     * 
     * @param player        player to change speed
     * 
     * @param walkSpeed     new baseline speed
     * 
     */
    public void setOriginalSpeed(Player player, float walkSpeed) {
        this.setOriginalSpeed(player.getUniqueId(), walkSpeed);
    }

    /**
     * Updates the baseline and injured speed of a player.
     * 
     * 
     * @param playerId      id of player to update
     * 
     * @param walkSpeed     new baseline speed
     * 
     */
    public void updateWalkSpeed(UUID playerId, float walkSpeed) {
        double multiplier = ((Crippled)this.injuries.get(InjuryType.CRIPPLED)).getSlowPercent();
        float target = this.hasInjury(playerId, InjuryType.CRIPPLED) ? walkSpeed * (float)multiplier : walkSpeed;
        this.setInjuredSpeed(playerId, target);
        this.setOriginalSpeed(playerId, walkSpeed);
        Bukkit.getPlayer(playerId).setWalkSpeed(walkSpeed * (float)multiplier);
    }

    /**
     * Updates the baseline and injured speed of a player.
     * 
     * 
     * @param player        player to update
     * 
     * @param walkSpeed     new baseline speed
     * 
     */
    public void updateWalkSpeed(Player player, float walkSpeed) {
        this.updateWalkSpeed(player.getUniqueId(), walkSpeed);
    }

    /**
     * Returns a Collection containing all of the injuries currently
     * assigned to a player.
     * 
     * 
     * @param playerId      id of player for which to get injuries
     * 
     */
    public Collection<Injury> getInjuries(UUID playerId) {
        if (this.trackingPlayer(playerId)) {
            return this.injuredPlayers.get(playerId).getInjuries();
        } else {
            return new HashSet<Injury>();
        }
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
        if (!this.hasInjury(playerId, injury)) {
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
            if (injury.getDuration() > 0) {
                new InjuryTimerTask(playerId, injury.getType()).runTaskLater(this.getPlugin(), injury.getDuration());
            }
        } else {
            this.getPlugin().debugInfo("(InjuryCaptain) " + Bukkit.getPlayer(playerId) + " already has [" + injury.getName() + "].");
        }
    }

    /**
     * Returns the original speed of a player before injury.
     * 
     * 
     * @param playerId      id of player for which to check
     * 
     */
    public float getOriginalSpeed(UUID playerId) {
        if (this.trackingPlayer(playerId)) {
            return this.injuredPlayers.get(playerId).getOriginalSpeed();
        } else {
            return Bukkit.getPlayer(playerId).getWalkSpeed();
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
    public void scheduleInjury(UUID playerId, Injury injury) {
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
    public void cancelInjury(UUID playerId, InjuryType injType) {
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