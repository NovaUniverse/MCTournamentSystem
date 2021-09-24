package net.novauniverse.mctournamentsystem.bungeecord.listener;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class TSPluginMessageListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase("tournamentsystem:tsdata")) {
			e.setCancelled(true);
		}
	}
}