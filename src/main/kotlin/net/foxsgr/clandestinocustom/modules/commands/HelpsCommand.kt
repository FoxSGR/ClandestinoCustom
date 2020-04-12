package net.foxsgr.clandestinocustom.modules.commands

import net.foxsgr.clandestinocustom.config.HelpsConfig
import net.foxsgr.clandestinocustom.plugin.ClandestinoCommand
import net.foxsgr.clandestinocustom.plugin.ClandestinoCustom
import net.foxsgr.clandestinocustom.util.contentFromFile
import net.foxsgr.clandestinocustom.util.withoutExtension
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import java.io.File
import java.util.*
import java.util.logging.Level
import kotlin.collections.HashMap

class HelpsCommand(plugin: ClandestinoCustom) : ClandestinoCommand(plugin, MODULE_NAME, COMMAND_NAME, HelpsConfig(plugin)) {

    companion object {
        const val MODULE_NAME = "helps"
        const val COMMAND_NAME = "ajuda"

        private const val HELPS_FOLDER = "helps"
        private const val RELOAD_COMMAND = "reload"
    }

    private val helps = HashMap<String, String>()

    init {
        readHelps()
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            val typeForHelp = getI18n("type-for-help", COMMAND_NAME)
            sender.sendMessage("${getI18n("available-helps")} ${allHelps()}\n$typeForHelp")
            return true
        }

        if (args[0].equals(RELOAD_COMMAND, ignoreCase = true) && hasPermission(sender, RELOAD_COMMAND)) {
            readHelps()
            sender.sendMessage(getI18n("reloaded"))
            return true
        }

        val helpName = helpName(args)
        val help = helps[helpName]
        if (help == null) {
            sender.sendMessage("${getI18n("invalid-help", helpName)}\n${getI18n("invalid-help-available-helps")} ${allHelps()}")
        } else {
            sender.sendMessage(help)
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        val fileName = withoutExtension(args.joinToString(separator = " ")).toLowerCase()
        return helps.keys.filter { it.contains(fileName) }
    }

    private fun readHelps() {
        helps.clear()
        val files = helpsFolder().listFiles() ?: return

        for (file in files) {
            val aroundHelp = getI18n("around-help")
            var content = contentFromFile(file)
                    .replace("&", "§")
                    .replace("\r", "")
            content = "$aroundHelp\n$content\n$aroundHelp"

            val fileName = decodeFileName(file).replace("$", "ç")
            helps[fileName] = content
        }
    }

    private fun allHelps(): String {
        val ajudasDisponiveis = ArrayList<String>(helps.keys)
        ajudasDisponiveis.sort()

        val listColor = getI18n("list-color")
        val listSeparatorColor = getI18n("list-separator-color")

        val ajudasBuilder = StringBuilder(listColor)
        for (i in ajudasDisponiveis.indices) {
            val ajuda = ajudasDisponiveis[i]
            ajudasBuilder.append(ajuda)

            if (i != ajudasDisponiveis.size - 1) {
                ajudasBuilder.append(listSeparatorColor).append(", ").append(listColor)
            }
        }

        return ajudasBuilder.toString()
    }

    private fun decodeFileName(file: File): String {
        val fileName = file.name.toLowerCase()
        return withoutExtension(fileName)
    }

    private fun helpsFolder(): File {
        val folder = File(plugin.dataFolder, HELPS_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()

            try {
                File(folder, "example").writeText("&bThis is an example!\nSupports multiline text!")
            } catch (e: Exception) {
                plugin.logger.log(Level.WARNING, "Could not write example help.", e)
            }
        }

        return folder
    }

    private fun helpName(args: Array<String>): String {
        return withoutExtension(args.joinToString(separator = " ")).toLowerCase()
    }
}