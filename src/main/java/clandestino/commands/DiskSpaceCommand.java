package clandestino.commands;

import clandestino.CustomClandestino;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.stream.Stream;

public class DiskSpaceCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    private static final String PERMISSION = CustomClandestino.PERMISSIONS_PREFIX + "diskspace";

    public DiskSpaceCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "Não tens permissão.");
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                Path folder = Paths.get(args[0]);
                try (Stream<Path> pathStream = Files.walk(folder)) {
                    long size = pathStream.filter(p -> p.toFile().isFile())
                            .mapToLong(p -> p.toFile().length())
                            .sum();
                    sender.sendMessage(ChatColor.AQUA + "Espaço: " + ChatColor.GRAY + humanReadableByteCount(size));
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "Error while trying to find size.", e);
                    sender.sendMessage(ChatColor.RED + "Erro: " + e.getClass().getSimpleName()
                            + "\n" + ChatColor.WHITE + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);

        return true;
    }

    private static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < unit) {
            return bytes + " B";
        }

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
