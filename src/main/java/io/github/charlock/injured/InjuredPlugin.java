package io.github.charlock.injured;

import org.bukkit.command.CommandExecutor;
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
        // Save default configuration.
        this.saveDefaultConfig();
        // Update any Captain singletons with the plugin instance.
        InjuryCaptain.updatePlugin(this);
        InjuryCaptain.getCaptain().updateInjuries();
        // Register commands with the handler.
        CommandExecutor executor = new InjuredCommandHandler();
        this.getCommand("injure").setExecutor(executor);
        this.getCommand("uninjure").setExecutor(executor);
        this.getCommand("resetspeed").setExecutor(executor);
        // And we're done here!
        this.getLogger().info("Welcome to the pain train -- Injuries activated.");
    }

    @Override
    public void onDisable() {
        this.getLogger().warning("Injuries are no longer in effect. Plugin disabled.");
    }   
}
