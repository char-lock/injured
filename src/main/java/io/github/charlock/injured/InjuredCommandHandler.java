package io.github.charlock.injured;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.charlock.injured.injury.Injury;
import io.github.charlock.injured.injury.InjuryType;


/**
 * Handler for commands related to the Injured plugin.
 * 
 */
public class InjuredCommandHandler implements CommandExecutor {
    private final InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    // private final InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();
    private Set<String> validInjuries;
    
    /**
     * Constructor for InjuredCommandHandler. Adds known injuries to a
     * Set.
     * 
     */
    public InjuredCommandHandler() {
        this.validInjuries = new HashSet<String>();
        for (Injury i : this.injuryCaptain.getInjuryMapping().values()) {
            this.validInjuries.add(i.getName().toLowerCase());
        }
    }

    private boolean checkInjuryValidity(String injury) {
        boolean knownInjury = false;
        for (String i : this.validInjuries) {
            if(injury.toLowerCase().contains(i)) {
                knownInjury = true;
                break;
            }
        }
        return knownInjury;
    }

    private boolean checkPlayerValidity(String playerName) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().toLowerCase().contains(playerName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check argument length for validity.
        if (command.getName().equalsIgnoreCase("uninjure") || command.getName().equalsIgnoreCase("injure")) {
            // Handle invalid usage of commands.
            if (args.length < 1 || args.length > 2) {
                sender.sendMessage(ChatColor.RED + "Incorrect usage");
                sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.AQUA + "/" + command.getName().toLowerCase() + " [target] [injury]");
                return true;
            } else if (args.length == 1) {
                if (!checkInjuryValidity(args[0])) {
                    sender.sendMessage(ChatColor.RED + "Unknown injury");
                    sender.sendMessage(ChatColor.AQUA + "Valid injuries: " + this.validInjuries.toString());
                    return true;
                } else if (!(sender instanceof Player)) {
                    sender.sendMessage(ChatColor.RED + "Sorry! Only a player can self-target");
                    return true;
                }
            } else if (args.length == 2) {
                if (!checkInjuryValidity(args[1])) {
                    sender.sendMessage(ChatColor.RED + "Unknown injury");
                    return true;
                }
                if (!checkPlayerValidity(args[0])) {
                    sender.sendMessage(ChatColor.RED + "Unknown player");
                    return true;
                }
            } else if ((sender instanceof Player) && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by an opped user.");
                return true;
            }
            // Check the argument length in order to tell if this is
            // directed at the sender or another target.
            boolean targetSender = true;
            if (args.length == 2) {
                targetSender = false;
            }
            String argTarget = targetSender ? sender.getName().toLowerCase() : args[0].toLowerCase();
            String argInjury = targetSender ? args[0].toLowerCase() : args[1].toLowerCase();
            // Set target as a Player object.
            UUID targetPlayer = null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(argTarget)) {
                    targetPlayer = p.getUniqueId();
                    break;
                }
            }
            // Safety check for null target.
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Unknown player");
                return true;
            }
            // Handle individual commands
            if (command.getName().equalsIgnoreCase("injure")) {
                for (Injury i : this.injuryCaptain.getInjuryMapping().values()) {
                    if (i.getName().contains(argInjury)) {
                        this.injuryCaptain.addInjury(targetPlayer, i);
                        sender.sendMessage(ChatColor.YELLOW + "Succesfully injured " + Bukkit.getPlayer(targetPlayer).getName() + " with [" + i.getName() + "]");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.YELLOW + "Unable to injure " + Bukkit.getPlayer(targetPlayer).getName() + " with [" + argInjury + "]");
                return true;
            } else if (command.getName().equalsIgnoreCase("uninjure")) {
                for (Injury i : this.injuryCaptain.getInjuryMapping().values()) {
                    if (i.getName().contains(argInjury)) {
                        if (this.injuryCaptain.hasInjury(targetPlayer, i.getType())) {
                            i.sendRemedyMessage(Bukkit.getPlayer(targetPlayer));
                            if (i.getType() == InjuryType.BLEEDING) {
                                this.injuryCaptain.removeInjury(targetPlayer, InjuryType.BLEEDING);
                            } else if (i.getType() == InjuryType.CRIPPLED) {
                                float originalSpeed = this.injuryCaptain.getOriginalSpeed(targetPlayer);
                                this.injuryCaptain.setInjuredSpeed(targetPlayer, originalSpeed);
                                Bukkit.getPlayer(targetPlayer).setWalkSpeed(originalSpeed);
                                this.injuryCaptain.removeInjury(targetPlayer, InjuryType.CRIPPLED);
                            }
                        }
                        sender.sendMessage(ChatColor.YELLOW + "Successfully healed " + Bukkit.getPlayer(targetPlayer).getName() + " of [" + i.getName() + "]");
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.YELLOW + "Unable to heal " + Bukkit.getPlayer(targetPlayer).getName() + " of [" + argInjury + "]");
                return true;
            }
        } else if (command.getName().equalsIgnoreCase("resetspeed")) {
            boolean targetSender = false;
            if (args.length > 1) {
                sender.sendMessage(ChatColor.RED + "Invalid usage");
                sender.sendMessage(ChatColor.WHITE + "Usage: " + ChatColor.AQUA + "/resetspeed [target]");
                return true;
            }
            if ((sender instanceof Player) && !sender.isOp()) {
                sender.sendMessage(ChatColor.RED + "This command can only be used by an opped user.");
                return true;
            }
            if (args.length == 0) {
                targetSender = true;
            } 
            String targetName = targetSender ? sender.getName() : args[0];
            if (!checkPlayerValidity(targetName)) {
                sender.sendMessage(ChatColor.RED + "Unknown player");
                return true;
            }
            UUID targetId = null;
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getName().equalsIgnoreCase(targetName)) {
                    targetId = p.getUniqueId();
                    break;
                }
            }
            this.injuryCaptain.setInjuredSpeed(targetId, 0.2f);
            Bukkit.getPlayer(targetId).setWalkSpeed(0.2f);
            return true;
        }
        return false;
    } 
}
