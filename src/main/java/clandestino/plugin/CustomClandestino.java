package clandestino.plugin;

import clandestino.commands.*;
import clandestino.listeners.CLXvsGPBugPreventer;
import clandestino.listeners.NetherToOverworldBlocker;
import clandestino.listeners.PlayerDeathListener;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

@SuppressWarnings("unused")
public final class CustomClandestino extends JavaPlugin {

    public static final String PERMISSIONS_PREFIX = "clandestinocustom.";

    private static final String AJUDA_COMMAND = "ajuda";
    private static final String DISK_SPACE_COMMAND = "diskspace";
    private static final String SELLABLE_COMMAND = "sellable";
    private static final String VANTAGENS_COMMAND = "vantagens";
    private static final String CC_COMMAND = "CC";

    @Override
    public void onEnable() {
        super.onEnable();
        ConfigManager.init(this);

        registerHelp();
        registerDiskSpaceCommand();
        registerSellable();
        registerVantagens();
        registerCCCommand();

        registerPortalBlocker();
        registerNameChanger();
        registerPvPTagBugPreventer();
    }

    private void registerVantagens() {
        if (!shouldEnable(ConfigManager.VANTAGENS_PREFIX)) {
            return;
        }

        PluginCommand command = getCommand(VANTAGENS_COMMAND);
        assert command != null;

        initModule(VANTAGENS_COMMAND, () -> {
            VantagensCommand vantagensCommand = new VantagensCommand(this);
            vantagensCommand.readConfig();

            command.setExecutor(vantagensCommand);
            command.setTabCompleter(vantagensCommand);
        });
    }

    private void registerHelp() {
        if (!shouldEnable(ConfigManager.HELP_PREFIX)) {
            return;
        }

        String commandName = ConfigManager.getInstance().getString(ConfigManager.HELP_PREFIX + "command-name");
        PluginCommand command = getCommand(commandName);
        assert command != null;

        HelpsCommand helpCommand = new HelpsCommand(this);
        helpCommand.readHelps();

        command.setExecutor(helpCommand);
        command.setTabCompleter(helpCommand);
    }

    private void registerDiskSpaceCommand() {
        PluginCommand command = getCommand(DISK_SPACE_COMMAND);
        assert command != null;

        DiskSpaceCommand diskSpaceCommand = new DiskSpaceCommand(this);
        command.setExecutor(diskSpaceCommand);
    }

    private void registerSellable() {
        if (!shouldEnable(ConfigManager.SELLABLE_PREFIX)) {
            return;
        }

        initModule(SELLABLE_COMMAND, () -> {
            PluginCommand command = getCommand(SELLABLE_COMMAND);
            assert command != null;

            SellableCommand sellableCommand = new SellableCommand(this);
            command.setExecutor(sellableCommand);
        });
    }

    private void registerCCCommand() {
        PluginCommand command = getCommand(CC_COMMAND);
        assert command != null;

        CCCommand ccCommand = new CCCommand();
        command.setExecutor(ccCommand);
    }

    private void registerPortalBlocker() {
        NetherToOverworldBlocker netherToOverworldBlocker = new NetherToOverworldBlocker(this);
        getServer().getPluginManager().registerEvents(netherToOverworldBlocker, this);
    }

    private void registerNameChanger() {
        PlayerDeathListener playerDeathListener = new PlayerDeathListener();
        getServer().getPluginManager().registerEvents(playerDeathListener, this);
    }

    private void registerPvPTagBugPreventer() {
        if (!shouldEnable(ConfigManager.CLX_VS_GP_PREFIX)) {
            return;
        }

        initModule("CombatLogX and GriefPrevention bug preventer", () -> {
            CLXvsGPBugPreventer pvpTagBugPreventer = new CLXvsGPBugPreventer(this);
            getServer().getPluginManager().registerEvents(pvpTagBugPreventer, this);
        });
    }

    private void initModule(String moduleName, Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "Could not load {0} module: {1}", new String[] {moduleName, e.getMessage()});
        }
    }

    private static boolean shouldEnable(String configPrefix) {
        ConfigManager configManager = ConfigManager.getInstance();
        String configKey = configPrefix + ConfigManager.MODULE_ENABLED;
        return configManager.getBoolean(configKey);
    }
}
