package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.dropper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.JSONObject;

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
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class DropperManager extends NovaModule implements Listener {
	public static final int TIME_LEFT_LINE = 5;
	public static final int DEATH_COUNT_LINE = 6;
	public static final int REMAINING_PLAYERS_LINE = 7;

	public static int LEVEL_COMPLETED_SCORE = 0;

	private Task task;
	private boolean timeLeftLineShown;

	private GameManager gameManager;

	public DropperManager() {
		super("TournamentSystem.GameSpecific.DropperManager");
	}

	@Override
	public void onLoad() {
		JSONObject scoreConfig = TournamentSystem.getInstance().getGameSpecificScoreSettings().optJSONObject("dropper");
		if (scoreConfig != null) {
			if (scoreConfig.has("complete_level")) {
				LEVEL_COMPLETED_SCORE = scoreConfig.getInt("complete_level");
				Log.info(getName(), "Setting level complete score to " + LEVEL_COMPLETED_SCORE);
			}
		}

		gameManager = GameManager.getInstance();
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
				if (!TournamentSystem.getInstance().isDisableScoreboard()) {
					boolean didShow = false;
					if (gameManager.hasGame()) {
						if (gameManager.getActiveGame().hasStarted() && !gameManager.getActiveGame().hasEnded()) {
							long totalSecs = ((Dropper) gameManager.getActiveGame()).getTimeLeft();

							if (totalSecs > 0) {
								long hours = totalSecs / 3600;
								long minutes = (totalSecs % 3600) / 60;
								long seconds = totalSecs % 60;

								String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

								NovaScoreboardManager.getInstance().setGlobalLine(TIME_LEFT_LINE, new StaticTextLine(ChatColor.GOLD + "Time left: " + ChatColor.AQUA + timeString));

								timeLeftLineShown = true;
								didShow = true;
							}
						}
					}

					if (!didShow && timeLeftLineShown) {
						NovaScoreboardManager.getInstance().clearGlobalLine(TIME_LEFT_LINE);
						timeLeftLineShown = false;
					}

					if (gameManager.hasGame()) {
						if (gameManager.getActiveGame().hasStarted()) {
							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								int deaths = ((Dropper) gameManager.getActiveGame()).getDeaths(player.getUniqueId());
								NovaScoreboardManager.getInstance().setPlayerLine(player, DEATH_COUNT_LINE, new StaticTextLine(ChatColor.GOLD + "Deaths: " + ChatColor.AQUA + deaths));
							});
						}
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
				ScoreManager.getInstance().addPlayerScore(e.getUuid(), score, "Dropper player placement");
				Player player = Bukkit.getServer().getPlayer(e.getUuid());
				if (player != null) {
					player.sendMessage(ChatColor.GRAY + "+" + score + " points");
				}
				break;

			case TEAM:
				TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getTeamByTeamUUID(e.getUuid());
				if (team != null) {
					ScoreManager.getInstance().addTeamScore(team, score, "Dropper team placement");
					team.sendMessage(ChatColor.GRAY + "+" + score + " points");
				}
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDropperPlayerCompleteRound(DropperPlayerCompleteRoundEvent e) {
		if (LEVEL_COMPLETED_SCORE > 0) {
			Player player = e.getPlayer();
			ScoreManager.getInstance().addPlayerScore(player, LEVEL_COMPLETED_SCORE, "Dropper round completion");
			if (TeamManager.hasTeamManager()) {
				Team team = TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					team.sendMessage(ChatColor.GRAY + "+" + LEVEL_COMPLETED_SCORE + " points");
				}
			} else {
				player.sendMessage(ChatColor.GRAY + "+" + LEVEL_COMPLETED_SCORE + " points");
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