package net.foxsgr.clandestinocustom.config

import net.foxsgr.clandestinocustom.modules.commands.HelpsCommand
import org.bukkit.plugin.java.JavaPlugin

class HelpsConfig(plugin: JavaPlugin) : Config(plugin, HelpsCommand.MODULE_NAME, createDefaults()) {

    companion object {
        private fun createDefaults(): Map<String, Any> {
            return hashMapOf(
                    "$LANGUAGE_PREFIX.available-helps" to "&eAjudas disponíveis:",
                    "$LANGUAGE_PREFIX.type-for-help" to "&eEscreve /{0} (uma ajuda disponível)",
                    "$LANGUAGE_PREFIX.reloaded" to "&eAjudas reloaded.",
                    "$LANGUAGE_PREFIX.invalid-help" to "&cAjuda '{0}' inválida.",
                    "$LANGUAGE_PREFIX.no-permission" to "&cNão tens permissão.",
                    "$LANGUAGE_PREFIX.invalid-help-available-helps" to "&eAjudas disponíveis:&3",
                    "$LANGUAGE_PREFIX.around-help" to "&6---",
                    "$LANGUAGE_PREFIX.list-color" to "&3",
                    "$LANGUAGE_PREFIX.list-separator-color" to "&e"
            )
        }
    }
}