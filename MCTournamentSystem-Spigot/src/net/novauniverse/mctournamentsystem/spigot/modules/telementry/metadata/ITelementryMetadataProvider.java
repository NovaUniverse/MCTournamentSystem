package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata;

import org.bukkit.entity.Player;
import org.json.JSONObject;

public interface ITelementryMetadataProvider {
	public void process(Player player, JSONObject metadata);
}