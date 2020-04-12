package net.foxsgr.clandestinocustom.util

import org.bukkit.ChatColor

fun translateColoredText(text: String): String {
    val newText = text.replace('§', '&')
    return ChatColor.translateAlternateColorCodes('&', newText)
}
