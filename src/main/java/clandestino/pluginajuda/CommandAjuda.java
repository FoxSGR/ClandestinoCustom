package clandestino.pluginajuda;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Comando principal do plugin. Apresenta as ajudas disponíveis
 */
public final class CommandAjuda implements CommandExecutor {

    /**
     * O comando.
     */
    static final String COMMAND_NAME = "ajuda";

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
     * @param sender
     * @param command
     * @param label
     * @param args
     * @return
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

    private String ajuda(String[] args) throws IOException {
        String name = String.join(" ", args);
        name = name.toLowerCase();

        try (Stream<Path> walk = Files.walk(Paths.get(plugin.getDataFolder().toURI()))) {
            List<Path> paths = walk.collect(Collectors.toList());

            for (Path path : paths) {
                String fileName = path.getFileName().toString().toLowerCase();
                if (path.toFile().isFile() && fileName.contains(name)) {
                    return contentFromFile(path.toUri()).replaceAll("&", "§");
                }
            }

            throw new FileNotFoundException();
        }
    }

    private String ajudas() {
        try (Stream<Path> walk = Files.walk(Paths.get(plugin.getDataFolder().toURI()))) {
            List<Path> files = walk.filter(Files::isRegularFile).map(Path::getFileName).collect(Collectors.toList());

            StringBuilder ajudas = new StringBuilder(ChatColor.BLUE.toString());
            for (int i = 0; i < files.size(); i++) {
                String fileName = files.get(i).toString();
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                ajudas.append(fileName);

                if (i != files.size() - 1) {
                    ajudas.append(", ");
                }
            }

            return ajudas.toString();
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, e.getMessage(), e);
            return "";
        }
    }

    private static String contentFromFile(URI uri) throws IOException {
        return new String(Files.readAllBytes(Paths.get(uri)), StandardCharsets.UTF_8);
    }
}
