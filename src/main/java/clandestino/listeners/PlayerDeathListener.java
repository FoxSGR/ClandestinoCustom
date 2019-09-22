package clandestino.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        String message = e.getDeathMessage();
        if (message == null) {
            return;
        }

        message = replaceName(message, e.getEntity());
        message = replaceName(message, e.getEntity().getKiller());
        e.setDeathMessage(message);

        // TODO: Make better death messages
    }

    private static String replaceName(String message, Player player) {
        if (player != null) {
            return message.replace(player.getName(), player.getDisplayName());
        }

        return message;
    }
}
