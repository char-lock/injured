package io.github.charlock.injured.event;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.charlock.injured.injury.InjuryType;


/**
 * An event broadcasted when a potentially scheduled injury needs to
 * be cancelled.
 * 
 */
public class InjuryCancelEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();

    private UUID injured;
    private InjuryType injuryType;
    private boolean eventCancelled;

    /**
     * Constructor for an InjuryCancelEvent.
     * 
     * 
     * @param playerId      id of player for which to call event
     * 
     * @param injType       type of injury to unschedule
     * 
     */
    public InjuryCancelEvent(UUID playerId, InjuryType injType) {
        this.injured = playerId;
        this.injuryType = injType;
        this.eventCancelled = false;
    }

    /**
     * Constructor for an InjuryCancelEvent.
     * 
     * 
     * @param player        player for which to call event
     * 
     * @param injType       type of injury to unschedule
     * 
     */
    public InjuryCancelEvent(Player player, InjuryType injType) {
        this(player.getUniqueId(), injType);
    }

    /**
     * Returns a Player object of the injured party.
     * 
     */
    public Player getInjured() {
        return Bukkit.getPlayer(this.injured);
    }

    /**
     * Returns the UUID for the injured party.
     * 
     */
    public UUID getInjuredId() {
        return this.injured;
    }

    /**
     * Returns the InjuryType that needs to be cancelled.
     * 
     */
    public InjuryType getInjuryType() {
        return this.injuryType;
    }

    // Event and Cancellable overrides follow.
    @Override
    public boolean isCancelled() {
        return this.eventCancelled;
    }

    @Override
    public void setCancelled(boolean value) {
        this.eventCancelled = value;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}