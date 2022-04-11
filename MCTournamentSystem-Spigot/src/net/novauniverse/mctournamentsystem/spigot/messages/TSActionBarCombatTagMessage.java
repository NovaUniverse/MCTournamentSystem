package net.novauniverse.mctournamentsystem.spigot.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.CombatTagMessage;

public class TSActionBarCombatTagMessage implements CombatTagMessage {
	@Override
	public void showTaggedMessage(Player player) {
		// TODO: Load from language file
		VersionIndependantUtils.get().sendActionBarMessage(player, ChatColor.RED + "Combat tagged");
	}

	@Override
	public void showNoLongerTaggedMessage(Player player) {
		// TODO: Load from language file
		VersionIndependantUtils.get().sendActionBarMessage(player, ChatColor.GREEN + "No longer combat tagged");
	}
}