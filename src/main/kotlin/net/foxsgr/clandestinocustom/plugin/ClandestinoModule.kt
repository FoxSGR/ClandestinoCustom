package net.foxsgr.clandestinocustom.plugin

import net.foxsgr.clandestinocustom.config.Config
import net.foxsgr.clandestinocustom.config.MainConfig
import org.bukkit.command.CommandSender

abstract class ClandestinoModule(protected val plugin: ClandestinoCustom,
                                 protected val config: Config? = null,
                                 protected val name: String) {

    fun registerModule() {
        if (canRegister()) {
            register()
        }
    }

    protected abstract fun register()

    protected fun hasPermission(sender: CommandSender, permission: String? = null): Boolean {
        return if (permission == null) {
            sender.hasPermission("${ClandestinoCustom.PERMISSIONS_PREFIX}.$name")
        } else {
            sender.hasPermission("${ClandestinoCustom.PERMISSIONS_PREFIX}.$name.$permission")
        }
    }

    protected fun getI18n(key: String, vararg args: String): String {
        return config!!.getI18n("${Config.LANGUAGE_PREFIX}.$key", *args)
    }

    private fun canRegister(): Boolean {
        val key = "${MainConfig.ENABLED_MODULES_PREFIX}.$name"
        if (!plugin.mainConfig.hasKey(key)) {
            return true
        }

        return plugin.mainConfig.getBoolean(key)
    }
}