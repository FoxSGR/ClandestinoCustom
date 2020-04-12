package net.foxsgr.clandestinocustom.config

import net.foxsgr.clandestinocustom.modules.commands.PerksCommand
import org.bukkit.plugin.java.JavaPlugin

class PerksConfig(plugin: JavaPlugin) : Config(plugin, PerksCommand.MODULE_NAME, createDefaults()) {

    companion object {
        const val RANKS_PREFIX = "ranks"

        private fun createDefaults(): Map<String, Any> {
            return hashMapOf(
                    "$LANGUAGE_PREFIX.reloaded" to "&ePerks reloaded.",
                    "$LANGUAGE_PREFIX.invalid-rank" to "&cEsse rank não existe.",
                    "$LANGUAGE_PREFIX.available-ranks" to "&eRanks disponíveis:",
                    "$LANGUAGE_PREFIX.rank-separator" to "&e, ",
                    "$LANGUAGE_PREFIX.rank-color-in-list" to "&6",
                    "$LANGUAGE_PREFIX.around-help" to "&6---",
                    "$LANGUAGE_PREFIX.type-to-see-advantages" to "&eEscreve &6/{0} &epara ver as vantagens de um rank.",
                    "$LANGUAGE_PREFIX.list-separator-color" to "&e",
                    
                    "$RANKS_PREFIX.vip" to "&eYou can do so much stuff with VIP.\nYou can't even imagine."
            )
        }
    }
}