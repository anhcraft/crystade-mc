package dev.anhcraft.crystade.spigot.push;

import dev.anhcraft.crystade.common.PushService;
import dev.anhcraft.crystade.spigot.config.SpigotConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.nio.file.Path;

/**
 * Handles the {@code /crystade push} subcommand.
 */
public final class PushCommand implements CommandExecutor {

    private static final String CONFIG_FILE = "crystade.yml";

    private final SpigotConfig config;
    private final Path dataFolder;

    public PushCommand(SpigotConfig config, Path dataFolder) {
        this.config = config;
        this.dataFolder = dataFolder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1 || !args[0].equalsIgnoreCase("push")) {
            sender.sendMessage(ChatColor.RED + "Usage: /crystade push");
            return true;
        }

        if (!sender.hasPermission("crystade.push")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        String apiKey = config.apiKey();
        if (apiKey.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No API key configured. Set api-key in config.yml.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "Pushing configuration to Crystade...");

        PushService pushService = new PushService();
        try {
            int status = pushService.push(dataFolder.resolve(CONFIG_FILE), apiKey);
            if (status >= 200 && status < 300) {
                sender.sendMessage(ChatColor.GREEN + "Configuration pushed successfully (HTTP " + status + ").");
            } else {
                sender.sendMessage(ChatColor.RED + "Push failed with HTTP " + status + ".");
            }
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Push failed: " + e.getMessage());
        }

        return true;
    }
}
