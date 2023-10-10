package net.novauniverse.mctournamentsystem.bungeecord.maps;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.commons.utils.JSONFileUtils.JSONFileNameFilter.Mode;

public class MapScanner {
	public static final FilenameFilter JSON_FILE_NAME_FILTER = new JSONFileUtils.JSONFileNameFilter(Mode.IGNORE_CASE);

	public static void fixMapUUID() {
		List<String> games = getGameList();
		AtomicInteger modifiedCount = new AtomicInteger(0);
		games.forEach(game -> {
			File mapDataFolder = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + game + File.separator + "Maps");
			if (!mapDataFolder.exists()) {
				Log.warn("TournamentSystem", "Game " + game + " is configured in games.json but the map folder at " + mapDataFolder.getAbsolutePath() + " was not found");
				return;
			}

			List<File> files = Arrays.asList(mapDataFolder.listFiles(JSON_FILE_NAME_FILTER));
			files.forEach(mapFile -> {
				try {
					JSONObject json = JSONFileUtils.readJSONObjectFromFile(mapFile);

					if (!json.has("map_name")) {
						Log.warn("TournamentSystem", "Map at " + mapFile.getAbsolutePath() + " does not contain a map_name value");
						return;
					}

					if (!json.has("uuid")) {
						UUID uuid = UUID.randomUUID();
						Log.info("TournamentSystem", "Adding a random uuid to map" + json.getString("map_name") + " located at " + mapFile.getAbsolutePath() + ". UUID: (" + uuid.toString() + ")");
						json.put("uuid", uuid.toString());
						JSONFileUtils.saveJson(mapFile, json, 4);
						modifiedCount.addAndGet(1);
					}
				} catch (Exception e) {
					Log.error("TournamentSystem", "Failed to read map file at " + mapFile.getAbsolutePath() + ". " + e.getClass().getName() + " " + e.getMessage());
				}
			});
		});
		Log.info("TournamentSystem", "Map uuid scan completed. " + modifiedCount.get() + " maps where modified");
	}

	public static void updateMapDatabase() {
		List<String> games = getGameList();
		List<MapInfo> mapEntries = new ArrayList<>();

		List<UUID> existingMaps = new ArrayList<>();

		AtomicInteger removedCount = new AtomicInteger(0);
		AtomicInteger addedCount = new AtomicInteger(0);

		try {
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement("SELECT uuid FROM map_pool");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				existingMaps.add(UUID.fromString(rs.getString("uuid")));
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "Failed to update map database. Error while reading existing maps. " + e.getClass().getName() + " " + e.getMessage());
			return;
		}

		Log.trace("TournamentSystem", "Initial map db contains " + existingMaps.size() + " entries");

		List<UUID> toBeRemoved = new ArrayList<>(existingMaps);

		games.forEach(game -> {
			File mapDataFolder = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + game + File.separator + "Maps");
			if (!mapDataFolder.exists()) {
				Log.warn("TournamentSystem", "Game " + game + " is configured in games.json but the map folder at " + mapDataFolder.getAbsolutePath() + " was not found");
				return;
			}

			List<File> files = Arrays.asList(mapDataFolder.listFiles(JSON_FILE_NAME_FILTER));
			files.forEach(mapFile -> {
				try {
					JSONObject json = JSONFileUtils.readJSONObjectFromFile(mapFile);

					if (!json.has("map_name")) {
						Log.warn("TournamentSystem", "Map at " + mapFile.getAbsolutePath() + " does not contain a map_name value");
						return;
					}

					String name = json.getString("map_name");

					if (!json.has("uuid")) {
						Log.warn("TournamentSystem", "Map at " + mapFile.getAbsolutePath() + " does not contain a uuid value");
						return;
					}

					UUID uuid = UUID.fromString(json.getString("uuid"));
					toBeRemoved.remove(uuid);

					mapEntries.add(new MapInfo(uuid, game, name));
				} catch (Exception e) {
					Log.error("TournamentSystem", "Failed to read map file at " + mapFile.getAbsolutePath() + ". " + e.getClass().getName() + " " + e.getMessage());
				}
			});
		});

		toBeRemoved.forEach(mapId -> {
			try {
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement("DELETE FROM map_pool WHERE uuid = ?");
				ps.setString(1, mapId.toString());
				ps.executeUpdate();
				ps.close();
				removedCount.addAndGet(1);
				Log.info("TournamentSystem", "Removing old map " + mapId.toString() + " from map databse");
			} catch (Exception e) {
				Log.error("TournamentSystem", "Failed to delete old map " + mapId.toString() + " from map database. " + e.getClass().getName() + " " + e.getMessage());
			}
		});

		mapEntries.forEach(mapInfo -> {
			try {
				if (existingMaps.contains(mapInfo.getUuid())) {
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement("UPDATE map_pool SET game_name = ?, map_name = ? WHERE uuid = ?");
					ps.setString(1, mapInfo.getGame());
					ps.setString(2, mapInfo.getName());
					ps.setString(3, mapInfo.getUuid().toString());
					ps.executeUpdate();
					ps.close();
				} else {
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement("INSERT INTO map_pool (uuid, game_name, map_name) VALUES (?, ?, ?)");
					ps.setString(1, mapInfo.getUuid().toString());
					ps.setString(2, mapInfo.getGame());
					ps.setString(3, mapInfo.getName());
					ps.executeUpdate();
					ps.close();
					addedCount.addAndGet(1);
					Log.info("TournamentSystem", "Added map " + mapInfo.getName() + " of type " + mapInfo.getGame() + " with uuid " + mapInfo.getUuid().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("TournamentSystem", "Failed to add/update map " + mapInfo.getName() + " to the map database. " + e.getClass().getName() + " " + e.getMessage());
			}
		});

		Log.info("TournamentSystem", "Map database updated. " + addedCount.get() + " added. " + removedCount.get() + " removed");
	}

	public static List<String> getGameList() {
		List<String> result = new ArrayList<>();

		File dataFile = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + "games.json");

		if (dataFile.exists()) {
			try {
				JSONArray json = JSONFileUtils.readJSONArrayFromFile(dataFile);
				for (int i = 0; i < json.length(); i++) {
					result.add(json.getString(i));
				}
			} catch (IOException | JSONException e) {
				Log.error("TournamentSystem", "Failed to read json file at " + dataFile.getAbsolutePath() + ". Map pool might not show correctly in web ui");
			}
		} else {
			Log.error("TournamentSystem", "Could not find file " + dataFile.getAbsolutePath() + ". Map pool might not show correctly in web ui");
		}

		return result;
	}

	private static class MapInfo {
		private UUID uuid;
		private String game;
		private String name;

		public MapInfo(UUID uuid, String game, String name) {
			this.uuid = uuid;
			this.game = game;
			this.name = name;
		}

		public UUID getUuid() {
			return uuid;
		}

		public String getName() {
			return name;
		}

		public String getGame() {
			return game;
		}
	}
}