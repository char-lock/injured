package io.github.charlock.splinter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;

import org.bukkit.plugin.java.JavaPlugin;


public class SplinterPlugin extends JavaPlugin {
    private final FileConfiguration userConfig;

    public SplinterPlugin() {
        this.userConfig = this.getConfig();
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

    @Override
    public void onEnable() {
        // Save default configuration
        this.saveDefaultConfig();
        this.getLogger().info("Sneak like you're playing Splinter Cell -- stealth activated.");
    }

    @Override
    public void onDisable() {
        this.getLogger().warning("Stealth is no longer in effect. Plugin disabled.");
    }
}
