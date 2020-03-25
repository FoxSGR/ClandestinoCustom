package clandestino.commands;

import clandestino.plugin.ConfigManager;
import clandestino.plugin.CustomClandestino;
import clandestino.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.stream.Stream;

public class DiskSpaceCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    private static final String PERMISSION = CustomClandestino.PERMISSIONS_PREFIX + "diskspace";
    private static final String SORT_COMMAND = "sort";

    private static final String ERROR_PREFIX = ChatColor.RED + "Error: ";

    public DiskSpaceCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission(PERMISSION)) {
            ConfigManager config = ConfigManager.getInstance();
            String noPermissionStr = config.getString(
                    ConfigManager.GENERAL_PREFIX + ConfigManager.LANGUAGE_PREFIX + "no-permission"
            );
            sender.sendMessage(noPermissionStr);
            return true;
        }

        if (args.length == 1) {
            single(sender, args);
        } else if (args.length == 2 && args[0].equalsIgnoreCase(SORT_COMMAND)) {
            sort(sender, args[1], Integer.MAX_VALUE);
        } else if (args.length == 3 && args[0].equalsIgnoreCase(SORT_COMMAND)) {
            return sortAmount(sender, args);
        } else {
            return false;
        }

        return true;
    }

    private void single(CommandSender sender, String[] args) {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Path path = Paths.get(args[0]);
                    long size = size(path);
                    sender.sendMessage(ChatColor.AQUA + "Size: " + ChatColor.GRAY + humanReadableByteCount(size));
                } catch (IOException | InvalidPathException e) {
                    plugin.getLogger().log(Level.WARNING, "Error while trying to find size.", e);
                    sender.sendMessage(ERROR_PREFIX + e.getClass().getSimpleName() + "\n" + ChatColor.WHITE
                            + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    private void sort(CommandSender sender, String name, int amount) {
        try {
            File folder = new File(name);

            if (!folder.isDirectory()) {
                sender.sendMessage(ERROR_PREFIX + "Not a folder.");
                return;
            }

            File[] files = folder.listFiles();
            if (files == null) {
                sender.sendMessage(ERROR_PREFIX + "Folder doesn't exist.");
                return;
            }

            Set<Pair<String, Long>> sizes = new TreeSet<>(Comparator.comparing(e -> -e.second));
            for (File file : files) {
                long size = size(Paths.get(file.toURI()));
                Pair<String, Long> fileSizePair = new Pair<>(file.getName(), size);
                sizes.add(fileSizePair);
            }

            StringBuilder result = new StringBuilder();
            int i = 0;
            for (Pair<String, Long> fileSizePair : sizes) {
                if (i >= amount) {
                    break;
                }

                String size = humanReadableByteCount(fileSizePair.second);
                result.append(ChatColor.AQUA).append(i + 1).append(". ").append(ChatColor.WHITE)
                        .append(fileSizePair.first).append(" - ").append(ChatColor.GRAY).append(size).append('\n');
                i++;
            }

            sender.sendMessage(result.toString());
        } catch (IOException | InvalidPathException e) {
            plugin.getLogger().log(Level.WARNING, "Error while trying to sort sizes.", e);
            sender.sendMessage(ERROR_PREFIX + e.getClass().getSimpleName() + "\n" + ChatColor.WHITE + e.getMessage());
        }
    }

    private boolean sortAmount(CommandSender sender, String[] args) {
        try {
            int number = Integer.parseInt(args[1]);

            new BukkitRunnable() {
                @Override
                public void run() {
                    sort(sender, args[2], number);
                }
            }.runTaskAsynchronously(plugin);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static long size(Path path) throws IOException {
        if (path.toFile().isDirectory()) {
            return folderSize(path);
        } else if (path.toFile().isFile()) {
            return path.toFile().length();
        } else {
            throw new IOException("The file/folder doesn't exist.");
        }
    }

    private static long folderSize(Path folder) throws IOException {
        try (Stream<Path> pathStream = Files.walk(folder)) {
            return pathStream.filter(p -> p.toFile().isFile())
                    .mapToLong(p -> p.toFile().length())
                    .sum();
        }
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
