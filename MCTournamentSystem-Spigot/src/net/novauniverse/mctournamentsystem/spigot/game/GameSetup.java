package net.novauniverse.mctournamentsystem.spigot.game;

import java.io.File;

import org.bukkit.Bukkit;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.messages.TSActionBarCombatTagMessage;
import net.novauniverse.mctournamentsystem.spigot.messages.TSTeamEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreListener;
import net.novauniverse.mctournamentsystem.spigot.tracker.TSCompassTracker;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.gamelobby.GameLobby;

public class GameSetup {
	public static void init(TournamentSystem tournamentSystem) {
		ScoreListener scoreListener = new ScoreListener(tournamentSystem.getConfig().getBoolean("kill_score_enabled"), tournamentSystem.getConfig().getInt("kill_score"), tournamentSystem.getConfig().getBoolean("win_score_enabled"), tournamentSystem.getWinScore(), tournamentSystem.getConfig().getBoolean("participation_score_enabled"), tournamentSystem.getConfig().getInt("participation_score"));
		Bukkit.getServer().getPluginManager().registerEvents(scoreListener, tournamentSystem);
		Log.info("GameSetup", "ScoreListener started");

		ModuleManager.enable(CompassTracker.class);

		ModuleManager.loadModule(GameListeners.class, true);
		ModuleManager.loadModule(GameWinMessage.class, true);
		ModuleManager.loadModule(KillListener.class, true);

		Log.info("GameSetup", "Loaded and enabled modules required by games");

		GameManager.getInstance().setUseTeams(true);
		GameManager.getInstance().addCombatTagMessage(new TSActionBarCombatTagMessage());
		GameManager.getInstance().setTeamEliminationMessage(new TSTeamEliminationMessage());

		CompassTracker.getInstance().setStrictMode(true);
		CompassTracker.getInstance().setCompassTrackerTarget(new TSCompassTracker());
		Log.info("GameSetup", "Game variables set");

		Log.info("GameSetup", "Loading lobby maps...");

		File dataFileDirectory = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + "GameLobbyData");
		File worldFileDirectory = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + "Worlds");

		GameLobby.getInstance().getMapReader().loadAll(dataFileDirectory, worldFileDirectory);

		Log.success("GameSetup", "Game support enabled");
	}
}