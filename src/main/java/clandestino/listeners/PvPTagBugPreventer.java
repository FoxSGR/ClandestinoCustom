package clandestino.listeners;

import com.SirBlobman.combatlogx.utility.CombatUtil;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
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
import org.bukkit.util.Vector;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PvPTagBugPreventer implements Listener {

    private static class BlockBreakInfo {

        private Date date;
        private Location playerLocation;

        BlockBreakInfo(Location playerLocation) {
            this.playerLocation = playerLocation;
            date = new Date();
        }
    }

    private Map<Player, BlockBreakInfo> frozenPlayers;

    private static final long BLOCKED_TIME = 500; // milliseconds

    public PvPTagBugPreventer() {
        this.frozenPlayers = new ConcurrentHashMap<>();
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

    private boolean shouldFreeze(Player player, Location location) {
        return CombatUtil.isInCombat(player) && GriefPrevention.instance.allowBuild(player, location) != null;
    }

    private void freezePlayer(Player player) {
        frozenPlayers.put(player, new BlockBreakInfo(player.getLocation()));
    }

    private void applyFreeze(Player player, BlockBreakInfo info) {
        Date date = info.date;
        Date now = new Date();
        long difference = Math.abs(date.getTime() - now.getTime());
        if (difference < BLOCKED_TIME) {
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
