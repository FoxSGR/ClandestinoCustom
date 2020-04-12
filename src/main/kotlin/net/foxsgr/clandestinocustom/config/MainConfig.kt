package net.foxsgr.clandestinocustom.config

import net.foxsgr.clandestinocustom.modules.commands.*
import net.foxsgr.clandestinocustom.modules.listener.NetherToOverworldBlocker
import org.bukkit.plugin.java.JavaPlugin

class MainConfig(plugin: JavaPlugin) : Config(plugin, "config", createDefaults()) {

    companion object {
        const val ENABLED_MODULES_PREFIX = "enabled-modules"

        private fun createDefaults(): Map<String, Any> {
            return hashMapOf(
                    "$ENABLED_MODULES_PREFIX.${HelpsCommand.MODULE_NAME}" to true,
                    "$ENABLED_MODULES_PREFIX.${SellableCommand.MODULE_NAME}" to true,
                    "$ENABLED_MODULES_PREFIX.${PerksCommand.MODULE_NAME}" to true,
                    "$ENABLED_MODULES_PREFIX.${DiskSpaceCommand.MODULE_NAME}" to true,
                    "$ENABLED_MODULES_PREFIX.${CCCommand.MODULE_NAME}" to true,
                    "$ENABLED_MODULES_PREFIX.${NetherToOverworldBlocker.MODULE_NAME}" to true,
                    "$ENABLED_MODULES_PREFIX.death-message-replacer" to true,

                    "$LANGUAGE_PREFIX.no-permission" to "&cNão tens permissão."
            )
        }
    }
}