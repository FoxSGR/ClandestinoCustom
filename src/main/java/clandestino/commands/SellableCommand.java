package clandestino.commands;

import clandestino.plugin.ConfigManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;

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
            sender.sendMessage(getString("wrong-syntax"));
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
        valuesBuilder.append(getString("intro")).append('\n');

        for (String key : keys) {
            double value = worthSection.getDouble(key);
            double stackValue = value * 64;

            String strValue = String.format("%.2f%s", value, getString("currency-symbol"));
            String strStackValue = String.format("%.2f%s", stackValue, getString("currency-symbol"));
            String line = getString("item-price", key, strValue, strStackValue);
            valuesBuilder.append(line).append('\n');
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
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, "Could not find {0} {1} file.",
                    new String[] {ESSENTIALS_FOLDER, WORTH_FILE});
            return new byte[] {};
        } catch (IOException e) {
            plugin.getLogger().warning("Could not calculate the checksum of " + WORTH_FILE);
            return new byte[] {};
        }
    }

    private static String getString(String key, String... args) {
        ConfigManager config = ConfigManager.getInstance();
        return config.getColoredString(ConfigManager.SELLABLE_PREFIX + ConfigManager.LANGUAGE_PREFIX + key, args);
    }
}
