package dev.anhcraft.crystade.spigot.config;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Immutable configuration DTO for the Spigot plugin.
 */
public final class SpigotConfig {

    private final String apiKey;

    private SpigotConfig(String apiKey) {
        this.apiKey = apiKey;
    }

    public static SpigotConfig load(FileConfiguration config) {
        String apiKey = config.getString("api-key", "");
        return new SpigotConfig(apiKey);
    }

    public String apiKey() {
        return apiKey;
    }
}
