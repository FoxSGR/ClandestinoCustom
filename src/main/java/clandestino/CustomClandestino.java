package clandestino;

import clandestino.commands.*;
import clandestino.listeners.NetherToOverworldBlocker;
import clandestino.listeners.PlayerDeathListener;
import clandestino.listeners.PvPTagBugPreventer;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

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

        registerAjuda();
        registerDiskSpaceCommand();
        registerSellable();
        registerVantagens();
        registerCCCommand();

        registerPortalBlocker();
        registerNameChanger();
        registerPvPTagBugPreventer();
    }

    private void registerVantagens() {
        PluginCommand command = getCommand(VANTAGENS_COMMAND);
        assert command != null;

        VantagensCommand vantagensCommand = new VantagensCommand(this);
        vantagensCommand.readConfig();

        command.setExecutor(vantagensCommand);
        command.setTabCompleter(vantagensCommand);
    }

    private void registerAjuda() {
        PluginCommand command = getCommand(AJUDA_COMMAND);
        assert command != null;

        AjudaCommand ajudaCommand = new AjudaCommand(this);
        ajudaCommand.readAjudas();

        command.setExecutor(ajudaCommand);
        command.setTabCompleter(ajudaCommand);
    }

    private void registerDiskSpaceCommand() {
        PluginCommand command = getCommand(DISK_SPACE_COMMAND);
        assert command != null;

        DiskSpaceCommand diskSpaceCommand = new DiskSpaceCommand(this);
        command.setExecutor(diskSpaceCommand);
    }

    private void registerSellable() {
        PluginCommand command = getCommand(SELLABLE_COMMAND);
        assert command != null;

        SellableCommand sellableCommand = new SellableCommand(this);
        command.setExecutor(sellableCommand);
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
        PvPTagBugPreventer pvpTagBugPreventer = new PvPTagBugPreventer();
        getServer().getPluginManager().registerEvents(pvpTagBugPreventer, this);
    }
}
