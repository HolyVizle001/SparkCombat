package net.sparkvanilla.combat.listeners;

import net.sparkvanilla.combat.SparkCombat;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class InteractionListener implements Listener {

    private final SparkCombat plugin;

    public InteractionListener(SparkCombat plugin) { this.plugin = plugin; }

    @EventHandler
    public void onCrystalPlace(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getClickedBlock() == null) return;

        Material block = e.getClickedBlock().getType();
        if ((block == Material.OBSIDIAN || block == Material.BEDROCK) && 
             e.getItem() != null && e.getItem().getType() == Material.END_CRYSTAL) {

            
            if (isProtectedRegion(e.getPlayer())) {
                e.setCancelled(true);
                e.getClickedBlock().getLocation().getWorld().spawnEntity(
                        e.getClickedBlock().getLocation().add(0.5, 1, 0.5), EntityType.END_CRYSTAL);

                if (!e.getPlayer().getGameMode().toString().contains("CREATIVE")) {
                    e.getItem().setAmount(e.getItem().getAmount() - 1);
                }
            }
        }
    }

    @EventHandler
    public void onPearlHit(ProjectileHitEvent e) {
        if (e.getEntityType() == EntityType.ENDER_PEARL && e.getEntity().getShooter() instanceof Player) {
            Player shooter = (Player) e.getEntity().getShooter();
            if (plugin.getCombatManager().isTagged(shooter) && isProtectedRegion(shooter)) {
                e.setCancelled(true);
                shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
                shooter.sendMessage(plugin.getMessage("pearl-blocked"));
            }
        }
    }

    private boolean isProtectedRegion(Player p) {
        
        return false; 
    }
}