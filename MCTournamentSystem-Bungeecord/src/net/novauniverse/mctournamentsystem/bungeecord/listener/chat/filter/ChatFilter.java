package net.novauniverse.mctournamentsystem.bungeecord.listener.chat.filter;

import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class ChatFilter implements Listener {
	private List<String> filter;

	public List<String> getFilter() {
		return filter;
	}

	public ChatFilter() {
		filter = new ArrayList<String>();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(ChatEvent e) {
		ProxiedPlayer player = (ProxiedPlayer) e.getSender();

		String message = e.getMessage().toLowerCase();

		boolean detected = false;

		for (String blocked : filter) {
			if (message.matches("(.* )?" + blocked + "( .*)?")) {
				e.setMessage(e.getMessage().toLowerCase().replaceAll(blocked, "***"));
				detected = true;
			}
		}

		if (detected) {
			ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission("tournamentcore.notify.swear")).forEach(p -> p.sendMessage(new TextComponent(ChatColor.YELLOW + player.getName() + " failed swear check. Original message: " + ChatColor.AQUA + message)));
		}
	}
}