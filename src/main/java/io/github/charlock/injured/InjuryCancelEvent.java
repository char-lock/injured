package io.github.charlock.injured;


import org.bukkit.event.Event;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;


public class InjuryCancelEvent extends Event implements Cancellable {
    private static HandlerList handlers = new HandlerList();
    private Player injured;
    private Injury injury;
    private boolean cancelled;

    public InjuryCancelEvent(Player player, Injury injury) {
        this.injured = player;
        this.injury = injury;
    }

    public Player getInjured() {
        return this.injured;
    }

    public Injury getInjury() {
        return this.injury;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean c) {
        this.cancelled = c;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}
