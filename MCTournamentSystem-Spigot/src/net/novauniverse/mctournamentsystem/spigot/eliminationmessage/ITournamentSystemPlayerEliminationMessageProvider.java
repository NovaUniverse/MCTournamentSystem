package net.novauniverse.mctournamentsystem.spigot.eliminationmessage;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;

public interface ITournamentSystemPlayerEliminationMessageProvider {
	public String getEliminationMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement);
}