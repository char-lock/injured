package io.github.charlock.injured;


import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The entry point for PaperMC to initialise the Injured plugin.
 * 
 * @see JavaPlugin
 * 
 */
public class Injured extends JavaPlugin {
    private FileConfiguration config;

    public Injured() {
        config = getConfig();
    }


    @Override
    public void onEnable() {
        config.addDefault("bleedChance", 0.15);
        config.addDefault("bleedInterval", 40);
        config.addDefault("bleedDamage", 0.05);
        config.options().copyDefaults(true);
        saveConfig();
        // Update any Captain singletons with the plugin instance.
        InjuryCaptain.updatePlugin(this);
        InjuryCaptain.getCaptain().updateInjuries();
        // Regiser all event listeners.
        getServer().getPluginManager().registerEvents(new InjuryDamageListener(), this);
        getServer().getPluginManager().registerEvents(new InjuryDeathListener(), this);
        getServer().getPluginManager().registerEvents(new InjuryHealListener(), this);
        // Define command executors.
        // getCommand("injure").setExecutor(new CommandHandler(this));
        // getCommand("uninjure").setExecutor(new CommandHandler(this));
        // And we're done here!
        getLogger().info("Welcome to the pain train. Injuries activated.");
    }

    @Override
    public void onDisable() {
        getLogger().warning(
            "Injuries are no longer in effect. Plugin disabled."
        );
    }
}