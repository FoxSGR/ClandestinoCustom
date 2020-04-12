package net.foxsgr.clandestinocustom.modules.commands

import net.foxsgr.clandestinocustom.config.PerksConfig
import net.foxsgr.clandestinocustom.plugin.ClandestinoCommand
import net.foxsgr.clandestinocustom.plugin.ClandestinoCustom
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

class PerksCommand(plugin: ClandestinoCustom) : ClandestinoCommand(plugin, MODULE_NAME, MODULE_NAME, PerksConfig(plugin)) {

    companion object {
        const val MODULE_NAME = "perks"
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            sendAvailable(sender)
            return true
        }

        if (args[0].equals("reload", ignoreCase = true)) {
            config!!.load()
            sender.sendMessage(getI18n("reloaded"))
            return true
        }

        val info = config!!.getString("${PerksConfig.RANKS_PREFIX}.${args[0].toLowerCase()}")
        if (info == null) {
            sender.sendMessage(getI18n("invalid-rank"))
            sendAvailable(sender)
        } else {
            sender.sendMessage(info)
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        val ranks = config!!.getKeys(PerksConfig.RANKS_PREFIX)
        if (args.isEmpty()) {
            return ranks.toList()
        }

        return ranks.filter { key: String -> key.toLowerCase().contains(args[0].toLowerCase()) }
    }

    private fun sendAvailable(sender: CommandSender) {
        val mainColor = ChatColor.YELLOW
        val secColor = ChatColor.GOLD
        val result = StringBuilder()
                .append(mainColor)
                .append("Ranks disponíveis: ")
        val ranks = config!!.getKeys(PerksConfig.RANKS_PREFIX).sorted()

        for (i in ranks.indices) {
            val rank = ranks[i].toUpperCase()
            result.append(secColor).append(rank)

            if (i != ranks.size - 1) {
                result.append(mainColor).append(", ")
            }
        }

        result.append('\n').append(mainColor).append("Escreve ").append(secColor).append("/vantagens (rank)")
                .append(mainColor).append(" para ver as vantagens de um rank.")
        sender.sendMessage(result.toString())
    }
}