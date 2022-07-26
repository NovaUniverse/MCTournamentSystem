package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.common;

import org.bukkit.entity.Player;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;

public class PlayerHungerProvider implements ITelementryMetadataProvider {
	@Override
	public void process(Player player, JSONObject metadata) {
		metadata.put("food_level", player.getFoodLevel());
	}
}