package clandestino.plugin;

import clandestino.util.TextUtil;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration manager. Manages the plugin's configuration file and its options.
 */
public class ConfigManager {

    public static final String GENERAL_PREFIX = "general.";
    public static final String CLX_VS_GP_PREFIX = "clx-vs-gp.";
    public static final String HELP_PREFIX = "help.";
    public static final String SELLABLE_PREFIX = "sellable.";
    public static final String VANTAGENS_PREFIX = "vantagens.";
    public static final String DEATH_MESSAGE_REPLACER_PREFIX = "death-msg-replacer.";
    public static final String NETHER_TO_OVERWORLD_BLOCKER_PREFIX = "nether-to-overworld-blocker.";

    public static final String LANGUAGE_PREFIX = "language.";
    public static final String MODULE_ENABLED = "enabled";

    private static final Map<String, Object> DEFAULTS = createDefaults();

    /**
     * The plugin.
     */
    private final JavaPlugin plugin;

    /**
     * The single class instance.
     */
    private static ConfigManager instance;

    /**
     * The name of the configuration file.
     */
    private static final String FILE_NAME = "config.yml";

    /**
     * Creates the config manager.
     *
     * @param plugin the plugin.
     */
    private ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        createDefaultConfig();
    }

    /**
     * Finds an integer in the configuration.
     *
     * @param key the key of the integer. (one of the constants)
     * @return the found integer.
     */
    public Integer getInt(String key) {
        return plugin.getConfig().getInt(key);
    }

    /**
     * Finds a string in the configuration.
     *
     * @param key the key of the string. (one of the constants)
     * @return the found string.
     */
    public String getString(String key) {
        return plugin.getConfig().getString(key);
    }

    /**
     * Finds a boolean in the configuration.
     *
     * @param key the key of the boolean. (one of the constants)
     * @return the found boolean.
     */
    public boolean getBoolean(String key) {
        return plugin.getConfig().getBoolean(key);
    }

    /**
     * Finds a string list in the configuration.
     *
     * @param key the key of the string list. (one of the constants)
     * @return the found string list.
     */
    public List<String> getStringList(String key) {
        return plugin.getConfig().getStringList(key);
    }

    /**
     * Finds a double in the configuration.
     *
     * @param key the key of the double. (one of the constants)
     * @return the found double.
     */
    public double getDouble(String key) {
        return plugin.getConfig().getDouble(key);
    }

    public String getColoredString(String key, String... args) {
        String value = getString(key);
        if (value == null) {
            return key;
        }

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg == null) {
                arg = "null";
            }

            value = value.replace(String.format("{%d}", i), arg);
        }

        return TextUtil.translateColoredText(value);
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    /**
     * Returns the (single) config manager instance.
     *
     * @return the config manager instance.
     */
    public static ConfigManager getInstance() {
        return instance;
    }

    /**
     * Loads the configuration.
     *
     * @param plugin the plugin.
     */
    public static void init(JavaPlugin plugin) {
        instance = new ConfigManager(plugin);
        instance.save();
    }

    /**
     * Saves the configuration if it doesn't exist (from the default values).
     */
    private void save() {
        File configurationFile = new File(plugin.getDataFolder(), FILE_NAME);
        if (configurationFile.exists()) {
            return;
        }

        try {
            plugin.getConfig().save(configurationFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save the config file.");
        }
    }

    /**
     * Loads the default configuration values.
     */
    private void createDefaultConfig() {
        Configuration config = plugin.getConfig();
        config.addDefaults(DEFAULTS);
        config.options().copyDefaults(true);
    }

    private static Map<String, Object> createDefaults() {
        Map<String, Object> defaults = new HashMap<>();

        defaults.put(GENERAL_PREFIX + LANGUAGE_PREFIX + "no-permission", "&cNão tens permissão.");

        defaults.put(CLX_VS_GP_PREFIX + MODULE_ENABLED, false);
        defaults.put(CLX_VS_GP_PREFIX + "freeze-time", 500);

        defaults.put(HELP_PREFIX + MODULE_ENABLED, true);
        defaults.put(HELP_PREFIX + "command-name", "ajuda");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "available-helps", "&eAjudas disponíveis:");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "type-for-help", "&eEscreve {0} (uma ajuda disponível)");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "reloaded", "&eAjudas reloaded.");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "invalid-help", "&cAjuda '{0}' inválida.");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "invalid-help-available-helps", "&eAjudas disponíveis:&3");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "around-help", "&6---");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "list-color", "&3");
        defaults.put(HELP_PREFIX + LANGUAGE_PREFIX + "list-separator-color", "&e");

        defaults.put(SELLABLE_PREFIX + MODULE_ENABLED, true);
        defaults.put(SELLABLE_PREFIX + LANGUAGE_PREFIX + "intro",
                "&eItems que podes vender usando &b/sell hand &eou &b/sell (nome do item)&e:");
        defaults.put(SELLABLE_PREFIX + LANGUAGE_PREFIX + "item-price", "&7- &6{0} &7por &6{1} &7cada (&6{2} &7por stack)");
        defaults.put(SELLABLE_PREFIX + LANGUAGE_PREFIX + "currency-symbol", "€");
        defaults.put(SELLABLE_PREFIX + LANGUAGE_PREFIX + "wrong-syntax", "&cPara ver quanto vale um item, usa o comando &a/worth &cou &a/worth (item)&c.");

        defaults.put(VANTAGENS_PREFIX + MODULE_ENABLED, false);
        defaults.put(VANTAGENS_PREFIX + "urls.vip", "http://vip.com/");
        defaults.put(VANTAGENS_PREFIX + "urls.mvp", "http://mvp.com/");

        defaults.put(DEATH_MESSAGE_REPLACER_PREFIX + MODULE_ENABLED, false);
        defaults.put(NETHER_TO_OVERWORLD_BLOCKER_PREFIX + MODULE_ENABLED, false);

        return defaults;
    }
}
