package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.common;

import org.bukkit.entity.Player;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;

public class PlayerYMetadataProvider implements ITelementryMetadataProvider {
	@Override
	public void process(Player player, JSONObject metadata) {
		metadata.put("player_y", player.getLocation().getBlockY());
	}
}