package net.novauniverse.mctournamentsystem.spigot.game.util;

import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;

public interface PlayerEliminatedTitleProvider {
	public void show(PlayerEliminatedEvent e);
}