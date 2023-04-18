package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.survivalgames;

import net.novauniverse.games.survivalgames.SurvivalGamesPlugin;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class SurvivalGamesManager extends NovaModule {
	public SurvivalGamesManager() {
		super("TournamentSystem.GameSpecific.SurvivalGamesManager");
	}

	@Override
	public void onEnable() {
		if (TournamentSystem.getInstance().isUseExtendedSpawnLocations()) {
			Log.info("SurvivalGamesManager", "Setting use extended spawn locations to true");
			SurvivalGamesPlugin.getInstance().setUseExtendedSpawnLocations(true);
		}
	}
}