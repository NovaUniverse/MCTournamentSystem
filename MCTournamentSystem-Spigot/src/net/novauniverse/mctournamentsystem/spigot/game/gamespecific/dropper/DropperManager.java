package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.dropper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.games.dropper.NovaDropper;
import net.novauniverse.games.dropper.game.Dropper;
import net.novauniverse.games.dropper.game.event.DropperPlacementEvent;
import net.novauniverse.games.dropper.game.event.DropperPlayerCompleteRoundEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class DropperManager extends NovaModule implements Listener {
	public static final int TIME_LEFT_LINE = 5;
	public static final int DEATH_COUNT_LINE = 6;
	public static final int REMAINING_PLAYERS_LINE = 7;

	private Task task;
	private boolean timeLeftLineShown;

	public DropperManager() {
		super("TournamentSystem.GameSpecific.DropperManager");
	}

	@Override
	public void onLoad() {
		timeLeftLineShown = false;

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			Log.debug("DropperManager", "Handle player respawn for player " + player.getUniqueId());
			Dropper dropper = NovaDropper.getInstance().getGame();
			if (dropper.getMaps().size() > 0) {
				player.setGameMode(GameMode.ADVENTURE);
				if (!dropper.getRemainingPlayers().contains(player.getUniqueId())) {
					dropper.getRemainingPlayers().add(player.getUniqueId());
					dropper.teleportPlayer(player);
				}
			}
		});

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean didShow = false;
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted() && !GameManager.getInstance().getActiveGame().hasEnded()) {
						long totalSecs = ((Dropper) GameManager.getInstance().getActiveGame()).getTimeLeft();

						if (totalSecs > 0) {
							long hours = totalSecs / 3600;
							long minutes = (totalSecs % 3600) / 60;
							long seconds = totalSecs % 60;

							String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

							NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LEFT_LINE, ChatColor.GOLD + "Time left: " + ChatColor.AQUA + timeString);

							timeLeftLineShown = true;
							didShow = true;
						}
					}
				}

				if (!didShow && timeLeftLineShown) {
					NetherBoardScoreboard.getInstance().clearGlobalLine(TIME_LEFT_LINE);
					timeLeftLineShown = false;
				}

				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							int deaths = ((Dropper) GameManager.getInstance().getActiveGame()).getDeaths(player.getUniqueId());
							NetherBoardScoreboard.getInstance().setPlayerLine(DEATH_COUNT_LINE, player, ChatColor.GOLD + "Deaths: " + ChatColor.AQUA + deaths);
						});
					}
				}
			}
		}, 10L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDropperPlacement(DropperPlacementEvent e) {
		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if ((e.getPlacement() - 1) < winScore.length) {
			int score = winScore[e.getPlacement() - 1];

			switch (e.getType()) {
			case PLAYER:
				ScoreManager.getInstance().addPlayerScore(e.getUuid(), score);
				Player player = Bukkit.getServer().getPlayer(e.getUuid());
				if (player != null) {
					player.sendMessage(ChatColor.GRAY + "+" + score + " points");
				}
				break;

			case TEAM:
				TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getTeamByTeamUUID(e.getUuid());
				if (team != null) {
					ScoreManager.getInstance().addTeamScore(team, score);
					team.sendMessage(ChatColor.GRAY + "+" + score + " points");
				}
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDropperPlayerCompleteRound(DropperPlayerCompleteRoundEvent e) {
		int score = TournamentSystem.getInstance().getDropperCompleteLevelScore();

		if (score > 0) {
			Player player = e.getPlayer();
			ScoreManager.getInstance().addPlayerScore(player, score);
			if (TeamManager.hasTeamManager()) {
				Team team = TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					team.sendMessage(ChatColor.GRAY + "+" + score + " points");
				}
			} else {
				player.sendMessage(ChatColor.GRAY + "+" + score + " points");
			}
		}
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}