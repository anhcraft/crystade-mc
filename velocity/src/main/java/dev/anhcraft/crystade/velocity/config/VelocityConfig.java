package dev.anhcraft.crystade.velocity.config;

import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Immutable configuration DTO for the Velocity plugin.
 * Reads from {@code config.yml} in the plugin data directory using Velocity's built-in Configurate YAML support.
 */
public final class VelocityConfig {

    private static final String FILE_NAME = "config.yml";

    private final String apiKey;

    private VelocityConfig(String apiKey) {
        this.apiKey = apiKey;
    }

    public static VelocityConfig load(Path dataDirectory) {
        Path configFile = dataDirectory.resolve(FILE_NAME);
        YamlConfigurationLoader loader = YamlConfigurationLoader.builder()
                .path(configFile)
                .build();

        // Write default config if it doesn't exist
        if (!Files.exists(configFile)) {
            try {
                Files.createDirectories(dataDirectory);
                Files.copy(
                    Objects.requireNonNull(VelocityConfig.class.getResourceAsStream("/" + FILE_NAME)),
                    configFile
                );
            } catch (IOException e) {
                // Fall through — loader will handle missing file
            }
        }

        try {
            CommentedConfigurationNode root = loader.load();
            String apiKey = root.node("api-key").getString("your_api_key");
            return new VelocityConfig(apiKey);
        } catch (IOException e) {
            return new VelocityConfig("");
        }
    }

    public String apiKey() {
        return apiKey;
    }
}
