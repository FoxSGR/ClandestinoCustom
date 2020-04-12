package net.foxsgr.clandestinocustom.modules.commands

import net.foxsgr.clandestinocustom.plugin.ClandestinoCommand
import net.foxsgr.clandestinocustom.plugin.ClandestinoCustom
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class CCCommand(plugin: ClandestinoCustom) : ClandestinoCommand(plugin, MODULE_NAME, COMMAND_NAME) {

    companion object {
        const val MODULE_NAME = "auto-reconnect"
        const val COMMAND_NAME = "cc"

        private const val AUTO_RECO_CHECK_COMMAND = "autorecocheck"
        private val AUTO_RECO_CHECK_PERMISSION = "${ClandestinoCustom.PERMISSIONS_PREFIX}.cc.$AUTO_RECO_CHECK_COMMAND"
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            return true
        }

        if (args[0].equals(AUTO_RECO_CHECK_COMMAND, ignoreCase = true)) {
            sender.sendMessage("$AUTO_RECO_CHECK_COMMAND${sender.hasPermission(AUTO_RECO_CHECK_PERMISSION)}")
        }

        return true
    }
}