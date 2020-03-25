package clandestino.commands;

import clandestino.plugin.ConfigManager;
import clandestino.plugin.CustomClandestino;
import clandestino.util.FileUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

/**
 * Comando que apresenta as ajudas disponíveis
 */
public final class HelpsCommand implements CommandExecutor, TabCompleter {

    /**
     * O plugin em que o comando está registado.
     */
    private final JavaPlugin plugin;

    /**
     * As ajudas disponíveis e os seus conteudos.
     */
    private final Map<String, String> helps;

    /**
     * A pasta das ajudas.
     */
    private static final String HELPS_FOLDER = "helps";

    /**
     * O sub comando para dar reload.
     */
    private static final String RELOAD_COMMAND = "reload";

    /**
     * Cria o comando.
     *
     * @param plugin o plugin em que o comando está registado.
     */
    public HelpsCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        helps = new HashMap<>();
    }

    /**
     * Executa o comando, mostrando as ajudas disponíveis se não tiverem sido enviados argumentos ou mostrando o
     * conteúdo da ajuda escolhida se tiver sido enviado algum argumento.
     *
     * @param sender  a entidade que executou o comando.
     * @param command o comando.
     * @param label   a alias do comando usado.
     * @param args    os argumentos do comando.
     * @return sempre true.
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            String typeForHelp = getString("type-for-help",
                    '/' + ConfigManager.getInstance().getString(ConfigManager.HELP_PREFIX + "command-name"));
            sender.sendMessage(getString("available-helps") + ' ' + ajudas() + '\n' + typeForHelp);
            return true;
        }

        if (args[0].equalsIgnoreCase(RELOAD_COMMAND)
                && sender.hasPermission(CustomClandestino.PERMISSIONS_PREFIX + RELOAD_COMMAND)) {
            readHelps();
            sender.sendMessage(getString("reloaded"));
            return true;
        }

        String helpName = helpName(args);
        String help = helps.get(helpName);
        if (help == null) {
            sender.sendMessage(getString("invalid-help", helpName) + '\n' + getString("invalid-help-available-helps")
                    + ' ' + ajudas());
        } else {
            sender.sendMessage(help);
        }

        return true;
    }

    @Override
    @SuppressWarnings("squid:S1168")
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        String fileName = FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
        for (String ajuda : helps.keySet()) {
            if (ajuda.contains(fileName)) {
                result.add(ajuda);
            }
        }

        if (result.isEmpty()) {
            return null;
        }

        return result;
    }

    @SuppressWarnings("squid:S3457")
    public void readHelps() {
        File[] files = ajudasFolder().listFiles();
        if (files == null) {
            return;
        }

        helps.clear();
        for (File file : files) {
            String aroundHelp = getString("around-help");
            String content = FileUtil.contentFromFile(file)
                    .replace("&", "§")
                    .replace("\r", "");
            content = String.format("%s\n%s\n%s", aroundHelp, content, aroundHelp);

            String fileName = decodeFileName(file).replace("$", "ç");
            helps.put(fileName, content);
        }
    }

    /**
     * Encontra todas as ajudas disponíveis.
     *
     * @return todas as ajudas disponíveis.
     */
    private String ajudas() {
        List<String> ajudasDisponiveis = new ArrayList<>(helps.keySet());
        Collections.sort(ajudasDisponiveis);

        String listColor = getString("list-color");
        String listSeparatorColor = getString("list-separator-color");
        StringBuilder ajudasBuilder = new StringBuilder(listColor);
        for (int i = 0; i < ajudasDisponiveis.size(); i++) {
            String ajuda = ajudasDisponiveis.get(i);
            ajudasBuilder.append(ajuda);

            if (i != ajudasDisponiveis.size() - 1) {
                ajudasBuilder.append(listSeparatorColor).append(", ").append(listColor);
            }
        }

        return ajudasBuilder.toString();
    }

    private String decodeFileName(File file) {
        String fileName = file.getName().toLowerCase();
        return FileUtil.withoutExtension(fileName);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File ajudasFolder() {
        File folder = new File(plugin.getDataFolder(), HELPS_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    private static String helpName(String[] args) {
        return FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
    }

    private static String getString(String key, String... args) {
        ConfigManager config = ConfigManager.getInstance();
        return config.getColoredString(ConfigManager.HELP_PREFIX + ConfigManager.LANGUAGE_PREFIX + key, args);
    }
}
