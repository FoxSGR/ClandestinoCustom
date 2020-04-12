package net.foxsgr.clandestinocustom.plugin

import net.foxsgr.clandestinocustom.config.Config
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

abstract class ClandestinoCommand(plugin: ClandestinoCustom,
                                  name: String,
                                  private val commandName: String,
                                  config: Config? = null) : ClandestinoModule(plugin, config, name), CommandExecutor, TabCompleter {

    companion object {
        val EMPTY_TAB_COMPLETION = ArrayList<String>()
    }

    override fun register() {
        val command = plugin.getCommand(commandName)
        if (command == null) {
            plugin.logger.warning("The command '$commandName' is not properly registered.")
            return
        }

        command.setExecutor(this)
        command.tabCompleter = this
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        return EMPTY_TAB_COMPLETION
    }
}