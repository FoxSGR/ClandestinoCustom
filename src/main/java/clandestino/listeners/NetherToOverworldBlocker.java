package clandestino.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class NetherToOverworldBlocker implements Listener {

    private JavaPlugin plugin;

    public NetherToOverworldBlocker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unused")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNetherPortalCreation(PortalCreateEvent event) {
        World world = event.getWorld();
        if (world.getName().endsWith("_nether") && event.getReason() != PortalCreateEvent.CreateReason.NETHER_PAIR) {
            plugin.getLogger().info("Cancelada criação de um portal.");
            event.setCancelled(true);
        }
    }
}
