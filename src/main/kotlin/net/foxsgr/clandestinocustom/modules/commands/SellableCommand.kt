package net.foxsgr.clandestinocustom.modules.commands

import net.foxsgr.clandestinocustom.config.SellableConfig
import net.foxsgr.clandestinocustom.plugin.ClandestinoCommand
import net.foxsgr.clandestinocustom.plugin.ClandestinoCustom
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.logging.Level

class SellableCommand(plugin: ClandestinoCustom) : ClandestinoCommand(plugin, MODULE_NAME, MODULE_NAME, SellableConfig(plugin)) {

    companion object {
        const val MODULE_NAME = "sellable"

        private const val ESSENTIALS_FOLDER = "Essentials"
        private const val WORTH_FILE = "worth.yml"
        private const val WORTH_SECTION = "worth"
    }

    private var values = load()
    private var checksum = fileChecksum()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isNotEmpty()) {
            sender.sendMessage(getI18n("wrong-syntax"))
            return true
        }

        val currentChecksum = fileChecksum()
        if (!currentChecksum.contentEquals(checksum)) {
            plugin.logger.info("File change detected. Reloading.")
            values = load()
            checksum = currentChecksum
        }

        sender.sendMessage(values)
        return true
    }

    private fun load(): String {
        val worthSection = loadFileConfiguration(createFile())
        val keys = worthSection.getKeys(false)

        val valuesBuilder = StringBuilder()
        valuesBuilder.append(getI18n("intro")).append('\n')

        for (key in keys) {
            val value = worthSection.getDouble(key)
            val stackValue = value * 64

            val strValue = "%.2f%s".format(value, getI18n("currency-symbol"))
            val strStackValue = "%.2f%s".format(stackValue, getI18n("currency-symbol"))

            val line = getI18n("item-price", key, strValue, strStackValue)
            valuesBuilder.append(line).append('\n')
        }

        return valuesBuilder.toString()
    }

    private fun loadFileConfiguration(file: File): ConfigurationSection {
        val fileConfiguration = YamlConfiguration()
        return try {
            fileConfiguration.load(file)
            fileConfiguration.getConfigurationSection(WORTH_SECTION)
                    ?: throw IllegalStateException("Could not find the $WORTH_SECTION section in $WORTH_FILE")
        } catch (e: IOException) {
            throw IllegalStateException("Could not load $WORTH_FILE", e)
        } catch (e: InvalidConfigurationException) {
            throw IllegalStateException("Could not load $WORTH_FILE", e)
        }
    }

    private fun createFile(): File {
        var file = plugin.dataFolder.parentFile
        file = File(file, ESSENTIALS_FOLDER)
        return File(file, WORTH_FILE)
    }

    private fun fileChecksum(): ByteArray {
        var md = try {
            MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            throw IllegalStateException(e)
        }

        val file = createFile()
        try {
            FileInputStream(file).use { inputStream ->
                DigestInputStream(inputStream, md).use { digestInputStream ->
                    while (digestInputStream.read() != -1) { // Clear the data
                    }

                    md = digestInputStream.messageDigest
                    return md.digest()
                }
            }
        } catch (e: FileNotFoundException) {
            plugin.logger.log(Level.WARNING, "Could not find $ESSENTIALS_FOLDER $WORTH_FILE file.")
            return byteArrayOf()
        } catch (e: IOException) {
            plugin.logger.warning("Could not calculate the checksum of $WORTH_FILE")
            return byteArrayOf()
        }
    }
}