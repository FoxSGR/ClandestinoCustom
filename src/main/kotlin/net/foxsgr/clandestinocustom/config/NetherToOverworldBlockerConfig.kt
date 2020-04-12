package net.foxsgr.clandestinocustom.config

import net.foxsgr.clandestinocustom.modules.listener.NetherToOverworldBlocker
import org.bukkit.plugin.java.JavaPlugin

class NetherToOverworldBlockerConfig(plugin: JavaPlugin) : Config(plugin, NetherToOverworldBlocker.MODULE_NAME, createDefaults()) {

    companion object {
        private fun createDefaults(): Map<String, Any> {
            return hashMapOf(
                    "$LANGUAGE_PREFIX.warning" to "&cNão podes voltar criar um portal aqui.",
                    "$LANGUAGE_PREFIX.warning-console" to "&cNão podes voltar criar um portal aqui."
            )
        }
    }
}