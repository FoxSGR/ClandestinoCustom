package clandestino.commands;

import clandestino.CustomClandestino;
import clandestino.util.FileUtil;
import org.bukkit.ChatColor;
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
public final class AjudaCommand implements CommandExecutor, TabCompleter {

    /**
     * O plugin em que o comando está registado.
     */
    private final JavaPlugin plugin;

    /**
     * As ajudas disponíveis e os seus conteudos.
     */
    private final Map<String, String> ajudas;

    /**
     * A pasta das ajudas.
     */
    private static final String AJUDAS_FOLDER = "ajudas";

    /**
     * O sub comando para dar reload.
     */
    private static final String RELOAD_COMMAND = "reload";

    /**
     * Cria o comando.
     *
     * @param plugin o plugin em que o comando está registado.
     */
    public AjudaCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        ajudas = new HashMap<>();
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
            sender.sendMessage(ChatColor.GREEN + "Ajudas disponíveis: " + ajudas() + ChatColor.GREEN + "\nEscreve "
                    + ChatColor.GOLD + "/ajuda (uma ajuda disponível)");
            return true;
        }

        if (args[0].equalsIgnoreCase(RELOAD_COMMAND)
                && sender.hasPermission(CustomClandestino.PERMISSIONS_PREFIX + RELOAD_COMMAND)) {
            readAjudas();
            sender.sendMessage(ChatColor.AQUA + "Ajudas reloaded.");
            return true;
        }

        String ajuda = ajuda(args);
        if (ajuda == null) {
            sender.sendMessage(ChatColor.RED + "Ajuda inválida.\n" + ChatColor.GREEN + "Ajudas disponíveis: "
                    + ChatColor.BLUE + ajudas());
        } else {
            sender.sendMessage(ajuda(args));
        }

        return true;
    }

    @Override
    @SuppressWarnings("squid:S1168")
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        String fileName = FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
        for (String ajuda : ajudas.keySet()) {
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
    public void readAjudas() {
        File[] files = ajudasFolder().listFiles();
        if (files == null) {
            return;
        }

        ajudas.clear();
        for (File file : files) {
            String content = FileUtil.contentFromFile(file)
                    .replace("&", "§")
                    .replace("\r", "");
            content = String.format("§6---\n%s\n§6---", content);

            String fileName = decodeFileName(file)
                    .replace("$", "ç");
            ajudas.put(fileName, content);
        }
    }

    /**
     * Encontra o conteúdo de uma ajuda.
     *
     * @param args os argumentos usados no comando.
     * @return a ajuda encontrada.
     */
    @SuppressWarnings("squid:S3457") // Tem de ser usado \n em vez de %n para mudar de linha corretamente
    private String ajuda(String[] args) {
        String name = FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
        return ajudas.get(name);
    }

    /**
     * Encontra todas as ajudas disponíveis.
     *
     * @return todas as ajudas disponíveis.
     */
    private String ajudas() {
        List<String> ajudasDisponiveis = new ArrayList<>(ajudas.keySet());
        Collections.sort(ajudasDisponiveis);

        StringBuilder ajudasBuilder = new StringBuilder(ChatColor.BLUE.toString());
        for (int i = 0; i < ajudasDisponiveis.size(); i++) {
            String ajuda = ajudasDisponiveis.get(i);
            ajudasBuilder.append(ajuda);

            if (i != ajudasDisponiveis.size() - 1) {
                ajudasBuilder.append(ChatColor.GREEN).append(", ").append(ChatColor.BLUE);
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
        File folder = new File(plugin.getDataFolder(), AJUDAS_FOLDER);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }
}
