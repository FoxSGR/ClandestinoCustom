package net.foxsgr.clandestinocustom.plugin

import net.foxsgr.clandestinocustom.config.Config
import org.bukkit.event.Listener

abstract class ClandestinoListener(plugin: ClandestinoCustom,
                                   name: String,
                                   config: Config? = null) : ClandestinoModule(plugin, config, name), Listener {

    override fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }
}