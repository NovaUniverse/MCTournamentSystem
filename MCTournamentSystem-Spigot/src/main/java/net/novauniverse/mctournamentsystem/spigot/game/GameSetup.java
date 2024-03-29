package net.novauniverse.mctournamentsystem.spigot.game;

import java.io.File;

import org.bukkit.Bukkit;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.NOPEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.labymod.LabyModGameIntegration;
import net.novauniverse.mctournamentsystem.spigot.messages.TSActionBarCombatTagMessage;
import net.novauniverse.mctournamentsystem.spigot.messages.TSPlayerEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.messages.TSTeamEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.PlayerTelementryManager;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.triggers.TriggerProvider;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreListener;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.gameengine.compass.trackers.ClosestEnemyPlayerTracker;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.mapselector.selectors.RandomLobbyMapSelector;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;

public class GameSetup {
	public static void init(TournamentSystem tournamentSystem) {
		GameManager gameManager = GameManager.getInstance();

		ScoreListener scoreListener = new ScoreListener(tournamentSystem.getConfig().getBoolean("kill_score_enabled"), tournamentSystem.getConfig().getInt("kill_score"), tournamentSystem.getConfig().getBoolean("win_score_enabled"), tournamentSystem.getWinScore(), tournamentSystem.getConfig().getBoolean("participation_score_enabled"), tournamentSystem.getConfig().getInt("participation_score"));
		tournamentSystem.setScoreListener(scoreListener);
		Bukkit.getServer().getPluginManager().registerEvents(scoreListener, tournamentSystem);
		Log.info("GameSetup", "ScoreListener started");

		ModuleManager.enable(CompassTracker.class);

		ModuleManager.loadModule(TournamentSystem.getInstance(), GameListeners.class, true);
		ModuleManager.loadModule(TournamentSystem.getInstance(), GameWinMessage.class, true);
		ModuleManager.loadModule(TournamentSystem.getInstance(), KillListener.class, true);

		Log.info("GameSetup", "Loaded and enabled modules required by games");

		gameManager.setUseTeams(true);
		gameManager.addCombatTagMessage(new TSActionBarCombatTagMessage());
		gameManager.setTeamEliminationMessage(new TSTeamEliminationMessage());
		gameManager.setPlayerEliminationMessage(new TSPlayerEliminationMessage());

		CompassTracker.getInstance().setStrictMode(true);
		CompassTracker.getInstance().setCompassTrackerTarget(new ClosestEnemyPlayerTracker());
		Log.info("GameSetup", "Game variables set");

		Log.info("GameSetup", "Loading lobby maps...");

		File dataFileDirectory = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + "GameLobbyData");
		File worldFileDirectory = new File(TournamentSystem.getInstance().getMapDataFolder() + File.separator + "Worlds");

		dataFileDirectory.mkdirs();
		worldFileDirectory.mkdirs();

		GameLobby.getInstance().getMapReader().loadAll(dataFileDirectory, worldFileDirectory);
		GameLobby.getInstance().setMapSelector(new RandomLobbyMapSelector());

		Log.success("GameSetup", "Game support enabled");

		PlayerTelementryManager.getInstance().addMetadataProvider(new TriggerProvider());
		
		if (Bukkit.getServer().getPluginManager().getPlugin("LabyApi") != null) {
			ModuleManager.loadModule(tournamentSystem, LabyModGameIntegration.class, true);
		}
	}

	public static void disableEliminationMessages() {
		GameManager gameManager = GameManager.getInstance();
		gameManager.setTeamEliminationMessage(new NOPEliminationMessage());
		gameManager.setPlayerEliminationMessage(new NOPEliminationMessage());
	}
}