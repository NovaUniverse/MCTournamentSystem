package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.game.StartGameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send.SendPlayerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send.SendPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.BroadcastHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.ClearPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.ResetHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.SetScoreboardURLHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.SetTournamentNameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.StatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team.ExportTeamDataHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team.UpploadTeamHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.user.LoginHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.user.WhoAmIHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.FaviconHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.StaticFileHandler;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class WebServer {
	private HttpServer httpServer;

	public WebServer(int port, String appRoot) throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);

		// System
		createContext("/api/status", new StatusHandler());
		createContext("/api/set_tournament_name", new SetTournamentNameHandler());
		createContext("/api/set_scoreboard_url", new SetScoreboardURLHandler());
		createContext("/api/broadcast", new BroadcastHandler());
		createContext("/api/reset", new ResetHandler());
		createContext("/api/clear_players", new ClearPlayersHandler());

		// Team
		createContext("/api/export_team_data", new ExportTeamDataHandler());
		createContext("/api/uppload_team", new UpploadTeamHandler());

		// Send
		createContext("/api/send_player", new SendPlayerHandler());
		createContext("/api/send_players", new SendPlayersHandler());

		// Game
		createContext("/api/start_game", new StartGameHandler());

		// User
		createContext("/api/whoami", new WhoAmIHandler());
		createContext("/api/login", new LoginHandler());

		// File index
		StaticFileHandler sfh = new StaticFileHandler("/app/", appRoot, "index.html");
		createContext("/app", sfh);

		// Icon
		createContext("/favicon.ico", new FaviconHandler(TournamentSystem.getInstance().getDataFolder().getPath()));

		// Start the server
		httpServer.setExecutor(null);
		httpServer.start();
	}

	private void createContext(String string, HttpHandler httpHandler) {
		Log.info("WebServer", "Creating context: " + string);
		httpServer.createContext(string, httpHandler);
	}

	public void stop() {
		httpServer.stop(10);
	}

	/**
	 * returns the url parameters in a map
	 * 
	 * @param query The query
	 * @return map result
	 */
	public static Map<String, String> queryToMap(String query) {
		try {
			Map<String, String> result = new HashMap<String, String>();
			for (String param : query.split("&")) {
				String pair[] = param.split("=");
				if (pair.length > 1) {
					result.put(pair[0], pair[1]);
				} else {
					result.put(pair[0], "");
				}
			}
			return result;
		} catch (Exception e) {
			return new HashMap<String, String>();
		}
	}
}