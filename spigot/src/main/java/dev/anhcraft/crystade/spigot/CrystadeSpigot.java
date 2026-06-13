package dev.anhcraft.crystade.spigot;

import dev.anhcraft.crystade.spigot.config.SpigotConfig;
import dev.anhcraft.crystade.spigot.push.PushCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Main plugin class for the Spigot/Paper platform.
 */
public final class CrystadeSpigot extends JavaPlugin {

    private static final String PUSH_CONFIG_FILE = "crystade.yml";

    @Override
    public void onEnable() {
        saveDefaultConfig();
        SpigotConfig config = SpigotConfig.load(getConfig());

        // Copy default crystade.yml if not present
        copyDefaultResource(PUSH_CONFIG_FILE);

        Objects.requireNonNull(getCommand("crystade")).setExecutor(
                new PushCommand(config, getDataFolder().toPath())
        );

        getLogger().info("CrystadeSpigot enabled");
    }

    private void copyDefaultResource(String fileName) {
        Path target = getDataFolder().toPath().resolve(fileName);
        if (!Files.exists(target)) {
            try (InputStream in = getClass().getResourceAsStream("/" + fileName)) {
                if (in != null) {
                    Files.createDirectories(target.getParent());
                    Files.copy(in, target);
                    getLogger().info("Created default " + fileName);
                }
            } catch (IOException e) {
                getLogger().warning("Failed to create default " + fileName + ": " + e.getMessage());
            }
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("CrystadeSpigot disabled");
    }
}
