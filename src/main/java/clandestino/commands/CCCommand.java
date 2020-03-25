package clandestino.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CCCommand implements CommandExecutor {

    private static final String AUTO_RECO_CHECK_COMMAND = "autorecocheck";
    private static final String AUTO_RECO_CHECK_PERMISSION = "clandestinocustom.cc.autorecocheck";

    @Override
    @SuppressWarnings("squid:S3516")
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return true;
        }

        if (args[0].equalsIgnoreCase(AUTO_RECO_CHECK_COMMAND)) {
            sender.sendMessage(AUTO_RECO_CHECK_COMMAND + sender.hasPermission(AUTO_RECO_CHECK_PERMISSION));
        }

        return true;
    }
}
