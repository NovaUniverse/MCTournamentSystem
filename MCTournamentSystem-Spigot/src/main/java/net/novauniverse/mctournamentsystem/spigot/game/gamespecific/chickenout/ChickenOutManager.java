package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.chickenout;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.chickenout.game.ChickenOut;
import net.novauniverse.games.chickenout.game.event.ChickenOutPlayerChickenOutEvent;
import net.novauniverse.games.chickenout.game.event.ChickenOutPlayerPlacementEvent;
import net.novauniverse.games.chickenout.game.event.ChickenOutTeamPlacementEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class ChickenOutManager extends NovaModule implements Listener {
	public static int TIME_LINE = 5;
	public static int FEATHER_COUNT_LINE = 6;
	public static int FINAL_FEATHER_COUNT_LINE = 7;

	private double CHICKEN_OUT_FEATHER_SCORE_MULTIPLIER = 0D;

	public static String LINE_PREFIX = ChatColor.GOLD.toString();

	private Task task;

	private GameManager gameManager;

	public ChickenOutManager() {
		super("TournamentSystem.GameSpecific.ChickenOutManager");
	}

	@Override
	public void onLoad() {
		JSONObject scoreConfig = TournamentSystem.getInstance().getGameSpecificScoreSettings().optJSONObject("chickenout");
		if (scoreConfig != null) {
			if (scoreConfig.has("feather_score_multiplier")) {
				CHICKEN_OUT_FEATHER_SCORE_MULTIPLIER = scoreConfig.getDouble("feather_score_multiplier");
				Log.info(getName(), "Setting feather score multiplier to " + CHICKEN_OUT_FEATHER_SCORE_MULTIPLIER);
			}
		}
		
		gameManager = GameManager.getInstance();

		// We use custom messages here instead
		GameSetup.disableEliminationMessages();
		gameManager.setPlayerEliminationMessage(new ChickenOutEliminationMessages());
		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);
		TournamentSystem.getInstance().disableEliminationTitleMessage();

		ModuleManager.disable(PlayerHeadDrop.class);

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			ChickenOut game = (ChickenOut) GameManager.getInstance().getActiveGame();
			if (!game.getAllParticipatingPlayers().contains(player.getUniqueId())) {
				game.getAllParticipatingPlayers().add(player.getUniqueId());
			}
			game.setupInventory(player);
		});

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (gameManager.hasGame()) {
					if (!TournamentSystem.getInstance().isDisableScoreboard()) {
						ChickenOut game = (ChickenOut) GameManager.getInstance().getActiveGame();

						if (gameManager.getActiveGame().hasStarted()) {
							String timeLeftLine = null;
							if (game.isRoundTimerRunning()) {
								timeLeftLine = LINE_PREFIX + "Level up in: " + ChatColor.WHITE + TextUtils.secondsToTime(game.getRoundTimeLeft());
							} else if (game.isFinalTimerRunning()) {
								timeLeftLine = LINE_PREFIX + "Game ends in: " + ChatColor.RED + TextUtils.secondsToTime(game.getFinalTimeLeft());
							}

							if (timeLeftLine == null) {
								NovaScoreboardManager.getInstance().clearGlobalLine(TIME_LINE);
							} else {
								NovaScoreboardManager.getInstance().setGlobalLine(TIME_LINE, new StaticTextLine(timeLeftLine));
							}

							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								if (game.getAllParticipatingPlayers().contains(player.getUniqueId())) {
									int feathers = game.getPlayerFeathers(player);
									int finalFeathers = game.getFinalScoreForDisplay(player.getUniqueId());
									String featherCountMessage = LINE_PREFIX + "Feathers: " + ChatColor.AQUA + feathers;
									String finalFeatherCount = LINE_PREFIX + "Stashed feathers: " + ChatColor.GREEN + finalFeathers;
									NovaScoreboardManager.getInstance().setPlayerLine(player, FEATHER_COUNT_LINE, new StaticTextLine(featherCountMessage));
									NovaScoreboardManager.getInstance().setPlayerLine(player, FINAL_FEATHER_COUNT_LINE, new StaticTextLine(finalFeatherCount));
								} else {
									NovaScoreboardManager.getInstance().clearPlayerLine(player, FEATHER_COUNT_LINE);
									NovaScoreboardManager.getInstance().clearPlayerLine(player, FINAL_FEATHER_COUNT_LINE);
								}
							});
						}
					}
				}
			}
		}, 5L);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (e.getPlayer().isOnline()) {
			Player player = e.getPlayer().getPlayer();
			VersionIndependentUtils.get().sendTitle(player, ChatColor.RED + "Eliminated", "", 10, 60, 10);
		}
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
			ScoreManager.getInstance().addTeamScore(team, score, "Chicken out team placement " + TextUtils.ordinal(e.getPlacement()) + " place");
			team.sendMessage(ChatColor.GRAY + "+" + score + " points");
			team.sendTitle(ChatColor.GREEN + TextUtils.ordinal(e.getPlacement()) + " place", "", 20, 60, 20);
			team.distributePointsToPlayers(score, "Chicken out team placement " + TextUtils.ordinal(e.getPlacement()) + " place");
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

			ScoreManager.getInstance().addPlayerScore(e.getUuid(), score, "Chicken out player placement " + TextUtils.ordinal(e.getPlacement()) + " place");
			Player player = Bukkit.getServer().getPlayer(e.getUuid());
			if (player != null) {
				VersionIndependentUtils.get().sendTitle(player, ChatColor.GREEN + TextUtils.ordinal(e.getPlacement()) + " place", "", 20, 60, 20);
				player.sendMessage(ChatColor.GRAY + "+" + score + " points");
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChickenOutPlayerChickenOut(ChickenOutPlayerChickenOutEvent e) {
		if (CHICKEN_OUT_FEATHER_SCORE_MULTIPLIER > 0) {
			Player player = e.getPlayer();
			int score = (int) Math.ceil(((double) e.getFeathers()) * CHICKEN_OUT_FEATHER_SCORE_MULTIPLIER);
			if (score <= 0) {
				return;
			}
			if (TeamManager.hasTeamManager()) {
				Team team = TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					team.sendMessage(ChatColor.GRAY + "+" + score + " points to team");
				}
			} else {
				player.sendMessage(ChatColor.GRAY + "+" + score + " points");
			}

			ScoreManager.getInstance().addPlayerScore(player, score, true, "Checken out score for " + e.getFeathers() + " feathers");
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