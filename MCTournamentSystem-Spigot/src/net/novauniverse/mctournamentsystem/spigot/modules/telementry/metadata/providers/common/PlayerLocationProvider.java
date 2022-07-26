package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.common;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;

public class PlayerLocationProvider implements ITelementryMetadataProvider {
	@Override
	public void process(Player player, JSONObject metadata) {
		JSONObject json = new JSONObject();
		Location location = player.getLocation();

		json.put("x", location.getX());
		json.put("y", location.getY());
		json.put("z", location.getZ());

		metadata.put("location", json);
	}
}