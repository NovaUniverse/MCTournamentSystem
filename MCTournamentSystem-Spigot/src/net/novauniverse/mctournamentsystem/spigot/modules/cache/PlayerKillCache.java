package net.novauniverse.mctournamentsystem.spigot.modules.cache;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class PlayerKillCache extends NovaModule implements Listener, TSDataCache {
	private static final long REFRESH_TIME = 20 * 20; // 20 seconds

	private static PlayerKillCache instance;

	private HashMap<UUID, Integer> cache;
	private int taskId;

	public static PlayerKillCache getInstance() {
		return instance;
	}

	public PlayerKillCache() {
		super("TournamentSystem.KillCache");
	}

	@Override
	public void onLoad() {
		PlayerKillCache.instance = this;
		this.cache = new HashMap<UUID, Integer>();
		this.taskId = -1;
	}

	@Override
	public void onEnable() throws Exception {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					Bukkit.getServer().getOnlinePlayers().forEach(player -> updateFromDatabase(player));
				}
			}, REFRESH_TIME, REFRESH_TIME);
		}
		clearCache();
	}

	@Override
	public void onDisable() throws Exception {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}

		clearCache();
	}

	public void updateFromDatabase(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "SELECT kills, uuid FROM players WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, player.getUniqueId().toString());

					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						cache.put(player.getUniqueId(), rs.getInt("kills"));

					}

					rs.close();
					ps.close();
				} catch (Exception ee) {
					ee.printStackTrace();
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public Integer getPlayerKills(UUID uuid) {
		if (cache.containsKey(uuid)) {
			return cache.get(uuid);
		}
		return 0;
	}

	@Override
	public void clearCache() {
		Log.trace("Clearing player kill cache");
		cache.clear();
	}

	public void invalidate(Player player) {
		if (cache.containsKey(player.getUniqueId())) {
			cache.remove(player.getUniqueId());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerJoinEvent e) {
		this.invalidate(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		this.invalidate(e.getPlayer());
	}
}