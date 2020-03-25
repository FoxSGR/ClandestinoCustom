package clandestino.util;

import org.bukkit.ChatColor;

public final class TextUtil {

    private TextUtil() {

    }

    public static String stripColorAndFormatting(String text) {
        text = translateColoredText(text);
        text = ChatColor.stripColor(text);
        return text.replaceAll("([&§])(r|[k-o])", "");
    }

    public static String translateColoredText(String text) {
        text = text.replace('§', '&');
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static boolean containsFormattingCodes(String text) {
        return contains(text, "&k", "&l", "&m", "&n", "&o", "&n", "§k", "§l", "§m", "§n", "§o", "&r", "§r");
    }

    public static boolean containsColorCodes(String text) {
        text = translateColoredText(text);
        return !text.equals(ChatColor.stripColor(text));
    }

    public static boolean contains(String text, String... strings) {
        for (String string : strings) {
            if (text.contains(string)) {
                return true;
            }
        }

        return false;
    }
}
