package net.foxsgr.clandestinocustom.modules.listener

import net.foxsgr.clandestinocustom.config.NetherToOverworldBlockerConfig
import net.foxsgr.clandestinocustom.plugin.ClandestinoCustom
import net.foxsgr.clandestinocustom.plugin.ClandestinoListener
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.world.PortalCreateEvent

class NetherToOverworldBlocker(plugin: ClandestinoCustom) : ClandestinoListener(plugin, MODULE_NAME, NetherToOverworldBlockerConfig(plugin)) {

    companion object {
        const val MODULE_NAME = "nether-to-overworld-blocker"
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPortalCreation(event: PortalCreateEvent) {
        if (event.world.environment == World.Environment.NETHER && event.reason != PortalCreateEvent.CreateReason.NETHER_PAIR) {
            if (event.entity is Player) {
                val player = event.entity as Player
                player.sendMessage(getI18n("warning"))
            }

            plugin.logger.info(getI18n("warning-console"))
            event.isCancelled = true
        }
    }
}