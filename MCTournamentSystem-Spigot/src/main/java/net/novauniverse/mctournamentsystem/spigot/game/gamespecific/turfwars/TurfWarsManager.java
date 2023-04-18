package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.turfwars;

import org.bukkit.event.Listener;

import net.novauniverse.game.turfwars.TurfWarsPlugin;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class TurfWarsManager extends NovaModule implements Listener {
	public TurfWarsManager() {
		super("TournamentSystem.TurfWarsManager");
	}

	@Override
	public void onLoad() {
		TurfWarsPlugin.Companion.getInstance().setTurfWarsTeamPopulator(new TournamentSystemTurfWarsTeamPopulator());
	}
}