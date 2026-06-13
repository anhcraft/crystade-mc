package dev.anhcraft.crystade.velocity.push;

import com.velocitypowered.api.command.SimpleCommand;
import dev.anhcraft.crystade.common.PushService;
import dev.anhcraft.crystade.velocity.config.VelocityConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.nio.file.Path;

/**
 * Handles the {@code /crystade push} command on Velocity.
 */
public final class PushCommand implements SimpleCommand {

    private static final String CONFIG_FILE = "crystade.yml";

    private final VelocityConfig config;
    private final Path dataDirectory;

    public PushCommand(VelocityConfig config, Path dataDirectory) {
        this.config = config;
        this.dataDirectory = dataDirectory;
    }

    @Override
    public void execute(Invocation invocation) {
        String[] args = invocation.arguments();
        if (args.length < 1 || !args[0].equalsIgnoreCase("push")) {
            invocation.source().sendMessage(Component.text("Usage: /crystade push", NamedTextColor.RED));
            return;
        }

        if (!invocation.source().hasPermission("crystade.push")) {
            invocation.source().sendMessage(Component.text("You do not have permission to use this command.", NamedTextColor.RED));
            return;
        }

        String apiKey = config.apiKey();
        if (apiKey.isEmpty()) {
            invocation.source().sendMessage(Component.text("No API key configured. Set api-key in config.yml.", NamedTextColor.RED));
            return;
        }

        invocation.source().sendMessage(Component.text("Pushing configuration to Crystade...", NamedTextColor.YELLOW));

        PushService pushService = new PushService();
        try {
            int status = pushService.push(dataDirectory.resolve(CONFIG_FILE), apiKey);
            if (status >= 200 && status < 300) {
                invocation.source().sendMessage(Component.text("Configuration pushed successfully (HTTP " + status + ").", NamedTextColor.GREEN));
            } else {
                invocation.source().sendMessage(Component.text("Push failed with HTTP " + status + ".", NamedTextColor.RED));
            }
        } catch (Exception e) {
            invocation.source().sendMessage(Component.text("Push failed: " + e.getMessage(), NamedTextColor.RED));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("crystade.push");
    }
}
