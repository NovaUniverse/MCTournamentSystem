package net.novauniverse.mctournamentsystem.spigot.eliminationmessage;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.messages.PlayerEliminationMessage;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.messages.TeamEliminationMessage;
import net.zeeraa.novacore.spigot.teams.Team;

/**
 * Used to hide elimination messages
 * @author Zeeraa
 *
 */
public class NOPEliminationMessage implements PlayerEliminationMessage, TeamEliminationMessage {
	@Override
	public void showTeamEliminatedMessage(Team team, int placement) {
	}

	@Override
	public void showPlayerEliminatedMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
	}
}