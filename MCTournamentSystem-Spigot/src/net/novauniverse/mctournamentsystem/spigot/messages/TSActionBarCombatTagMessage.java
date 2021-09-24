package net.novauniverse.mctournamentsystem.spigot.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.modules.game.CombatTagMessage;

public class TSActionBarCombatTagMessage implements CombatTagMessage {
	@Override
	public void showTaggedMessage(Player player) {
		// TODO: Load from language file
		NovaCore.getInstance().getActionBar().sendMessage(player, ChatColor.RED + "Combat tagged");
	}

	@Override
	public void showNoLongerTaggedMessage(Player player) {
		// TODO: Load from language file
		NovaCore.getInstance().getActionBar().sendMessage(player, ChatColor.GREEN + "No longer combat tagged");
	}
}