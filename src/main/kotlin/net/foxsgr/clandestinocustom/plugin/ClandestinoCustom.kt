package net.foxsgr.clandestinocustom.plugin

import net.foxsgr.clandestinocustom.config.MainConfig
import net.foxsgr.clandestinocustom.modules.commands.*
import net.foxsgr.clandestinocustom.modules.listener.NetherToOverworldBlocker
import org.bukkit.plugin.java.JavaPlugin

class ClandestinoCustom : JavaPlugin() {

    companion object {
        const val PLUGIN_NAME = "ClandestinoCustom"
        val PERMISSIONS_PREFIX = PLUGIN_NAME.toLowerCase()
    }

    val mainConfig = MainConfig(this)

    override fun onEnable() {
        super.onEnable()

        registerModules(
                CCCommand(this),
                DiskSpaceCommand(this),
                HelpsCommand(this),
                PerksCommand(this),
                SellableCommand(this),

                NetherToOverworldBlocker(this)
        )
    }

    private fun registerModules(vararg modules: ClandestinoModule) {
        for (module in modules) {
            module.registerModule()
        }
    }
}