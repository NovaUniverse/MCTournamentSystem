package net.novauniverse.mctournamentsystem.spigot.game;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class MapFilter {
	public static void removeDisabledMapsFromGame() {
		GameManager gameManager = ModuleManager.getModule(GameManager.class);

		List<String> removed = new ArrayList<>();

		gameManager.getAllLoadedMaps().forEach((name, map) -> {
			if (!map.getJsonData().has("uuid")) {
				Log.warn("MapFilter", "Map " + name + " has no uuid configured");
				return;
			}

			UUID uuid = UUID.fromString(map.getJsonData().getString("uuid"));

			try {
				String sql = "SELECT enabled FROM map_pool WHERE uuid = ?";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.setString(1, uuid.toString());
				ResultSet rs = ps.executeQuery();

				if (rs.next()) {
					if (!rs.getBoolean("enabled")) {
						removed.add(name);
					}
				} else {
					Log.warn("MapFilter", "Could not find data for map " + name + " (UUID: " + uuid.toString() + ") in the map pool database");
				}

				rs.close();
				ps.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("MapFilter", "Failed to get status for map " + name + ". " + e.getClass().getName() + " " + e.getMessage());
			}
		});

		removed.forEach(name -> {
			Log.info("MapFilter", "Disabling map " + name);
			gameManager.getAllLoadedMaps().remove(name);
		});
		Log.info("MapFilter", removed.size() + " maps removed");
	}
}
