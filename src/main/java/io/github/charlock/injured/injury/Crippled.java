package io.github.charlock.injured.injury;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import io.github.charlock.injured.InjuryCaptain;

/**
 * A type of Injury that slows the player's movement to the configured
 * percentage on particularly bad falls.
 * 
 */
public class Crippled extends Injury {
    private FileConfiguration userConfig;
    private double slowPercent;
    private boolean soundOnInjure;
    private String onInjuryMessage;
    private String onRemedyMessage;
    
    /** Constructor for the Crippled injury.  */
    public Crippled() {
        super(InjuryType.CRIPPLED);
        userConfig = this.getPlugin().getConfig();
        this.setCauses(new HashSet<DamageCause>(Arrays.asList(
            DamageCause.FALL,
            DamageCause.ENTITY_EXPLOSION,
            DamageCause.BLOCK_EXPLOSION
        )));
        this.setDuration(userConfig.getInt("injuries.crippled.tickDuration"));
        this.setChancePercent(userConfig.getDouble("injuries.crippled.chancePercent"));
        this.slowPercent = userConfig.getDouble("injuries.crippled.slowPercent");
        this.soundOnInjure = userConfig.getBoolean("injuries.crippled.soundOnInjure");
        this.onInjuryMessage = userConfig.getString("injuries.crippled.onInjuryMessage");
        this.onRemedyMessage = userConfig.getString("injuries.crippled.onRemedyMessage");
    }

    @Override
    public boolean hasInterval() {
        return false;
    }

    @Override
    public int getInterval() {
        return 0;
    }

    @Override
    public void sendInjuryMessage(Player player) {
        player.sendMessage(ChatColor.RED + this.onInjuryMessage);
    }

    @Override
    public void sendRemedyMessage(Player player) {
        player.sendMessage(ChatColor.YELLOW + this.onRemedyMessage);
    }

    @Override
    public void onEffect(UUID playerId, boolean first) {
        float playerSpeed = Bukkit.getPlayer(playerId).getWalkSpeed();
        float crippledSpeed = playerSpeed * (float)this.slowPercent;
        InjuryCaptain.getCaptain().setInjuredSpeed(playerId, crippledSpeed);
        Bukkit.getPlayer(playerId).setWalkSpeed(crippledSpeed);
        if (first && this.soundOnInjure) {
            Bukkit.getPlayer(playerId).playSound(Bukkit.getPlayer(playerId).getEyeLocation(), Sound.BLOCK_HONEY_BLOCK_BREAK, SoundCategory.AMBIENT, 1.0f, 1.0f);
        }
    }

    @Override
    public void onEffect(UUID playerId) {
        this.onEffect(playerId, false);
    }

    @Override
    public void onEffect(Player player) {
        this.onEffect(player.getUniqueId(), false);
    }

    @Override
    public boolean isScheduled() {
        return false;
    }
}
