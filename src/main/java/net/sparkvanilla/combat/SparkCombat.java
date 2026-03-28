package net.sparkvanilla.combat;

import net.sparkvanilla.combat.listeners.CombatListener;
import net.sparkvanilla.combat.listeners.InteractionListener;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SparkCombat extends JavaPlugin {

    private static SparkCombat instance;
    private CombatManager combatManager;
    private FileConfiguration messages;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        saveResource("messages.yml", false);

        reloadMessages();

        combatManager = new CombatManager(this);

        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new InteractionListener(this), this);

        getLogger().info("SparkCombat enabled.");
    }

    @Override
    public void onDisable() {
        if (combatManager != null) combatManager.shutdown();
        getLogger().info("SparkCombat disabled.");
    }

    public void reloadMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(file);
    }

    public String getMessage(String key) {
        String raw = messages.getString("messages." + key, "&cMissing message: " + key);
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public String getMessage(String key, String... replacements) {
        String raw = messages.getString("messages." + key, "&cMissing message: " + key);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            raw = raw.replace(replacements[i], replacements[i + 1]);
        }
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public static SparkCombat getInstance() { return instance; }
    public CombatManager getCombatManager() { return combatManager; }
}
