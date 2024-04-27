package net.novauniverse.mctournamentsystem.spigot.modules.finalgame;

import java.sql.SQLException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.mctournamentsystem.commons.winner.LockedWinnerManagement;
import net.novauniverse.mctournamentsystem.spigot.team.DefaultTopTeamProvider;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.Game;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.events.GameLobbyStartingEvent;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = false)
public class FinalGame extends NovaModule implements Listener {
	public FinalGame() {
		super("TournamentSystem.FinalGame");
		addDependency(GameLobby.class);
		addDependency(GameManager.class);
	}

	@Override
	public void onEnable() throws Exception {
		ModuleManager.getModule(GameLobby.class).setDisableAutoAddPlayers(true);
		if (!TeamManager.hasTeamManager()) {
			throw new RuntimeException("Tried to enable final game without a active team manager");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onGameLobbyStarting(GameLobbyStartingEvent e) {
		Game game = ModuleManager.getModule(GameManager.class).getActiveGame();

		DefaultTopTeamProvider.getTopParticipants().toList().forEach(team -> {
			team.getOnlinePlayers().forEach(game::addPlayer);
		});
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		TournamentSystemTeam team = (TournamentSystemTeam) e.getTeam();
		try {
			LockedWinnerManagement.lockWinner(team.getTeamNumber());
		} catch (SQLException e1) {
			e1.printStackTrace();
			Log.error("FinalGame", "Failed to set winner. Please manually set winner team to " + team.getDisplayName() + ". (" + team.getTeamNumber() + "). " + e1.getClass().getName() + " " + e1.getMessage());
		}
	}
}
