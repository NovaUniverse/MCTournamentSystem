package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.chickenout;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.novauniverse.games.chickenout.game.ChickenOut;
import net.novauniverse.games.chickenout.game.event.ChickenOutPlayerChickenOutEvent;
import net.novauniverse.games.chickenout.game.event.ChickenOutPlayerPlacementEvent;
import net.novauniverse.games.chickenout.game.event.ChickenOutTeamPlacementEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class ChickenOutManager extends NovaModule implements Listener {
	public static final int TIME_LINE = 5;
	public static final int FEATHER_COUNT_LINE = 6;
	public static final int FINAL_FEATHER_COUNT_LINE = 7;

	private Task task;

	public ChickenOutManager() {
		super("TournamentSystem.ChickenOutManager");
	}

	@Override
	public void onLoad() {
		// We use custom messages here instead
		GameSetup.disableEliminationMessages();
		GameManager.getInstance().setPlayerEliminationMessage(new ChickenOutEliminationMessages());
		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			ChickenOut game = (ChickenOut) GameManager.getInstance().getActiveGame();
			if (!game.getAllParticipatingPlayers().contains(player.getUniqueId())) {
				game.getAllParticipatingPlayers().add(player.getUniqueId());
			}
		});

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					ChickenOut game = (ChickenOut) GameManager.getInstance().getActiveGame();

					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						String timeLeftLine = null;
						if (game.isRoundTimerRunning()) {
							timeLeftLine = ChatColor.GOLD + "Level up in: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getRoundTimeLeft());
						} else if (game.isFinalTimerRunning()) {
							timeLeftLine = ChatColor.GOLD + "Game ends in: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getFinalTimeLeft());
						}

						if (timeLeftLine == null) {
							NetherBoardScoreboard.getInstance().clearGlobalLine(TIME_LINE);
						} else {
							NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LINE, timeLeftLine);
						}

						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							if (game.getAllParticipatingPlayers().contains(player.getUniqueId())) {
								int feathers = game.getPlayerFeathers(player);
								int finalFeathers = game.getFinalScoreForDisplay(player.getUniqueId());
								String featherCountMessage = ChatColor.GOLD + "Feathers: " + ChatColor.AQUA + feathers;
								String finalFeatherCount = ChatColor.GOLD + "Stashed feathers: " + ChatColor.AQUA + finalFeathers;
								NetherBoardScoreboard.getInstance().setPlayerLine(FEATHER_COUNT_LINE, player, featherCountMessage);
								NetherBoardScoreboard.getInstance().setPlayerLine(FINAL_FEATHER_COUNT_LINE, player, finalFeatherCount);
							} else {
								NetherBoardScoreboard.getInstance().clearPlayerLine(FEATHER_COUNT_LINE, player);
								NetherBoardScoreboard.getInstance().clearPlayerLine(FINAL_FEATHER_COUNT_LINE, player);
							}
						});
					}
				}
			}
		}, 5L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChickenOutTeamPlacement(ChickenOutTeamPlacementEvent e) {
		Log.trace("ChickenOutManager", "ChickenOutTeamPlacementEvent score: " + e.getScore());
		if (e.getScore() <= 0) {
			return;
		}

		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if ((e.getPlacement() - 1) < winScore.length) {
			int score = winScore[e.getPlacement() - 1];

			TournamentSystemTeam team = (TournamentSystemTeam) e.getTeam();

			ScoreManager.getInstance().addTeamScore(team, score);
			team.sendMessage(ChatColor.GRAY + "+" + score + " points");
			team.sendTitle(ChatColor.GREEN + TextUtils.ordinal(e.getPlacement()) + " place", getClassName(), 20, 60, 20);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChickenOutPlayerPlacement(ChickenOutPlayerPlacementEvent e) {
		Log.trace("ChickenOutManager", "ChickenOutPlayerPlacementEvent score: " + e.getScore());
		if (e.getScore() <= 0) {
			return;
		}

		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if ((e.getPlacement() - 1) < winScore.length) {
			int score = winScore[e.getPlacement() - 1];

			ScoreManager.getInstance().addPlayerScore(e.getUuid(), score);
			Player player = Bukkit.getServer().getPlayer(e.getUuid());
			if (player != null) {
				VersionIndependentUtils.get().sendTitle(player, ChatColor.GREEN + TextUtils.ordinal(e.getPlacement()) + " place", "", 20, 60, 20);
				player.sendMessage(ChatColor.GRAY + "+" + score + " points");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChickenOutPlayerChickenOut(ChickenOutPlayerChickenOutEvent e) {
		Player player = e.getPlayer();
		int score = (int) Math.ceil(((double) e.getFeathers()) * TournamentSystem.getInstance().getChickenOutFeatherScoreMultiplier());
		if (score <= 0) {
			return;
		}
		if (TeamManager.hasTeamManager()) {
			Team team = TeamManager.getTeamManager().getPlayerTeam(player);
			if (team != null) {
				team.sendMessage(ChatColor.GRAY + "+" + score + " points added to team");
			}
		} else {
			player.sendMessage(ChatColor.GRAY + "+" + score + " points added to team");
		}

		ScoreManager.getInstance().addPlayerScore(player, score, true);
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