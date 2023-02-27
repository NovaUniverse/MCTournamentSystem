package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.behindyourtail;

import org.bukkit.entity.Player;
import org.json.JSONObject;

import net.novauniverse.behindyourtail.NovaBehindYourTail;
import net.novauniverse.behindyourtail.game.role.Role;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;

public class BehindYourTailMetadataProvider implements ITelementryMetadataProvider {
	private GameManager gameManager;
	
	public BehindYourTailMetadataProvider() {
		gameManager = GameManager.getInstance();
	}
	
	private static final String ROLE_KEY = "behind_your_tail_role";
	
	@Override
	public void process(Player player, JSONObject metadata) {
		if (gameManager.getActiveGame().hasStarted()) {
			Role role = NovaBehindYourTail.getInstance().getGame().getPlayerRole(player.getUniqueId());
			if (role == null) {
				metadata.put(ROLE_KEY, "NONE");
			} else {
				metadata.put(ROLE_KEY, role.toString());
			}
		} else {
			metadata.put(ROLE_KEY, "NONE");
		}
	}
}