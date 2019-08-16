package clandestino.commands;

import clandestino.util.FileUtil;
import clandestino.util.WebUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class VantagensCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final Map<String, String> rankInfos;

    private static final String CONFIG_FILE = "vantagens.yml";

    public VantagensCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        rankInfos = new HashMap<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sendAvailable(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            readConfig();
            sender.sendMessage(ChatColor.AQUA + "Vantagens reloaded.");
            return true;
        }

        String info = rankInfos.get(args[0].toLowerCase());
        if (info == null) {
            sender.sendMessage(ChatColor.RED + "Esse rank não existe.");
            sendAvailable(sender);
        } else {
            sender.sendMessage(info);
        }

        return true;
    }

    public void readConfig() {
        FileConfiguration fileConfiguration = createFileConfiguration();

        Set<String> keys = fileConfiguration.getKeys(false);
        for (String key : keys) {
            String url = fileConfiguration.getString(key);
            String html = contentFromPage(url);
            rankInfos.put(key.toLowerCase(), html);
        }
    }

    @Override
    @SuppressWarnings("squid:S1168")
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return new ArrayList<>();
        }

        List<String> result = rankInfos.keySet().stream()
                .filter(key -> key.equalsIgnoreCase(args[0]))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private FileConfiguration createFileConfiguration() {
        FileConfiguration fileConfiguration = new YamlConfiguration();
        fileConfiguration.addDefault("vip", "http://vip.com/");
        fileConfiguration.addDefault("vip+", "http://vip+.com/");
        fileConfiguration.addDefault("mvp", "http://mvp.com/");
        fileConfiguration.options().copyDefaults(true);

        File file = new File(plugin.getDataFolder(), CONFIG_FILE);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            FileUtil.saveConfig(fileConfiguration, file);
        } else {
            FileUtil.loadConfig(fileConfiguration, file);
        }

        return fileConfiguration;
    }

    private void sendAvailable(CommandSender sender) {
        final ChatColor mainColor = ChatColor.YELLOW;
        final ChatColor secColor = ChatColor.GOLD;

        StringBuilder result = new StringBuilder()
                .append(mainColor)
                .append("Ranks disponíveis: ");

        List<String> ranks = new ArrayList<>(rankInfos.keySet());
        Collections.sort(ranks);
        for (int i = 0; i < ranks.size(); i++) {
            String rank = ranks.get(i).toUpperCase();
            result.append(secColor).append(rank);

            if (i != ranks.size() - 1) {
                result.append(mainColor).append(", ");
            }
        }

        result.append('\n').append(mainColor).append("Escreve ").append(secColor).append("/vantagens (rank)")
                .append(mainColor).append(" para ver as vantagens de um rank.");
        sender.sendMessage(result.toString());
    }

    private String contentFromPage(String url) {
        final String contentBegining = "<div id=\"content\">";
        final String contentEnd = "</div>";

        try {
            String content = WebUtil.htmlFromPage(url);
            int beginingIndex = content.indexOf(contentBegining);
            int endIndex = content.indexOf(contentEnd);
            if (beginingIndex == -1 || endIndex == -1) {
                return "";
            }

            ChatColor mainColor = ChatColor.YELLOW;
            String boldFormatting = ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
            content = mainColor + content.substring(beginingIndex + contentBegining.length(), endIndex).trim()
                    .replace("<h2>", " \n" + boldFormatting)
                    .replace("</h2>", "\n \n " + mainColor)
                    .replace("<strong>", boldFormatting)
                    .replace("<li>", "- ")
                    .replace("</li>", "\n")
                    .replace("<lu>", "")
                    .replace("<ul>", "\n")
                    .replace("<p>", "")
                    .replaceAll("</[^ ]*>", mainColor.toString())
                    .replace("\t", "")
                    .replaceAll(" +", " ")
                    .replace("&aacute;", "á")
                    .replace("&Ccedil;", "Ç")
                    .replace("&euro;", "€")
                    .replace("&ccedil;", "ç")
                    .replace("&atilde;", "ã")
                    .replace("&eacute;", "é")
                    .replace("&oacute;", "ó")
                    .replace("&atilde;", "ã")
                    .replace("&uacute;", "ú");

            return content;
        } catch (Exception e) {
            plugin.getLogger().log(Level.WARNING, "Could not read the content of the page " + url, e);
            return "";
        }
    }
}
