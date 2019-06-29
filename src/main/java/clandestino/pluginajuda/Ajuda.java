package clandestino.pluginajuda;

import clandestino.lib.PluginUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin de ajuda.
 */
@SuppressWarnings("unused")
public final class Ajuda extends JavaPlugin {

    /**
     * Invocado quando o plugin é inicializado. Regista o comando.
     */
    @Override
    public void onEnable() {
        super.onEnable();
        getLogger().info("Iniciando");
        PluginUtil.registerCommand(this, CommandAjuda.COMMAND_NAME, new CommandAjuda(this));
    }
}
