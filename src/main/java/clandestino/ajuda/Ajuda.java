package clandestino.ajuda;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin de ajuda.
 */
@SuppressWarnings("unused")
public final class Ajuda extends JavaPlugin {

    private static final String COMMAND_NAME = "ajuda";

    /**
     * Invocado quando o plugin é inicializado. Regista o comando.
     */
    @Override
    public void onEnable() {
        super.onEnable();

        PluginCommand command = getCommand(COMMAND_NAME);
        if (command == null) {
            throw new IllegalStateException("Could not register the command \"" + COMMAND_NAME + "\".");
        }

        CommandAjuda commandAjuda = new CommandAjuda(this);
        commandAjuda.readConfig();

        command.setExecutor(commandAjuda);
        command.setTabCompleter(commandAjuda);
    }
}
