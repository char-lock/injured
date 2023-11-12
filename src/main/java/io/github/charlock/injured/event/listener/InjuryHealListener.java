package io.github.charlock.injured.event.listener;

import org.bukkit.Material;
import org.bukkit.entity.Player;
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
        Player targetPlayer = e.getPlayer();
        ItemStack heldItem = targetPlayer.getInventory().getItemInMainHand();
        // Nothing needs to occur if the player is not holding an appropriately
        // defined item.
        if (heldItem == null || heldItem.getItemMeta() == null) return;
        String itemLabel = heldItem.getItemMeta().displayName().toString().toLowerCase();
        // Handling cure for the `Bleeding` injury.
        String bandageLabel = this.injuredPlugin.getConfig()
            .getString("remedies.bandage.label").toLowerCase();
        boolean isHoldingBandage = heldItem.getType() == Material.PAPER
            && itemLabel.contains(bandageLabel);
        boolean isBleeding = this.injuryCaptain.hasInjury(targetPlayer, InjuryType.BLEEDING);
        if (isBleeding && isHoldingBandage) {
            // TODO: Find a less convoluted way to do this.
            this.injuryCaptain.getInjuryMapping().get(InjuryType.BLEEDING)
                .sendRemedyMessage(targetPlayer);
            this.injuryCaptain.removeInjury(targetPlayer, InjuryType.BLEEDING);
            heldItem.setAmount(heldItem.getAmount() - 1);
        }
        // Handling cure for the `Crippled` injury.
        String splintLabel = this.injuredPlugin.getConfig()
            .getString("remedies.splint.label").toLowerCase();
        boolean isHoldingSplint = heldItem.getType() == Material.STICK
            && itemLabel.contains(splintLabel);
        boolean isCrippled = this.injuryCaptain.hasInjury(targetPlayer, InjuryType.CRIPPLED);
        if (isCrippled && isHoldingSplint) {
            // TODO: Find a less convoluted way to do this.
            this.injuryCaptain.getInjuryMapping().get(InjuryType.CRIPPLED)
                .sendRemedyMessage(targetPlayer);
            float slowPercent = (float)this.injuredPlugin.getConfig()
                .getDouble("injuries.crippled.slowPercent");
            float originalSpeed = targetPlayer.getWalkSpeed() / slowPercent;
            targetPlayer.setWalkSpeed(originalSpeed);
            this.injuryCaptain.removeInjury(targetPlayer, InjuryType.CRIPPLED);
            heldItem.setAmount(heldItem.getAmount() - 1);
        }
    }

}
