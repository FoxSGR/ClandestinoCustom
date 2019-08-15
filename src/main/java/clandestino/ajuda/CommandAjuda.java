package clandestino.ajuda;

import clandestino.ajuda.util.FileUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Comando principal do plugin. Apresenta as ajudas disponíveis
 */
public final class CommandAjuda implements CommandExecutor, TabCompleter {

    /**
     * O plugin em que o comando está registado.
     */
    private final JavaPlugin plugin;

    private final Map<String, String> ajudas;

    /**
     * Cria o comando.
     *
     * @param plugin o plugin em que o comando está registado.
     */
    CommandAjuda(JavaPlugin plugin) {
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

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("ajuda.reload")) {
            plugin.reloadConfig();
            readConfig();
            sender.sendMessage(ChatColor.AQUA + "Ajudas reloaded.");
            return true;
        }

        try {
            sender.sendMessage(ajuda(args));
        } catch (IOException e) {
            sender.sendMessage(ChatColor.RED + "Ajuda inválida.\n" + ChatColor.GREEN + "Ajudas disponíveis: "
                    + ChatColor.BLUE + ajudas());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> result = new ArrayList<>();
        String fileName = FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
        for (String ajuda : ajudas.keySet()) {
            if (ajuda.contains(fileName)) {
                result.add(ajuda);
            }
        }

        return result;
    }

    @SuppressWarnings("squid:S3457")
    void readConfig() {
        File[] files = plugin.getDataFolder().listFiles();
        if (files == null) {
            return;
        }

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
     * @throws IOException gerada se a ajuda não for encontrada.
     */
    @SuppressWarnings("squid:S3457") // Tem de ser usado \n em vez de %n para mudar de linha corretamente
    private String ajuda(String[] args) throws IOException {
        String name = FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
        String content = ajudas.get(name);
        if (content == null) {
            throw new FileNotFoundException();
        }

        return content;
    }

    /**
     * Encontra todas as ajudas disponíveis.
     *
     * @return todas as ajudas disponíveis.
     */
    private String ajudas() {
        List<String> ajudasDisponiveis = new ArrayList<>(ajudas.keySet());
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
        System.out.printf("%s - ", fileName);
        for (int i = 0; i < fileName.length(); i++) {
            System.out.printf("'%d' ", (int) fileName.charAt(i));
        }

        return FileUtil.withoutExtension(fileName);
    }
}
