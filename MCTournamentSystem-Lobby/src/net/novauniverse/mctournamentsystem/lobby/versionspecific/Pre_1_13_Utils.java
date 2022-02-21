package net.novauniverse.mctournamentsystem.lobby.versionspecific;

import org.bukkit.Achievement;
import org.bukkit.entity.Player;

public class Pre_1_13_Utils {
	public static void giveOpenInventoryAchivement(Player player) {
		if (!player.hasAchievement(Achievement.OPEN_INVENTORY)) {
			player.awardAchievement(Achievement.OPEN_INVENTORY);
		}
	}
}