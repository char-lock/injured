package io.github.charlock.injured;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.github.charlock.injured.injury.Injury;
import io.github.charlock.injured.injury.InjuryType;


/**
 * An object representing a player with an injury. This is the main way
 * that the plugin tracks injuries.
 * 
 */
public class InjuredPlayer {
    // Store only the UUID to prevent a memory leak.
    private final UUID uuid;
    private Map<InjuryType, Injury> injuries;
    private float originalSpeed;
    private float currentSpeed;

    /**
     * Constructor for an injured player from a player's UUID.
     * 
     * 
     * @param playerId     the injured player's UUID
     * 
     */
    public InjuredPlayer(UUID playerId) {
        injuries = new HashMap<InjuryType, Injury>();
        this.uuid = playerId;
        this.originalSpeed = Bukkit.getPlayer(playerId).getWalkSpeed();
        this.currentSpeed = this.originalSpeed;
    }

    /**
     * Constructor for an injured player from a standard Player object.
     * 
     * 
     * @param player       the injured player
     * 
     */
    public InjuredPlayer(Player player) {
        this(player.getUniqueId());
    }

    /**
     * Returns a Player object for this InjuredPlayer. 
     * 
     */
    public Player getPlayer() {
        return Bukkit.getPlayer(this.uuid);
    }

    /**
     * Returns a player's original speed at the time of first instance.
     * 
     */
    public float getOriginalSpeed() {
        return this.originalSpeed;
    }

    /**
     * Sets a player's original walk speed.
     * 
     * 
     * @param walkSpeed     speed of player
     * 
     */
    public void setOriginalSpeed(float walkSpeed) {
        this.originalSpeed = walkSpeed;
    }

    /**
     * Gets a player's target speed according to injury.
     * 
     */
    public float getInjuredSpeed() {
        return this.currentSpeed;
    }

    /**
     * Sets a player's target speed according to injury.
     * 
     * 
     * @param walkSpeed     target speed for player
     * 
     */
    public void setInjuredSpeed(float walkSpeed) {
        this.currentSpeed = walkSpeed;
    }

    /**
     * Returns the UUID for the InjuredPlayer.
     * 
     */
    public UUID getUuid() {
        return this.uuid;
    }

    /**
     * Add an injury to this Injured player. Please note that a player
     * can only have one injury of each type defined in InjuryType.
     * 
     * 
     * @param injury       the injury to add to this player
     * 
     * @param overwrite    whether or not to overwrite existing
     *                     injuries of the same type
     * 
     */
    public void addInjury(Injury injury, boolean overwrite) {
        InjuryType injType = injury.getType();
        if (injuries.containsKey(injType)) {
            if (overwrite) {
                // Using replace here just as a reminder that this
                // is overwriting the injury.
                injuries.replace(injType, injury);
            }
        } else {
            injuries.put(injType, injury);
        }
    }

    /**
     * Add an injury to this injured player. Please note that a player
     * can only have one injury of each type defined in InjuryType.
     * This method will not overwrite exisiting injuries.
     * 
     * 
     * @param injury       the injury to add to this player
     * 
     */
    public void addInjury(Injury injury) {
        this.addInjury(injury, false);
    }

    /**
     * Removes an injury from this injured player. There is no need to
     * check whether or not the player has the Injury or InjuryType in
     * question.
     * 
     * 
     * @param injType       the type of injury to remove
     *
     */
    public void removeInjury(InjuryType injType) {
        injuries.remove(injType);
    }

    /**
     * Removes an injury from this injured player. There is no need to
     * check whether or not the player has the Injury or InjuryType in
     * question.
     * 
     * 
     * @param injury       an injury with the same type as the one
     *                     to be removed
     *
     */
    public void removeInjury(Injury injury) {
        this.removeInjury(injury.getType());
    }

    /**
     * Removes all injuries from this injured player.
     * 
     */
    public void clearInjuries() {
        injuries.clear();
    }

    /**
     * Returns whether or not this player has an injury.
     * 
     * 
     * @param injType       the type of injury to check
     * 
     */
    public boolean hasInjury(InjuryType injType) {
        return injuries.containsKey(injType);
    }

    /**
     * Returns whether or not this player has an injury.
     * 
     * 
     * @param injType       an injury with the same type as the one
     *                      for which to check
     * 
     */
    public boolean hasInjury(Injury injury) {
        return this.hasInjury(injury.getType());
    }

    /**
     * Returns a Collection containing all of the injuries this player has.
     * 
     */
    public Collection<Injury> getInjuries() {
        return this.injuries.values();
    }
 }
