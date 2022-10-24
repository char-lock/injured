package io.github.charlock.injured;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;


public class InjuryHealListener implements Listener {
    private InjuryCaptain injuryCaptain = InjuryCaptain.getCaptain();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();
        if (heldItem != null) {
            if (heldItem.getItemMeta().hasDisplayName()) {
                String itemLabel = heldItem.getItemMeta().displayName().toString();
                if (heldItem.getType() == Material.PAPER && itemLabel.contains("andage")) {
                    InjuryBleed bleeding = new InjuryBleed(injuryCaptain.getPlugin());
                    if (injuryCaptain.hasInjury(player, bleeding)) {
                        injuryCaptain.removeInjury(player, bleeding);
                        player.sendMessage(ChatColor.YELLOW + "You are no longer bleeding.");
                        heldItem.setAmount(heldItem.getAmount() - 1);
                    }
                }
            
            }
        }
    }
}
