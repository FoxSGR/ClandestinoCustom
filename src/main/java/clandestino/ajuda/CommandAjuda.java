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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Comando principal do plugin. Apresenta as ajudas disponíveis
 */
public final class CommandAjuda implements CommandExecutor, TabCompleter {

    /**
     * O plugin em que o comando está registado.
     */
    private final JavaPlugin plugin;

    /**
     * Cria o comando.
     *
     * @param plugin o plugin em que o comando está registado.
     */
    CommandAjuda(JavaPlugin plugin) {
        this.plugin = plugin;
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
        File[] files = plugin.getDataFolder().listFiles();
        List<String> result = new ArrayList<>();
        if (files == null) {
            return result;
        }

        String fileName = FileUtil.withoutExtension(String.join(" ", args)).toLowerCase();
        for (File file : files) {
            String currentFileName = decodeFileName(file).toLowerCase();
            if (currentFileName.contains(fileName)) {
                result.add(currentFileName);
            }
        }

        return result;
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
        File[] files = plugin.getDataFolder().listFiles();
        if (files == null) {
            throw new FileNotFoundException();
        }

        String name = FileUtil.withoutExtension(String.join(" ", args));

        for (File file : files) {
            String fileName = decodeFileName(file);
            if (fileName.equalsIgnoreCase(name)) {
                String content = FileUtil.contentFromFile(file.toURI()).replace("&", "§").replace("\r", "");
                return String.format("§6---\n%s\n§6---", content);
            }
        }

        throw new FileNotFoundException();
    }

    /**
     * Encontra todas as ajudas disponíveis.
     *
     * @return todas as ajudas disponíveis.
     */
    private String ajudas() {
        File[] files = plugin.getDataFolder().listFiles();
        if (files == null) {
            return "";
        }

        StringBuilder ajudas = new StringBuilder(ChatColor.BLUE.toString());
        for (int i = 0; i < files.length; i++) {
            String fileName = decodeFileName(files[i]);
            ajudas.append(fileName);

            if (i != files.length - 1) {
                ajudas.append(ChatColor.GREEN).append(", ").append(ChatColor.BLUE);
            }
        }

        return ajudas.toString();
    }

    private String decodeFileName(File file) {
        String fileName = FileUtil.withoutExtension(file.getName());
        byte[] bytes = fileName.getBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
