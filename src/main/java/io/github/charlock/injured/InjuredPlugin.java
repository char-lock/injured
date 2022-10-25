package io.github.charlock.injured;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * The entry point for PaperMC to initialise the Injured plugin.
 * 
 * @see JavaPlugin
 */
public class InjuredPlugin extends JavaPlugin {
    private final FileConfiguration userConfig;

    /** 
     * Constructor for the Injured plugin to ensure that it gets the 
     * user configuration as soon as it is initialised.
     * 
     */
    public InjuredPlugin() {
        userConfig = this.getConfig();
    }

    /**
     * Checks the configured debug setting and prints a message to
     * the console if enabled.
     * 
     * 
     * @param message       what to print to the console if in debug.
     * 
     */
    public void debugInfo(String message) {
        if (userConfig.getBoolean("development.debug")) {
            this.getLogger().info(message);
        }
    }

    // Overrides for JavaPlugin methods
    @Override
    public void onEnable() {
        // Start of User Configuration
        // Development
        userConfig.createSection("development");
        userConfig.addDefault("development.debug", true);
        // Injuries
        userConfig.createSection("injuries");
        // Injuries -> Bleed
        userConfig.createSection("injuries.bleed");
        userConfig.addDefault("injuries.bleed.chancePercent", 0.15);
        userConfig.addDefault("injuries.bleed.damageInterval", 40);
        userConfig.addDefault("injuries.bleed.damagePercent", 0.05);
        userConfig.addDefault("injuries.bleed.tickDuration", -1);
        userConfig.addDefault("injuries.bleed.soundOnInjure", true);
        userConfig.addDefault("injuries.bleed.soundOnDamage", true);
        userConfig.addDefault("injuries.bleed.onInjuryMessage", "You are now bleeding -- patch yourself up quick or you will die.");
        userConfig.addDefault("injuries.bleed.onRemedyMessage", "You are no longer bleeding.");
        // Injuries -> Crippled
        userConfig.createSection("injuries.crippled");
        userConfig.addDefault("injuries.crippled.chancePercent", 0.50);
        userConfig.addDefault("injuries.crippled.slowPercent", 0.33);
        userConfig.addDefault("injuries.crippled.tickDuration", -1);
        userConfig.addDefault("injuries.crippled.soundOnInjure", true);
        userConfig.addDefault("injuries.crippled.onInjuryMessage", "You are now crippled -- get a splint or your movement will be slowed.");
        userConfig.addDefault("injuries.crippled.onRemedyMessage", "You are no longer crippled.");
        // Remedies
        userConfig.createSection("remedies");
        // Remedies -> Bandage
        userConfig.createSection("remedies.bandage");
        userConfig.addDefault("remedies.bandage.label", "Bandage");
        // Remedies -> Splint
        userConfig.createSection("remedies.splint");
        userConfig.addDefault("remedies.splint.label", "Splint");
        // End of User Configuration
        userConfig.options().copyDefaults(true);
        saveConfig();
        // Update any Captain singletons with the plugin instance.
        InjuryCaptain.updatePlugin(this);
        InjuryCaptain.getCaptain().updateInjuries();
        // And we're done here!
        this.getLogger().info("Welcome to the pain train -- Injuries activated.");
    }

    @Override
    public void onDisable() {
        this.getLogger().warning("Injuries are no longer in effect. Plugin disabled.");
    }   
}
