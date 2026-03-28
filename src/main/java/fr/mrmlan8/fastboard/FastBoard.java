package fr.mrmlan8.fastboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class FastBoard {

    private static final String[] SIDEBAR_ENTRIES = generateEntries();

    private final Player player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private final List<String> lines = new ArrayList<>();
    private String title = "";

    public FastBoard(Player player) {
        this.player = player;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("fastboard", "dummy", title);
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        player.setScoreboard(scoreboard);
    }

    public void updateTitle(String title) {
        this.title = title;
        objective.setDisplayName(title);
    }

    public void updateLines(String... newLines) {
        List<String> updated = new ArrayList<>();
        for (String line : newLines) {
            updated.add(line == null ? "" : line);
        }
        applyLines(updated);
    }

    private void applyLines(List<String> newLines) {
        int currentSize = lines.size();
        int newSize = newLines.size();

        for (int i = currentSize; i > newSize; i--) {
            String entry = SIDEBAR_ENTRIES[i - 1];
            Team team = scoreboard.getTeam("line" + i);
            if (team != null) team.unregister();
            scoreboard.resetScores(entry);
        }

        for (int i = 0; i < newSize; i++) {
            String entry = SIDEBAR_ENTRIES[i];
            String content = newLines.get(i);

            String prefix = content.length() > 64 ? content.substring(0, 64) : content;
            String suffix = content.length() > 64 ? content.substring(64, Math.min(content.length(), 128)) : "";

            Team team = scoreboard.getTeam("line" + (i + 1));
            if (team == null) {
                team = scoreboard.registerNewTeam("line" + (i + 1));
                team.addEntry(entry);
            }

            team.setPrefix(prefix);
            team.setSuffix(suffix);

            objective.getScore(entry).setScore(newSize - i);
        }

        lines.clear();
        lines.addAll(newLines);
    }

    public void delete() {
        for (int i = 0; i < lines.size(); i++) {
            Team team = scoreboard.getTeam("line" + (i + 1));
            if (team != null) team.unregister();
        }
        lines.clear();
        objective.unregister();
        if (player.isOnline()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }

    private static String[] generateEntries() {
        String[] entries = new String[16];
        for (int i = 0; i < 16; i++) {
            entries[i] = ChatColor.values()[i] + "\u00a7r";
        }
        return entries;
    }
}
