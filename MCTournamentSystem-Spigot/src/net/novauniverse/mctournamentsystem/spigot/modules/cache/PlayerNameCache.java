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

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class PlayerNameCache extends NovaModule implements Listener, TSDataCache {
	private static PlayerNameCache instance;

	private HashMap<String, String> cache;
	private int taskId;

	public static PlayerNameCache getInstance() {
		return instance;
	}

	public PlayerNameCache() {
		super("TournamentSystem.PlayerNameCache");
	}

	@Override
	public void onLoad() {
		PlayerNameCache.instance = this;
		this.cache = new HashMap<String, String>();
		this.taskId = -1;
	}

	@Override
	public void onEnable() throws Exception {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					updateCache();
				}
			}, 12000L, 12000L);
		}
		updateCache();
	}

	@Override
	public void onDisable() throws Exception {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}

		clearCache();
	}

	public String getPlayerName(UUID uuid) {
		String name = null;
		Player player = Bukkit.getServer().getPlayer(uuid);
		if (player != null) {
			if (player.isOnline()) {
				name = player.getName();
				cache.put(uuid.toString(), name);
				return name;
			}
		}

		if (cache.containsKey(uuid.toString())) {
			name = cache.get(uuid.toString());
		} else {
			Log.trace("Fetching player name from database");
			try {
				String sql = "SELECT username FROM players WHERE uuid = ?";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, uuid.toString());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					name = rs.getString("username");
					cache.put(uuid.toString(), name);
				}

				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return name;
	}

	public void updateCache() {
		Log.trace("Updating player name cache");
		cache.clear();
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			cache.put(player.getUniqueId().toString(), player.getName());
		}
	}

	@Override
	public void clearCache() {
		Log.trace("Clearing player name cache");
		cache.clear();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		Log.trace("Caching player name for " + player.getName());
		cache.put(player.getUniqueId().toString(), player.getName());
	}
}