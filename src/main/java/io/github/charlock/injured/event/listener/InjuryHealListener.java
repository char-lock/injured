package io.github.charlock.injured.event.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import io.github.charlock.injured.InjuredPlugin;
import io.github.charlock.injured.InjuryCaptain;
import io.github.charlock.injured.injury.InjuryType;


/**
 * Listener for a PlayerInteractEvent in order to check for a remedy.
 * 
 * @see Listener
 * 
 */
public class InjuryHealListener implements Listener {
    private InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();
    private InjuredPlugin injuredPlugin = injuryCaptain.getPlugin();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        ItemStack heldItem = e.getPlayer().getInventory().getItemInMainHand();
        if (heldItem != null && heldItem.getItemMeta() != null) {
            if (heldItem.getItemMeta().hasDisplayName()) {
                String itemLabel = heldItem.getItemMeta().displayName().toString().toLowerCase();
                // Remedy Labels
                String bandageLabel = this.injuredPlugin.getConfig().getString("remedies.bandage.label").toLowerCase();
                String splintLabel = this.injuredPlugin.getConfig().getString("remedies.splint.label").toLowerCase();
                if (heldItem.getType() == Material.PAPER && itemLabel.contains(bandageLabel)) {
                    this.injuredPlugin.debugInfo(
                        "(InjuryHealListener) " + e.getPlayer().getName()
                        + " is holding the proper remedy for [bleeding]."
                        + " Checking if they have the injury ..."
                    );
                    if (this.injuryCaptain.hasInjury(e.getPlayer().getUniqueId(), InjuryType.BLEEDING)) {
                        this.injuredPlugin.debugInfo(
                            "(InjuryHealListener) " + e.getPlayer().getName()
                            + " is bleeding. Applying remedy and removing"
                            + " 1 from their inventory ..."
                        );
                        this.injuryCaptain.getInjuryMapping().get(InjuryType.BLEEDING).sendRemedyMessage(e.getPlayer());
                        this.injuryCaptain.removeInjury(e.getPlayer().getUniqueId(), InjuryType.BLEEDING);
                        heldItem.setAmount(heldItem.getAmount() - 1);
                    }
                } else if (heldItem.getType() == Material.STICK && itemLabel.contains(splintLabel)) {
                    this.injuredPlugin.debugInfo(
                        "(InjuryHealListener) " + e.getPlayer().getName()
                        + " is holding the proper remedy for [crippled]."
                        + " Checking if they have the injury ..." 
                    );
                    if (this.injuryCaptain.hasInjury(e.getPlayer().getUniqueId(), InjuryType.CRIPPLED)) {
                        this.injuredPlugin.debugInfo(
                            "(InjuryHealListener) " + e.getPlayer().getName()
                            + " is crippled. Applying remedy and removing"
                            + " 1 from their inventory ..."
                        );
                        this.injuryCaptain.getInjuryMapping().get(InjuryType.CRIPPLED).sendRemedyMessage(e.getPlayer());
                        e.getPlayer().setWalkSpeed(e.getPlayer().getWalkSpeed() / 0.33f);
                        this.injuryCaptain.removeInjury(e.getPlayer().getUniqueId(), InjuryType.CRIPPLED);
                        heldItem.setAmount(heldItem.getAmount() - 1);
                    }
                }
            }
        }
    }
}
