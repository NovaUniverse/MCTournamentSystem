package net.novauniverse.mctournamentsystem.spigot.messages;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.messages.PlayerEliminationMessage;

public class TSPlayerEliminationMessage implements PlayerEliminationMessage {
	@Override
	public void showPlayerEliminatedMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
		String message = TournamentSystem.getInstance().getPlayerEliminationMessageProvider().getEliminationMessage(player, killer, reason, placement);
		//Log.trace("TSPlayerEliminationMessage", "Using message: " + message);
		Bukkit.getServer().broadcastMessage(message);
	}
}