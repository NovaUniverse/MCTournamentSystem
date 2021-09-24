package net.novauniverse.mctournamentsystem.spigot.game;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerWinEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.teams.Team;

public class GameWinMessage  extends NovaModule implements Listener{
	@Override
	public String getName() {
		return "TSWinMessageListener";
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerWin(PlayerWinEvent e) {
		ChatColor color = ChatColor.AQUA;

		Team team = TournamentSystem.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());

		if (team != null) {
			color = team.getTeamColor();
		}

		LanguageManager.broadcast("tournamentsystem.game.gameover.winner.player", color.toString(), e.getPlayer().getName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		ChatColor color = e.getTeam().getTeamColor();
		
		LanguageManager.broadcast("tournamentsystem.game.gameover.winner.team", color.toString(), e.getTeam().getDisplayName());
	}
}