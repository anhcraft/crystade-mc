package dev.anhcraft.crystade.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import dev.anhcraft.crystade.velocity.config.VelocityConfig;
import dev.anhcraft.crystade.velocity.push.PushCommand;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public final class CrystadeVelocity {

    private static final String PUSH_CONFIG_FILE = "crystade.yml";

    private final Logger logger;
    private final Path dataDirectory;
    private final CommandManager commandManager;

    @Inject
    public CrystadeVelocity(
            Logger logger,
            @DataDirectory Path dataDirectory,
            CommandManager commandManager
    ) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        this.commandManager = commandManager;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        VelocityConfig config = VelocityConfig.load(dataDirectory);

        // Copy default crystade.yml if not present
        copyDefaultResource(PUSH_CONFIG_FILE);

        // Register /crystade command
        commandManager.register(
                commandManager.metaBuilder("crystade")
                        .plugin(this)
                        .build(),
                new PushCommand(config, dataDirectory)
        );

        logger.info("CrystadeVelocity enabled");
    }

    private void copyDefaultResource(String fileName) {
        Path target = dataDirectory.resolve(fileName);
        if (!Files.exists(target)) {
            try (InputStream in = getClass().getResourceAsStream("/" + fileName)) {
                if (in != null) {
                    Files.createDirectories(target.getParent());
                    Files.copy(in, target);
                    logger.info("Created default {}", fileName);
                }
            } catch (IOException e) {
                logger.warn("Failed to create default {}: {}", fileName, e.getMessage());
            }
        }
    }
}
