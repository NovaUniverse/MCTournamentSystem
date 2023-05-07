package net.novauniverse.mctournamentsystem.lobby.modules.labymod;

import org.bukkit.event.Listener;

import net.labymod.serverapi.api.LabyAPI;
import net.labymod.serverapi.api.serverinteraction.actionmenu.ActionType;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class LabyModLobbyIntegration extends NovaModule implements Listener {
	public LabyModLobbyIntegration() {
		super("TournamentSystem.LabyMod.Lobby");
	}

	@Override
	public void onLoad() {
		LabyAPI.getService().getMenuTransmitter().addEntry(LabyAPI.getService().getMenuEntryFactory().create("Duel Player", "duel {name}", ActionType.RUN_COMMAND));
	}
}