package net.novauniverse.mctournamentsystem.bungeecord.listener.pluginmessages;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class TSPluginMessageListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase(TournamentSystemCommons.DATA_CHANNEL) || e.getTag().equalsIgnoreCase(TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL)) {
			e.setCancelled(true);
		}
	}
}