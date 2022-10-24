package io.github.charlock.injured;

// import java.util.ArrayList;
// import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class CommandHandler implements CommandExecutor {
    Injured injured;

    public CommandHandler(Injured plugin) {
        super();
        injured = plugin;
    }

    // @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player commander = (Player) sender;
            // COMMAND HANDLER: /injure <player> <injury>
            if (command.getName().equalsIgnoreCase("injure")) {
                if (!commander.isOp()) {
                    sender.sendMessage("This command is only usable by opped users.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("Incorrect usage.");
                    sender.sendMessage("/injure [player] [injury]");
                    return true;
                }
                String argPlayer = args[0].toLowerCase();
                String argInjury = args[1].toLowerCase();

                Player targetPlayer = null;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String currentPlayer = player.getName();
                    if (currentPlayer.equalsIgnoreCase(argPlayer)) {
                        targetPlayer = player;
                    }
                }

                if (targetPlayer == null) {
                    sender.sendMessage("Unknown player.");
                    return true;
                }

                if (!injured.injuries.contains(argInjury)) {
                    sender.sendMessage("Invalid injury.");
                    return true;
                }

                // Handle adding bleeding.
                if (argInjury.equalsIgnoreCase("bleeding")) {
                    injured.addInjury(targetPlayer, InjuredPlayer.InjuredPlayerFlag.BLEEDING);
                    targetPlayer.addPotionEffect(injured.getInjuryEffect("bleed"));
                    sender.sendMessage("Successfully added bleeding to " + targetPlayer.getName());
                    return true;
                }
            } else if (command.getName().equalsIgnoreCase("uninjure")) {
                if (!commander.isOp()) {
                    sender.sendMessage("This command is only usable by opped users.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("Incorrect usage.");
                    sender.sendMessage("/uninjure [player] [injury]");
                    return true;
                }
                String argPlayer = args[0].toLowerCase();
                String argInjury = args[1].toLowerCase();

                Player targetPlayer = null;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    String currentPlayer = player.getName();
                    if (currentPlayer.equalsIgnoreCase(argPlayer)) {
                        targetPlayer = player;
                    }
                }

                if (targetPlayer == null) {
                    sender.sendMessage("Unknown player.");
                    return true;
                }

                if (!injured.injuries.contains(argInjury)) {
                    sender.sendMessage("Invalid injury.");
                    return true;
                }

                // Handle adding bleeding.
                if (argInjury.equalsIgnoreCase("bleeding")) {
                    injured.removeInjury(targetPlayer, InjuredPlayer.InjuredPlayerFlag.BLEEDING);
                    sender.sendMessage("Successfully removed bleeding from " + targetPlayer.getName());
                    targetPlayer.removePotionEffect(PotionEffectType.POISON);
                    return true;
                }
            }
        }
        return false;
    }
}
