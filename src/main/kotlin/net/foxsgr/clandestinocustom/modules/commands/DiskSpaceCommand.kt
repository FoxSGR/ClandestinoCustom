package net.foxsgr.clandestinocustom.modules.commands

import net.foxsgr.clandestinocustom.config.Config
import net.foxsgr.clandestinocustom.plugin.ClandestinoCommand
import net.foxsgr.clandestinocustom.plugin.ClandestinoCustom
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.scheduler.BukkitRunnable
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.logging.Level
import kotlin.math.ln
import kotlin.math.pow

class DiskSpaceCommand(plugin: ClandestinoCustom) : ClandestinoCommand(plugin, MODULE_NAME, COMMAND_NAME) {

    companion object {
        const val MODULE_NAME = "disk-space"
        const val COMMAND_NAME = "diskspace"

        private const val SORT_COMMAND = "sort"
        private val ERROR_PREFIX = "${ChatColor.RED} + Error:"

        private fun formatException(e: Exception): String {
            return "$ERROR_PREFIX ${e.javaClass.simpleName}\n${ChatColor.WHITE}${e.message}"
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!hasPermission(sender)) {
            val noPermissionStr = plugin.mainConfig.getI18n("${Config.LANGUAGE_PREFIX}.no-permission")
            sender.sendMessage(noPermissionStr)
            return true
        }

        if (args.size == 1) {
            single(sender, args)
        } else if (args.size == 2 && args[0].equals(SORT_COMMAND, ignoreCase = true)) {
            sort(sender, args[1], Int.MAX_VALUE)
        } else return if (args.size == 3 && args[0].equals(SORT_COMMAND, ignoreCase = true)) {
            sortAmount(sender, args)
        } else {
            false
        }

        return true
    }

    private fun single(sender: CommandSender, args: Array<String>) {
        object : BukkitRunnable() {
            override fun run() {
                try {
                    val path = Paths.get(args[0])
                    val size = size(path)
                    sender.sendMessage("${ChatColor.AQUA}Size: ${ChatColor.GRAY}${humanReadableByteCount(size)}")
                } catch (e: IOException) {
                    plugin.logger.log(Level.WARNING, "Error while trying to find size.", e)
                    sender.sendMessage(formatException(e))
                } catch (e: InvalidPathException) {
                    plugin.logger.log(Level.WARNING, "Error while trying to find size.", e)
                    sender.sendMessage(formatException(e))
                }
            }
        }.runTaskAsynchronously(plugin)
    }

    private fun sort(sender: CommandSender, name: String, amount: Int) {
        try {
            val folder = File(name)
            if (!folder.isDirectory) {
                sender.sendMessage("$ERROR_PREFIX Not a folder.")
                return
            }

            val files = folder.listFiles()
            if (files == null) {
                sender.sendMessage("$ERROR_PREFIX Folder doesn't exist.")
                return
            }

            val sizes = TreeSet<Pair<String, Long>>(compareBy { pair -> -pair.second })
            for (file in files) {
                val size = size(Paths.get(file.toURI()))
                val fileSizePair = Pair(file.name, size)
                sizes.add(fileSizePair)
            }

            val result = StringBuilder()
            var i = 0
            for ((first, second) in sizes) {
                if (i >= amount) {
                    break
                }

                val size = humanReadableByteCount(second)
                result.append(ChatColor.AQUA).append(i + 1).append(". ").append(ChatColor.WHITE)
                        .append(first).append(" - ").append(ChatColor.GRAY).append(size).append('\n')
                i++
            }

            sender.sendMessage(result.toString())
        } catch (e: IOException) {
            plugin.logger.log(Level.WARNING, "Error while trying to sort sizes.", e)
            sender.sendMessage(formatException(e))
        } catch (e: InvalidPathException) {
            plugin.logger.log(Level.WARNING, "Error while trying to sort sizes.", e)
            sender.sendMessage(formatException(e))
        }
    }

    private fun sortAmount(sender: CommandSender, args: Array<String>): Boolean {
        return try {
            val number = args[1].toInt()
            object : BukkitRunnable() {
                override fun run() {
                    sort(sender, args[2], number)
                }
            }.runTaskAsynchronously(plugin)
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    @Throws(IOException::class)
    private fun size(path: Path): Long {
        return when {
            path.toFile().isDirectory -> {
                folderSize(path)
            }
            path.toFile().isFile -> {
                path.toFile().length()
            }
            else -> {
                throw IOException("The file/folder doesn't exist.")
            }
        }
    }

    @Throws(IOException::class)
    private fun folderSize(folder: Path): Long {
        return Files.walk(folder).filter { p: Path -> p.toFile().isFile }
                .mapToLong { p: Path -> p.toFile().length() }
                .sum()
    }

    private fun humanReadableByteCount(bytes: Long): String {
        val unit = 1024
        if (bytes < unit) {
            return "$bytes B"
        }

        val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
        val pre = "${"KMGTPE"[exp - 1]}i"
        return "%.1f %sB".format(bytes / unit.toDouble().pow(exp.toDouble()), pre)
    }
}