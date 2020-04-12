package net.foxsgr.clandestinocustom.config

import net.foxsgr.clandestinocustom.util.translateColoredText
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import org.jetbrains.annotations.Nullable
import java.io.File
import java.io.IOException
import java.util.logging.Level

open class Config(private val plugin: JavaPlugin,
                  private val fileName: String) {

    private val configuration = YamlConfiguration()

    companion object {
        const val LANGUAGE_PREFIX = "language"
        private const val EXTENSION = ".yml"
    }

    constructor(plugin: JavaPlugin,
                fileName: String,
                defaults: Map<String, Any>) : this(plugin, fileName) {
        configuration.addDefaults(defaults)
        configuration.options().copyDefaults(true)
        load()
    }

    fun hasKey(key: String): Boolean {
        return configuration.contains(key, false)
    }

    fun getInt(key: String): Int {
        return configuration.getInt(key)
    }

    fun getDouble(key: String): Double {
        return configuration.getDouble(key)
    }

    fun getBoolean(key: String): Boolean {
        return configuration.getBoolean(key)
    }

    fun getString(key: String): @Nullable String? {
        return configuration.getString(key)?.let { translateColoredText(it) }
    }

    fun getI18n(key: String, vararg args: String): String {
        var value: String = configuration.getString(key) ?: return key

        for (i in args.indices) {
            value = value.replace("{$i}", args[i])
        }

        return translateColoredText(value)
    }

    fun getKeys(path: String?): Set<String> {
        var section: ConfigurationSection = configuration
        if (path != null) {
            section = configuration.getConfigurationSection(path)!!
        }

        return section.getKeys(false)
    }

    fun load() {
        if (createFile()) {
            return
        }

        configuration.load(getFile())
    }

    private fun createFile(): Boolean {
        val file = getFile()
        if (file.exists()) {
            return false
        }

        return try {
            configuration.save(file)
            true
        } catch (e: IOException) {
            plugin.logger.log(Level.WARNING, "Could not save '$fileName' config file.", e)
            false
        }
    }

    private fun getFile(): File {
        plugin.dataFolder.mkdirs()
        return File(plugin.dataFolder, "$fileName$EXTENSION")
    }
}