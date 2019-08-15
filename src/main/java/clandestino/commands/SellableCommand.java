package clandestino.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;

public class SellableCommand implements CommandExecutor {

    private JavaPlugin plugin;

    private String values;
    private byte[] checksum;

    private static final String ESSENTIALS_FOLDER = "Essentials";
    private static final String WORTH_FILE = "worth.yml";
    private static final String WORTH_SECTION = "worth";

    public SellableCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        load();
        checksum = fileChecksum();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length != 0) {
            sender.sendMessage(ChatColor.RED + "Para ver quanto vale um item, usa o comando " + ChatColor.GREEN
                    +  "/worth" + ChatColor.RED + " ou " + ChatColor.GREEN + "/worth (nome do item)" + ChatColor.RED
                    + ".");
            return true;
        }

        byte[] currentChecksum = fileChecksum();
        if (!Arrays.equals(currentChecksum, checksum)) {
            plugin.getLogger().info("File change detected. Reloading.");
            load();
            checksum = currentChecksum;
        }

        sender.sendMessage(values);
        return true;
    }

    private void load() {
        File file = createFile();
        ConfigurationSection worthSection = loadFileConfiguration(file);
        Set<String> keys = worthSection.getKeys(false);

        StringBuilder valuesBuilder = new StringBuilder();
        valuesBuilder.append(ChatColor.YELLOW).append("Items que podes vender usando ")
                .append(ChatColor.AQUA).append("/sell hand").append(ChatColor.YELLOW).append(" ou ")
                .append(ChatColor.AQUA).append("/sell (nome do item)").append(ChatColor.YELLOW).append(":\n");

        for (String key : keys) {
            double value = worthSection.getDouble(key);
            double stackValue = value * 64;

            valuesBuilder.append(ChatColor.GRAY).append("- ")
                    .append(ChatColor.GOLD).append(key).append(ChatColor.GRAY).append(" por ")
                    .append(ChatColor.GOLD).append(String.format("%.2f", value)).append('Ç')
                    .append(ChatColor.GRAY).append(" cada (").append(ChatColor.GOLD).append(stackValue).append('Ç')
                    .append(ChatColor.GRAY).append(" por stack)\n");
        }

        values = valuesBuilder.toString();
    }

    private ConfigurationSection loadFileConfiguration(File file) {
        FileConfiguration fileConfiguration = new YamlConfiguration();
        try {
            fileConfiguration.load(file);
            ConfigurationSection configurationSection = fileConfiguration.getConfigurationSection(WORTH_SECTION);
            if (configurationSection == null) {
                throw new IllegalStateException("Could not find the " + WORTH_SECTION + " section in " + WORTH_FILE);
            }

            return configurationSection;
        } catch (IOException | InvalidConfigurationException e) {
            throw new IllegalStateException("Could not load " + WORTH_FILE, e);
        }
    }

    private File createFile() {
        File file = plugin.getDataFolder().getParentFile();
        file = new File(file, ESSENTIALS_FOLDER);
        return new File(file, WORTH_FILE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private byte[] fileChecksum() {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }

        File file = createFile();
        try (InputStream inputStream = new FileInputStream(file);
             DigestInputStream digestInputStream = new DigestInputStream(inputStream, md)) {
            while (digestInputStream.read() != -1) {
                // Clear the data
            }

            md = digestInputStream.getMessageDigest();
            return md.digest();
        } catch (IOException e) {
            plugin.getLogger().warning("Could not calculate the checksum of " + WORTH_FILE);
            return new byte[] {};
        }
    }
}
