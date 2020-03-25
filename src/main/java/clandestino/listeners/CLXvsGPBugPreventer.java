package clandestino.listeners;

import clandestino.plugin.ConfigManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CLXvsGPBugPreventer implements Listener {

    private static class BlockBreakInfo {

        private Date date;
        private Location playerLocation;

        BlockBreakInfo(Location playerLocation) {
            this.playerLocation = playerLocation;
            date = new Date();
        }
    }

    private Map<Player, BlockBreakInfo> frozenPlayers;

    private static CLXvsGPBugPreventer instance;

    public CLXvsGPBugPreventer(JavaPlugin plugin) {
        this.frozenPlayers = new ConcurrentHashMap<>();
        instance = this;

        plugin.getLogger().warning("PvP Tag Bug Prevention is being used but it is not ready in this version.");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        if (shouldFreeze(player, location)) {
            freezePlayer(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onOpen(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        BlockData blockData = block.getBlockData();
        if (!(blockData instanceof Openable)) {
            return;
        }

        Player player = event.getPlayer();
        Location location = block.getLocation();
        if (shouldFreeze(player, location)) {
            freezePlayer(player);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        BlockBreakInfo info = frozenPlayers.get(player);
        if (info == null) {
            return;
        }

        applyFreeze(player, info);
    }

    public Set<Player> frozenPlayers() {
        return frozenPlayers.keySet();
    }

    public static CLXvsGPBugPreventer getInstance() {
        return instance;
    }

    private boolean shouldFreeze(Player player, Location location) {
        return false;
        // return CombatUtil.isInCombat(player) && GriefPrevention.instance.allowBuild(player, location) != null;
    }

    private void freezePlayer(Player player) {
        frozenPlayers.put(player, new BlockBreakInfo(player.getLocation()));
    }

    private void applyFreeze(Player player, BlockBreakInfo info) {
        Date date = info.date;
        Date now = new Date();
        long difference = Math.abs(date.getTime() - now.getTime());
        double freezeTime = ConfigManager.getInstance().getDouble("clx-vs-gp.freeze-time");
        if (difference < freezeTime) {
            Vector nullVelocity = new Vector();
            player.setVelocity(nullVelocity);

            Location currentLocation = player.getLocation();
            Location newLocation = new Location(currentLocation.getWorld(), info.playerLocation.getX(),
                    info.playerLocation.getY(), info.playerLocation.getZ(), currentLocation.getYaw(),
                    currentLocation.getPitch());
            player.teleport(newLocation);
        } else {
            frozenPlayers.remove(player);
        }
    }
}
