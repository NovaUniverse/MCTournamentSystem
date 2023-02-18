package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.chat.GetChatLogHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator.CommentatorTPHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator.GetCommentatorGuestKeyHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game.StartGameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game.TriggerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.nextmingame.ResetNextMinigameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.nextmingame.SetNextMinigameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.publicapi.PublicStatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send.SendPlayerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send.SendPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.GetServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.GetServersLogSessionIDHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.GetServersLogsHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.SendServerCommandHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.StartServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.StopServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot.ExportSnapshotHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot.ImportSnapshotHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.staff.GetStaffHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.staff.SetStaffHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.BroadcastHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.ClearPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.GetCustomThemesHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.PHPMyAdminUrlHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.QuickMessageHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.ResetHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.ShutdownHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.StatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config.SetMOTDHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config.SetScoreboardURLHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config.SetTournamentNameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.dynamicconfig.ReloadDynamicConfig;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team.ExportTeamDataHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team.UploadTeamHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user.LoginHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user.WhoAmIHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist.AddWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist.ClearWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist.RemoveWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.FaviconHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.StaticFileHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.redirect.FileNotFoundHandler;
import net.zeeraa.novacore.commons.log.Log;

// If you get warnings here in eclipse follow this guide https://stackoverflow.com/a/25945740
public class WebServer {
	private HttpServer httpServer;
	private boolean hasShutDown;

	public WebServer(int port, File appRoot) throws IOException {
		httpServer = HttpServer.create(new InetSocketAddress(port), 0);
		hasShutDown = false;

		// Redirect
		createContext("/", new FileNotFoundHandler(appRoot));

		// System
		createContext("/api/v1/system/status", new StatusHandler());
		createContext("/api/v1/system/broadcast", new BroadcastHandler());
		createContext("/api/v1/system/quick_message", new QuickMessageHandler());
		createContext("/api/v1/system/reset", new ResetHandler());
		createContext("/api/v1/system/clear_players", new ClearPlayersHandler());
		createContext("/api/v1/system/phpmyadmin_url", new PHPMyAdminUrlHandler());
		createContext("/api/v1/system/custom_themes", new GetCustomThemesHandler());
		createContext("/api/v1/system/shutdown", new ShutdownHandler());

		createContext("/api/v1/system/settings/set_tournament_name", new SetTournamentNameHandler());
		createContext("/api/v1/system/settings/set_scoreboard_url", new SetScoreboardURLHandler());
		createContext("/api/v1/system/settings/set_motd", new SetMOTDHandler());

		// Dynamic config
		createContext("/api/v1/system/dynamicconfig/reload", new ReloadDynamicConfig());

		// Team
		createContext("/api/v1/team/export_team_data", new ExportTeamDataHandler());
		createContext("/api/v1/team/upload_team", new UploadTeamHandler());

		// Send
		createContext("/api/v1/send/send_player", new SendPlayerHandler());
		createContext("/api/v1/send/send_players", new SendPlayersHandler());

		// Game
		createContext("/api/v1/game/start_game", new StartGameHandler());
		createContext("/api/v1/game/trigger", new TriggerHandler());

		// User
		createContext("/api/v1/user/whoami", new WhoAmIHandler());
		createContext("/api/v1/user/login", new LoginHandler());

		// Staff
		createContext("/api/v1/staff/get_staff", new GetStaffHandler());
		createContext("/api/v1/staff/set_staff", new SetStaffHandler());

		// Whitelist
		createContext("/api/v1/whitelist/add", new AddWhitelistHandler());
		createContext("/api/v1/whitelist/remove", new RemoveWhitelistHandler());
		createContext("/api/v1/whitelist/clear", new ClearWhitelistHandler());

		// Commentator
		createContext("/api/v1/commentator/tp", new CommentatorTPHandler());
		createContext("/api/v1/commentator/get_guest_key", new GetCommentatorGuestKeyHandler());

		// Public
		createContext("/api/v1/public/status", new PublicStatusHandler());

		// Snapshots
		createContext("/api/v1/snapshot/export", new ExportSnapshotHandler());
		createContext("/api/v1/snapshot/import", new ImportSnapshotHandler());

		// Chat
		createContext("/api/v1/chat/log", new GetChatLogHandler());

		// Next minigame
		createContext("/api/v1/next_minigame/set", new SetNextMinigameHandler());
		createContext("/api/v1/next_minigame/reset", new ResetNextMinigameHandler());

		// Servers
		createContext("/api/v1/servers/get_servers", new GetServersHandler());
		createContext("/api/v1/servers/start", new StartServersHandler());
		createContext("/api/v1/servers/stop", new StopServersHandler());
		createContext("/api/v1/servers/logs", new GetServersLogsHandler());
		createContext("/api/v1/servers/log_session_id", new GetServersLogSessionIDHandler());
		createContext("/api/v1/servers/run_command", new SendServerCommandHandler());

		// File index
		StaticFileHandler sfh = new StaticFileHandler("/app/", appRoot.getAbsolutePath(), "index.html");
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

	public boolean hasShutDown() {
		return hasShutDown;
	}

	public boolean kill() {
		if (hasShutDown) {
			return false;
		}
		httpServer.stop(0);
		hasShutDown = true;
		return true;
	}

	public boolean stop() {
		if (hasShutDown) {
			return false;
		}
		httpServer.stop(10);
		hasShutDown = true;
		return true;
	}

	public HttpServer getHttpServer() {
		return httpServer;
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