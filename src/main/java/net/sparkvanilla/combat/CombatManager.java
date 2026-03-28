package net.sparkvanilla.combat;

import fr.mrmlan8.fastboard.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CombatManager {

    private final SparkCombat plugin;
    private final Map<UUID, Integer> combatTimers = new HashMap<>();
    private final Map<UUID, FastBoard> boards = new HashMap<>();

    public CombatManager(SparkCombat plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : new HashMap<>(combatTimers).keySet()) {
                    Player player = Bukkit.getPlayer(uuid);
                    int timeLeft = combatTimers.get(uuid) - 1;

                    if (player == null || timeLeft <= 0) {
                        exitCombat(uuid);
                    } else {
                        combatTimers.put(uuid, timeLeft);
                        updateBoard(player, timeLeft);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public void tagPlayer(Player player, Player target) {
        UUID uuid = player.getUniqueId();
        int duration = plugin.getConfig().getInt("combat.duration", 15);

        if (!combatTimers.containsKey(uuid)) {
            player.sendMessage(plugin.getMessage("tagged", "{player}", target.getName()));
            boards.put(uuid, new FastBoard(player));
        }
        combatTimers.put(uuid, duration);
    }

    public boolean isTagged(Player player) {
        return combatTimers.containsKey(player.getUniqueId());
    }

    public void exitCombat(UUID uuid) {
        combatTimers.remove(uuid);
        FastBoard board = boards.remove(uuid);
        if (board != null) board.delete();

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(plugin.getMessage("out-of-combat"));
        }
    }

    public void shutdown() {
        for (FastBoard board : boards.values()) {
            board.delete();
        }
        boards.clear();
        combatTimers.clear();
    }

    private void updateBoard(Player player, int time) {
        FastBoard board = boards.get(player.getUniqueId());
        if (board == null) return;

        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getMessages().getString("scoreboard.title", "&b&lsparkvanilla.net"));

        board.updateTitle(title);

        List<String> rawLines = plugin.getMessages().getStringList("scoreboard.lines");
        String[] rendered = rawLines.stream()
                .map(l -> ChatColor.translateAlternateColorCodes('&', l.replace("{time}", String.valueOf(time))))
                .toArray(String[]::new);

        board.updateLines(rendered);
    }
}
