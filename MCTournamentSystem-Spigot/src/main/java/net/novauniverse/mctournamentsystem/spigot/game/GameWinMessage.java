package net.novauniverse.mctournamentsystem.spigot.game;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerWinEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.teams.Team;

public class GameWinMessage extends NovaModule implements Listener {
	public GameWinMessage() {
		super("TournamentSystem.GameWinMessage");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerWin(PlayerWinEvent e) {
		ChatColor color = ChatColor.AQUA;

		Team team = TournamentSystem.getInstance().getTeamManager().getPlayerTeam(e.getPlayer());

		if (team != null) {
			color = team.getTeamColor();
		}

		final ChatColor finalColor = color;

		new BukkitRunnable() {
			@Override
			public void run() {
				LanguageManager.broadcast("tournamentsystem.game.gameover.winner.player", finalColor.toString(), e.getPlayer().getName());
			}
		}.runTaskLater(TournamentSystem.getInstance(), 4L);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		ChatColor color = e.getTeam().getTeamColor();

		new BukkitRunnable() {
			@Override
			public void run() {
				LanguageManager.broadcast("tournamentsystem.game.gameover.winner.team", color.toString(), e.getTeam().getDisplayName());
			}
		}.runTaskLater(TournamentSystem.getInstance(), 4L);
	}
}