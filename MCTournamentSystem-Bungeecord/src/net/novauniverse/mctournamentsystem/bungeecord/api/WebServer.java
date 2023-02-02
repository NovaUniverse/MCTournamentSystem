package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.chat.GetChatLogHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.commentator.CommentatorTPHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.commentator.GetCommentatorGuestKeyHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.game.StartGameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.game.TriggerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.nextmingame.ResetNextMinigameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.nextmingame.SetNextMinigameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.publicapi.PublicStatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send.SendPlayerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send.SendPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.server.GetServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.server.GetServersLogsHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.server.StartServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.server.StopServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.snapshot.ExportSnapshotHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.snapshot.ImportSnapshotHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.staff.GetStaffHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.staff.SetStaffHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.BroadcastHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.ClearPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.PHPMyAdminUrlHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.QuickMessageHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.ResetHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.ShutdownHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.StatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.config.SetScoreboardURLHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.config.SetTournamentNameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.dynamicconfig.ReloadDynamicConfig;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team.ExportTeamDataHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team.UploadTeamHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.user.LoginHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.user.WhoAmIHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.whitelist.AddWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.whitelist.ClearWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.whitelist.RemoveWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.FaviconHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.StaticFileHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.redirect.RedirectToApp;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class WebServer {
	private HttpServer httpServer;

	public WebServer(int port, String appRoot) throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);

		// Redirect
		createContext("/", new RedirectToApp());

		// System
		createContext("/api/system/status", new StatusHandler());
		createContext("/api/system/set_tournament_name", new SetTournamentNameHandler());
		createContext("/api/system/set_scoreboard_url", new SetScoreboardURLHandler());
		createContext("/api/system/broadcast", new BroadcastHandler());
		createContext("/api/system/quick_message", new QuickMessageHandler());
		createContext("/api/system/reset", new ResetHandler());
		createContext("/api/system/clear_players", new ClearPlayersHandler());
		createContext("/api/system/phpmyadmin_url", new PHPMyAdminUrlHandler());
		createContext("/api/system/shutdown", new ShutdownHandler());

		// Dynamic config
		createContext("/api/system/dynamicconfig/reload", new ReloadDynamicConfig());

		// Team
		createContext("/api/team/export_team_data", new ExportTeamDataHandler());
		createContext("/api/team/upload_team", new UploadTeamHandler());

		// Send
		createContext("/api/send/send_player", new SendPlayerHandler());
		createContext("/api/send/send_players", new SendPlayersHandler());

		// Game
		createContext("/api/game/start_game", new StartGameHandler());
		createContext("/api/game/trigger", new TriggerHandler());

		// User
		createContext("/api/user/whoami", new WhoAmIHandler());
		createContext("/api/user/login", new LoginHandler());

		// Staff
		createContext("/api/staff/get_staff", new GetStaffHandler());
		createContext("/api/staff/set_staff", new SetStaffHandler());

		// Whitelist
		createContext("/api/whitelist/add", new AddWhitelistHandler());
		createContext("/api/whitelist/remove", new RemoveWhitelistHandler());
		createContext("/api/whitelist/clear", new ClearWhitelistHandler());

		// Commentator
		createContext("/api/commentator/tp", new CommentatorTPHandler());
		createContext("/api/commentator/get_guest_key", new GetCommentatorGuestKeyHandler());

		// Public
		createContext("/api/public/status", new PublicStatusHandler());

		// Snapshots
		createContext("/api/snapshot/export", new ExportSnapshotHandler());
		createContext("/api/snapshot/import", new ImportSnapshotHandler());

		// Chat
		createContext("/api/chat/log", new GetChatLogHandler());

		// Next minigame
		createContext("/api/next_minigame/set", new SetNextMinigameHandler());
		createContext("/api/next_minigame/reset", new ResetNextMinigameHandler());

		// Servers
		createContext("/api/servers/get_servers", new GetServersHandler());
		createContext("/api/servers/start", new StartServersHandler());
		createContext("/api/servers/stop", new StopServersHandler());
		createContext("/api/servers/logs", new GetServersLogsHandler());

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