package net.sparkvanilla.combat.listeners;

import net.sparkvanilla.combat.SparkCombat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CombatListener implements Listener {

    private final SparkCombat plugin;

    public CombatListener(SparkCombat plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player victim = (Player) e.getEntity();
            Player attacker = (Player) e.getDamager();

            
            if (victim == attacker) return;
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) return;

            plugin.getCombatManager().tagPlayer(victim, attacker);
            plugin.getCombatManager().tagPlayer(attacker, victim);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        if (victim.getWorld().getName().equalsIgnoreCase("world")) {
            e.setKeepInventory(false);
            e.getDrops().clear();
            
            for (var item : victim.getInventory().getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    victim.getWorld().dropItemNaturally(victim.getLocation(), item);
                }
            }
            victim.getInventory().clear();
        }
        plugin.getCombatManager().exitCombat(victim.getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        if (plugin.getCombatManager().isTagged(player)) {
            if (plugin.getConfig().getBoolean("combat.kill-on-logout", true)) {
                player.setHealth(0);
            }
            plugin.getServer().broadcastMessage(plugin.getMessage("combat-log", "{player}", player.getName()));
            plugin.getCombatManager().exitCombat(player.getUniqueId());
        }
    }
}