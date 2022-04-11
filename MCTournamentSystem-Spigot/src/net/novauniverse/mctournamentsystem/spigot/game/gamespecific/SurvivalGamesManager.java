package net.novauniverse.mctournamentsystem.spigot.game.gamespecific;

import net.novauniverse.games.survivalgames.NovaSurvivalGames;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class SurvivalGamesManager extends NovaModule {
	public SurvivalGamesManager() {
		super("TournamentSystem.SurvivalGamesManager");
	}

	@Override
	public void onEnable() {
		if (TournamentSystem.getInstance().isUseExtendedSpawnLocations()) {
			Log.info("SurvivalGamesManager", "Setting use extended spawn locations to true");
			NovaSurvivalGames.getInstance().setUseExtendedSpawnLocations(true);
		}
	}
}