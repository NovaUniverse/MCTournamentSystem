package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.File;
import java.io.IOException;
import java.net.BindException;

import net.novauniverse.apilib.http.HTTPServer;
import net.novauniverse.apilib.http.enums.ExceptionMode;
import net.novauniverse.apilib.http.enums.StandardResponseType;
import net.novauniverse.apilib.http.middleware.middlewares.CorsAnywhereMiddleware;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKeyAuthProvider;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuthProvider;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.internal.InternalAuthProvider;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.GetServiceProvidersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.chat.GetChatLogHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator.CommentatorTPHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator.GetCommentatorGuestKeyHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game.GetTriggersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game.StartGameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game.TriggerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.nextmingame.NextMinigameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.publicapi.PublicStatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send.SendPlayerHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send.SendPlayersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.GetServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.GetServersLogSessionIDHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.GetServersLogsHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.SendServerCommandHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.StartServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server.StopServersHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.skinrestorer.GetSkinrestorerSkinHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot.ExportSnapshotHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot.ImportSnapshotHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.staff.StaffHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.BroadcastHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.ModeHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.QuickMessageHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.SecurityCheckHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.ShutdownHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.StatusHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config.MOTDHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config.ScoreboardURLHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config.TournamentNameHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.dynamicconfig.ReloadDynamicConfig;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.reset.ResetHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.web.GetCustomThemesHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.web.PHPMyAdminUrlHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team.ExportTeamDataHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team.UploadTeamHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user.LoginHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user.WhoAmIHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.util.OfflineUsernameToUUIDHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist.ClearWhitelistHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist.ManageWhitelistUserHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files.FaviconHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.handlers.redirect.FileNotFoundHandler;
import net.novauniverse.mctournamentsystem.bungeecord.api.internal.ManagedServerStateReportingEndpoint;

public class WebServer {
	private int port;
	private HTTPServer server;

	public WebServer(int port, File appRoot) throws BindException, IOException {
		this.port = port;
		this.server = new HTTPServer(port);

		server.addMiddleware(new CorsAnywhereMiddleware());
		server.addAuthenticationProvider(new InternalAuthProvider());
		server.addAuthenticationProvider(new APIKeyAuthProvider());
		server.addAuthenticationProvider(new CommentatorAuthProvider());

		server.setStandardResponseType(StandardResponseType.JSON);
		server.setExceptionMode(ExceptionMode.MESSAGE);

		// Redirect and favicon
		server.getHttpServer().createContext("/", new FileNotFoundHandler(appRoot));
		server.getHttpServer().createContext("/favicon.ico", new FaviconHandler(TournamentSystem.getInstance().getDataFolder().getPath()));

		// Service providers
		server.addEndpoint("/api/v1/service_providers", new GetServiceProvidersHandler());

		// System
		server.addEndpoint("/api/v1/system/status", new StatusHandler());
		server.addEndpoint("/api/v1/system/broadcast", new BroadcastHandler());
		server.addEndpoint("/api/v1/system/quick_message", new QuickMessageHandler());
		server.addEndpoint("/api/v1/system/shutdown", new ShutdownHandler());
		server.addEndpoint("/api/v1/system/security_check", new SecurityCheckHandler());
		server.addEndpoint("/api/v1/system/mode", new ModeHandler());

		server.addEndpoint("/api/v1/system/reset", new ResetHandler());

		server.addEndpoint("/api/v1/system/settings/tournament_name", new TournamentNameHandler());
		server.addEndpoint("/api/v1/system/settings/scoreboard_url", new ScoreboardURLHandler());
		server.addEndpoint("/api/v1/system/settings/motd", new MOTDHandler());

		server.addEndpoint("/api/v1/system/web/phpmyadmin_url", new PHPMyAdminUrlHandler());
		server.addEndpoint("/api/v1/system/web/custom_themes", new GetCustomThemesHandler());

		// Dynamic config
		server.addEndpoint("/api/v1/system/dynamicconfig/reload", new ReloadDynamicConfig());

		// Team
		server.addEndpoint("/api/v1/team/export_team_data", new ExportTeamDataHandler());
		server.addEndpoint("/api/v1/team/upload_team", new UploadTeamHandler());

		// Send
		server.addEndpoint("/api/v1/send/send_player", new SendPlayerHandler());
		server.addEndpoint("/api/v1/send/send_players", new SendPlayersHandler());

		// Game
		server.addEndpoint("/api/v1/game/start_game", new StartGameHandler());
		server.addEndpoint("/api/v1/game/trigger", new TriggerHandler());
		server.addEndpoint("/api/v1/game/triggers", new GetTriggersHandler());

		// User
		server.addEndpoint("/api/v1/user/whoami", new WhoAmIHandler());
		server.addEndpoint("/api/v1/user/login", new LoginHandler());

		// Staff
		server.addEndpoint("/api/v1/staff", new StaffHandler());

		// Whitelist
		server.addEndpoint("/api/v1/whitelist/users", new ManageWhitelistUserHandler());
		server.addEndpoint("/api/v1/whitelist/clear", new ClearWhitelistHandler());

		// Commentator
		server.addEndpoint("/api/v1/commentator/tp", new CommentatorTPHandler());
		server.addEndpoint("/api/v1/commentator/get_guest_key", new GetCommentatorGuestKeyHandler());

		// Public
		server.addEndpoint("/api/v1/utils/offline_username_to_uuid", new OfflineUsernameToUUIDHandler());

		// Public
		server.addEndpoint("/api/v1/public/status", new PublicStatusHandler());

		// Snapshots
		server.addEndpoint("/api/v1/snapshot/export", new ExportSnapshotHandler());
		server.addEndpoint("/api/v1/snapshot/import", new ImportSnapshotHandler());
		
		// Chat
		server.addEndpoint("/api/v1/chat/log", new GetChatLogHandler());

		// Next minigame
		server.addEndpoint("/api/v1/next_minigame", new NextMinigameHandler());

		// Servers
		server.addEndpoint("/api/v1/servers/get_servers", new GetServersHandler());
		server.addEndpoint("/api/v1/servers/start", new StartServersHandler());
		server.addEndpoint("/api/v1/servers/stop", new StopServersHandler());
		server.addEndpoint("/api/v1/servers/logs", new GetServersLogsHandler());
		server.addEndpoint("/api/v1/servers/log_session_id", new GetServersLogSessionIDHandler());
		server.addEndpoint("/api/v1/servers/run_command", new SendServerCommandHandler());

		// Skinrestorer
		server.addEndpoint("/api/skinrestorer/get_user_skin", new GetSkinrestorerSkinHandler());

		// Internal
		server.getHttpServer().createContext("/api/internal/server/state_reporting", new ManagedServerStateReportingEndpoint());

		// Static files
		server.addStaticFileHandler("/app/", appRoot, "index.html");

	}

	public int getPort() {
		return port;
	}

	public HTTPServer getServer() {
		return server;
	}

	public boolean stop() {
		return server.stop();
	}
}
