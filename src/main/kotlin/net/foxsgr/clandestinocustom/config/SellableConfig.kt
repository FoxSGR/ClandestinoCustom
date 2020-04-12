package net.foxsgr.clandestinocustom.config

import net.foxsgr.clandestinocustom.modules.commands.SellableCommand
import org.bukkit.plugin.java.JavaPlugin

class SellableConfig(plugin: JavaPlugin) : Config(plugin, SellableCommand.MODULE_NAME, createDefaults()) {

    companion object {
        private fun createDefaults(): Map<String, Any> {
            return hashMapOf(
                    "$LANGUAGE_PREFIX.intro" to "&eItems que podes vender usando &b/sell hand &eou &b/sell (nome do item)&e:",
                    "$LANGUAGE_PREFIX.item-price" to "&7- &6{0} &7por &6{1} &7cada (&6{2} &7por stack)",
                    "$LANGUAGE_PREFIX.currency-symbol" to "€",
                    "$LANGUAGE_PREFIX.wrong-syntax" to "&cPara ver quanto vale um item, usa o comando &a/worth &cou &a/worth (item)&c."
            )
        }
    }
}