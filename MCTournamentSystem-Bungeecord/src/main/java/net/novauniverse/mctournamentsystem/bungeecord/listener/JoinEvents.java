package net.novauniverse.mctournamentsystem.bungeecord.listener;

import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import de.dombo.bungeemessages.BungeeMessages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class JoinEvents implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer player = e.getPlayer();

		if (player.hasPermission("tournamentcore.autosocialspy")) {
			if (!BungeeMessages.getPlugin().getManager().isSocialSpy(player)) {
				BungeeMessages.getPlugin().getManager().playerSocialSpy().add(player);
				player.sendMessage(new ComponentBuilder("Social spy enabled since you have moderator permissions").color(ChatColor.GREEN).create());
			}
		} else if (BungeeMessages.getPlugin().getManager().isSocialSpy(player)) {
			BungeeMessages.getPlugin().getManager().playerSocialSpy().remove(player);
		}

		String activeServerName = TournamentSystemCommons.getActiveServer();
		if (activeServerName != null) {
			ServerInfo activeServer = ProxyServer.getInstance().getServerInfo(activeServerName);
			if (activeServer == null) {
				Log.error("JoinEvents", "Cant find server with name: " + activeServerName);
			} else {
				ProxyServer.getInstance().getScheduler().schedule(TournamentSystem.getInstance(), new Runnable() {
					@Override
					public void run() {
						if (player.isConnected()) {
							player.connect(activeServer);
							player.sendMessage(new ComponentBuilder("Connecting to " + activeServerName + " since a game is active. Use /hub to get back to the lobby").color(ChatColor.GOLD).create());
						}
					}
				}, 5, TimeUnit.SECONDS);
			}
		}

		ProxyServer.getInstance().getPlayers().forEach(p -> {
			if ((p.getUniqueId().equals(e.getPlayer().getUniqueId()))) {
				return;
			}

			if (p.hasPermission("tournamentsystem.joinnotifications")) {
				ClickEvent event = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sendhere " + e.getPlayer().getName());

				p.sendMessage(new ComponentBuilder(e.getPlayer().getName() + " connected. Click this message to send them to your server").color(ChatColor.GOLD).event(event).create());
			}
		});
		
		if(TournamentSystemCommons.hasSocketAPI()) {
			JSONObject data = new JSONObject();
			data.put("username", e.getPlayer().getName());
			data.put("uuid", e.getPlayer().getUniqueId().toString());
			data.put("version", e.getPlayer().getPendingConnection().getVersion());
			TournamentSystemCommons.getSocketAPI().sendEventAsync("proxy_player_join", data);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		JSONObject data = new JSONObject();
		data.put("username", e.getPlayer().getName());
		data.put("uuid", e.getPlayer().getUniqueId().toString());
		TournamentSystemCommons.getSocketAPI().sendEventAsync("proxy_player_quit", data);
	}
}