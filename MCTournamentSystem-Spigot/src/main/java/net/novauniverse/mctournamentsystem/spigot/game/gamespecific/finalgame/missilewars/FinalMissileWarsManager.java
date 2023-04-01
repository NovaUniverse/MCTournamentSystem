package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.finalgame.missilewars;

import org.bukkit.event.Listener;

import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class FinalMissileWarsManager extends NovaModule implements Listener {
	public FinalMissileWarsManager() {
		super("TournamentSystem.GameSpecific.FinalMissileWarsManager");
	}

	@Override
	public void onLoad() {
		NovaFinalMissileWars.getInstance().setFinalGameTeamProvider(new FinalMissileWarsTeamPairProvider());
	}

	@Override
	public void onEnable() throws Exception {
	}

	@Override
	public void onDisable() throws Exception {
	}
}