package net.novauniverse.mctournamentsystem.spigot.game;

import org.json.JSONArray;
import org.json.JSONObject;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;

public class GameSummary {
	public static JSONObject getGameSummaryAsJSON() {
		JSONObject data = new JSONObject();
		data.put("name", GameManager.getInstance().getActiveGame().getName());
		data.put("display_name", GameManager.getInstance().getActiveGame().getDisplayNameFromGameManager());
		data.put("game_class", GameManager.getInstance().getActiveGame().getClass().getName());
		if (GameManager.getInstance().getActiveGame() instanceof MapGame) {
			data.put("is_map_game", true);
			MapGame game = (MapGame) GameManager.getInstance().getActiveGame();
			JSONObject mapData = new JSONObject();
			mapData.put("name", game.getActiveMap().getAbstractMapData().getMapName());
			mapData.put("display_name", game.getActiveMap().getAbstractMapData().getDisplayName());
			data.put("map", mapData);
		}
		JSONArray players = new JSONArray();
		GameManager.getInstance().getActiveGame().getPlayers().forEach(p -> players.put(p.toString()));
		data.put("players", players);
		data.put("is_map_game", false);
		return data;
	}
}