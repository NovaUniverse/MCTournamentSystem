package net.novauniverse.mctournamentsystem.bungeecord.listener.ping;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;

public class PingListeners implements Listener {
	private TournamentSystem tournamentSystem;

	public PingListeners(TournamentSystem tournamentSystem) {
		this.tournamentSystem = tournamentSystem;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onProxyPing(ProxyPingEvent e) {
		ServerPing ping = e.getResponse();
		ping.setDescriptionComponent(new TextComponent(ChatColor.translateAlternateColorCodes('§', TournamentSystem.formatMOTD(tournamentSystem.getMotd()))));
		e.setResponse(ping);
	}
}