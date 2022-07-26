package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.tnttag;

import org.bukkit.entity.Player;
import org.json.JSONObject;

import net.novauniverse.games.tnttag.game.TNTTag;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;

public class TNTTagMetadataProvider implements ITelementryMetadataProvider {
	@Override
	public void process(Player player, JSONObject metadata) {
		TNTTag game = (TNTTag) GameManager.getInstance().getActiveGame();
		metadata.put("tnttag_tagged", game.getTaggedPlayers().contains(player.getUniqueId()));
	}
}