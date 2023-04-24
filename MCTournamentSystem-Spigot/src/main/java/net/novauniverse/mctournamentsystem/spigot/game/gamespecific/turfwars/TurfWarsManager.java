package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.turfwars;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.game.turfwars.TurfWarsPlugin;
import net.novauniverse.game.turfwars.game.TurfWars;
import net.novauniverse.game.turfwars.game.data.PlayerData;
import net.novauniverse.game.turfwars.game.event.TurfWarsBeginEvent;
import net.novauniverse.game.turfwars.game.event.TurfWarsDeathEvent;
import net.novauniverse.game.turfwars.game.event.TurfWarsTurfChangeEvent;
import net.novauniverse.game.turfwars.game.team.TurfWarsTeam;
import net.novauniverse.game.turfwars.game.team.TurfWarsTeamData;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.kills.KillManager;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class TurfWarsManager extends NovaModule implements Listener {
	public static int BUILD_TIME_LINE = 6;
	public static int SCORE_LINE = 7;

	public static int TEAM_WIN_SCORE = 100;

	public static int WINNER_TEAM_KILL_SCORE_POOL = 1000;
	public static int WINNER_TEAM_KILL_SCORE_MAX = 250;

	public static int LOOSER_TEAM_KILL_SCORE_POOL = 500;
	public static int LOOSER_TEAM_KILL_SCORE_MAX = 125;

	private Task updateTask;

	private GameManager gameManager;

	public TurfWarsManager() {
		super("TournamentSystem.TurfWarsManager");
	}

	@Override
	public void onLoad() {
		JSONObject scoreConfig = TournamentSystem.getInstance().getGameSpecificScoreSettings().optJSONObject("turfwars");
		if (scoreConfig != null) {
			if (scoreConfig.has("team_win_score")) {
				TEAM_WIN_SCORE = scoreConfig.getInt("team_win_score");
				Log.info(getName(), "Setting team win score to " + TEAM_WIN_SCORE);
			}

			if (scoreConfig.has("winner_team_kill_score_pool")) {
				WINNER_TEAM_KILL_SCORE_POOL = scoreConfig.getInt("winner_team_kill_score_pool");
				Log.info(getName(), "Setting winner team kill score pool to " + WINNER_TEAM_KILL_SCORE_POOL);
			}

			if (scoreConfig.has("winner_team_kill_score_max")) {
				WINNER_TEAM_KILL_SCORE_MAX = scoreConfig.getInt("winner_team_kill_score_max");
				Log.info(getName(), "Setting winner team kill score max to " + WINNER_TEAM_KILL_SCORE_MAX);
			}

			if (scoreConfig.has("looser_team_kill_score_pool")) {
				LOOSER_TEAM_KILL_SCORE_POOL = scoreConfig.getInt("looser_team_kill_score_pool");
				Log.info(getName(), "Setting looser team kill score pool to " + LOOSER_TEAM_KILL_SCORE_POOL);
			}

			if (scoreConfig.has("looser_team_kill_score_max")) {
				LOOSER_TEAM_KILL_SCORE_MAX = scoreConfig.getInt("looser_team_kill_score_max");
				Log.info(getName(), "Setting looser team kill score max to " + LOOSER_TEAM_KILL_SCORE_MAX);
			}
		}

		TurfWarsPlugin.getInstance().setTurfWarsTeamPopulator(new TournamentSystemTurfWarsTeamPopulator());

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			Team playerTeam = TeamManager.getTeamManager().getPlayerTeam(player);
			if (playerTeam == null) {
				return;
			}

			if (gameManager.hasGame()) {
				TurfWars game = (TurfWars) gameManager.getActiveGame();
				if (!game.isPlayerInGame(player)) {
					game.addPlayer(player);
				}

				TurfWarsTeamData existingTeam = game.getTeam(player);
				if (existingTeam == null) {
					TurfWarsTeamData recommended = game.getTeams()
							.stream()
							.filter(twt -> twt
									.getMembers()
									.stream()
									.anyMatch(tm -> playerTeam
											.isMember(tm)))
							.findFirst()
							.orElse(null);
					if (recommended == null) {
						int team1Members = (int) game.getTeam1().getMembers().stream().filter(uuid -> game.isPlayerInGame(uuid)).count();
						int team2Members = (int) game.getTeam2().getMembers().stream().filter(uuid -> game.isPlayerInGame(uuid)).count();

						recommended = team1Members > team2Members ? game.getTeam2() : game.getTeam1();
					}

					recommended.getMembers().add(player.getUniqueId());
				}

				game.tpPlayer(player);
				setupOverrides(player);
			}
		});

		gameManager = ModuleManager.getModule(GameManager.class);

		updateTask = new SimpleTask(getPlugin(), () -> {
			if (gameManager.hasGame()) {
				TurfWars game = (TurfWars) gameManager.getActiveGame();

				if (game.hasStarted()) {
					DelayedGameTrigger buildEndTrigger = game.getBuildTimeEndTrigger();
					DelayedGameTrigger buildStartTrigger = game.getBuildTimeStartTrigger();

					if (buildEndTrigger.isRunning()) {
						NetherBoardScoreboard.getInstance().setGlobalLine(BUILD_TIME_LINE, ChatColor.GOLD + "Build ends in: " + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(buildEndTrigger.getTicksLeft() / 20));
					} else if (buildStartTrigger.isRunning()) {
						NetherBoardScoreboard.getInstance().setGlobalLine(BUILD_TIME_LINE, ChatColor.GOLD + "Build starts in: " + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(buildStartTrigger.getTicksLeft() / 20));
					}
					Bukkit.getOnlinePlayers().forEach(player -> {
						TurfWarsTeamData first = game.getTeam1();
						TurfWarsTeamData second = game.getTeam2();

						if (game.isPlayerInGame(player)) {
							TurfWarsTeamData playerTeam = game.getTeam(player);
							if (playerTeam != null) {
								if (playerTeam.getTeam() == TurfWarsTeam.TEAM_2) {
									first = game.getTeam2();
									second = game.getTeam1();
								}
							}
						}

						int diff = first.getKills() - second.getKills();

						int firstTurf = (game.getTurfSize() / 2) + diff;
						int secondTurf = (game.getTurfSize() / 2) - diff;

						if (game.isPlayerInGame(player)) {
							NetherBoardScoreboard.getInstance().setPlayerLine(SCORE_LINE, player, ChatColor.GREEN + ChatColor.BOLD.toString() + "(You) " + firstTurf + ChatColor.WHITE + " vs " + ChatColor.RED + ChatColor.BOLD.toString() + secondTurf + " (Enemy)");
						} else {
							NetherBoardScoreboard.getInstance().setPlayerLine(SCORE_LINE, player, first.getTeamConfig().getChatColor() + ChatColor.BOLD.toString() + "team 1 " + firstTurf + ChatColor.WHITE + " vs " + second.getTeamConfig().getChatColor() + ChatColor.BOLD.toString() + secondTurf + " team 2");
						}
					});
				}
			}
		}, 10L);
	}

	public void setupOverrides(Player player) {
		if (gameManager.hasGame()) {
			TurfWars game = (TurfWars) gameManager.getActiveGame();
			if (game.isPlayerInGame(player)) {
				TurfWarsTeamData team = game.getTeam(player);
				if (team != null) {
					TournamentSystemTeamManager.getInstance().setPlayerTeamColorOverride(player.getUniqueId(), team.getTeamConfig().getChatColor());
					TournamentSystemTeamManager.getInstance().setPlayerTeamNameOverride(player.getUniqueId(), team.getTeamConfig().getDisplayName());
					TournamentSystemTeamManager.getInstance().updateNetherboardColor(player);
					TournamentSystemTeamManager.getInstance().updatePlayerName(player);
				}
			}
		}
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(updateTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}

	public JSONObject turfwarsTeamDataToJSON(TurfWarsTeamData data) {
		JSONObject result = new JSONObject();

		result.put("team", data.getTeam().name());
		result.put("display_name", data.getTeamConfig().getDisplayName());
		result.put("chat_color", data.getTeamConfig().getChatColor().name());
		result.put("dye_color", data.getTeamConfig().getDyeColor().name());

		Color color = data.getTeamConfig().getColor();
		JSONObject rgb = new JSONObject();
		rgb.put("r", color.getRed());
		rgb.put("g", color.getGreen());
		rgb.put("b", color.getBlue());

		result.put("color", rgb);

		return result;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTurfWarsDeath(TurfWarsDeathEvent e) {
		if (e.getKiller() != null) {
			KillManager.addPlayerKill(e.getKiller());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameEnd(GameEndEvent e) {
		if (e.getReason() == GameEndReason.WIN) {
			TurfWars game = (TurfWars) e.getGame();
			TurfWarsTeam winner = game.getWinner();
			if (winner != null) {
				TurfWarsTeamData winnerTeam = game.getTeam(winner);
				{
					List<TurfWarsTeamResult> teamResults = new ArrayList<>();

					winnerTeam.getMembers().forEach(member -> {
						PlayerData data = game.getPlayerData(member);
						if (data != null) {
							TeamManager.getTeamManager().ifHasTeam(member, playerTeam -> {
								if (!teamResults.stream().anyMatch(t -> t.getTeam().equals(playerTeam))) {
									teamResults.add(new TurfWarsTeamResult((TournamentSystemTeam) playerTeam));
								}
								teamResults.stream().filter(r -> r.getTeam().equals(playerTeam)).findFirst().ifPresent(td -> td.addKills(data.getKills()));
							});
						}
					});

					if (TEAM_WIN_SCORE > 0) {
						teamResults.forEach(t -> {
							TournamentSystemTeam team = t.getTeam();
							team.sendMessage(ChatColor.GRAY + "Team won. +" + TEAM_WIN_SCORE + " points");
							ScoreManager.getInstance().addTeamScore(team, TEAM_WIN_SCORE);
							team.distributePointsToPlayers(TEAM_WIN_SCORE);
						});
					}

					if (WINNER_TEAM_KILL_SCORE_POOL > 0) {
						int totalKills = teamResults.stream().mapToInt(TurfWarsTeamResult::getKills).sum();
						if (totalKills == 0) {
							return;
						}

						Log.debug("TurfWars(Team Won)", "The total amount of kills for all teams is " + totalKills + ". Score pool is " + WINNER_TEAM_KILL_SCORE_POOL + " and max kill score is " + WINNER_TEAM_KILL_SCORE_MAX);

						teamResults.stream().filter(t -> t.getKills() > 0).forEach(t -> {
							TournamentSystemTeam team = t.getTeam();

							double killPercentage = Double.valueOf(t.getKills()) / Double.valueOf(totalKills);
							int score = (int) Math.ceil(killPercentage * Double.valueOf(WINNER_TEAM_KILL_SCORE_POOL));
							if (score > WINNER_TEAM_KILL_SCORE_MAX) {
								Log.trace("TurfWars(Team Won)", "Capping score to " + WINNER_TEAM_KILL_SCORE_MAX + " for team " + team.getDisplayName() + " since their score of " + score + " is above the max kill score");
								score = WINNER_TEAM_KILL_SCORE_MAX;
							}

							if (score <= 0) {
								Log.trace("TurfWars(Team Won)", "Team " + team.getDisplayName() + " had " + ((int) (killPercentage * 100D)) + "% of all kills but the score was less than 1");
							} else {
								Log.trace("TurfWars(Team Won)", "Team " + team.getDisplayName() + " had " + ((int) (killPercentage * 100D)) + "% of all kills and should get " + score + " as reward");
								team.sendMessage(ChatColor.GRAY + "Team got " + t.getKills() + " kills. +" + score + " points");
								ScoreManager.getInstance().addTeamScore(team, score);
								team.distributePointsToPlayers(score);
							}
						});
					}
				}

				TurfWarsTeamData teamLost = game.getTeam(winner.getOpposite());
				{
					List<TurfWarsTeamResult> teamResults = new ArrayList<>();
					teamLost.getMembers().forEach(member -> {
						PlayerData data = game.getPlayerData(member);
						if (data != null) {
							TeamManager.getTeamManager().ifHasTeam(member, playerTeam -> {
								if (!teamResults.stream().anyMatch(t -> t.getTeam().equals(playerTeam))) {
									teamResults.add(new TurfWarsTeamResult((TournamentSystemTeam) playerTeam));
								}
								teamResults.stream().filter(r -> r.getTeam().equals(playerTeam)).findFirst().ifPresent(td -> td.addKills(data.getKills()));
							});
						}
					});

					if (WINNER_TEAM_KILL_SCORE_POOL > 0) {
						int totalKills = teamResults.stream().mapToInt(TurfWarsTeamResult::getKills).sum();
						if (totalKills == 0) {
							return;
						}

						Log.debug("TurfWars(Team Lost)", "The total amount of kills for all teams is " + totalKills + ". Score pool is " + WINNER_TEAM_KILL_SCORE_POOL + " and max kill score is " + WINNER_TEAM_KILL_SCORE_MAX);

						teamResults.stream().filter(t -> t.getKills() > 0).forEach(t -> {
							TournamentSystemTeam team = t.getTeam();

							double killPercentage = Double.valueOf(t.getKills()) / Double.valueOf(totalKills);
							int score = (int) Math.ceil(killPercentage * Double.valueOf(WINNER_TEAM_KILL_SCORE_POOL));
							if (score > WINNER_TEAM_KILL_SCORE_MAX) {
								Log.trace("TurfWars(Team Lost)", "Capping score to " + WINNER_TEAM_KILL_SCORE_MAX + " for team " + team.getDisplayName() + " since their score of " + score + " is above the max kill score");
								score = WINNER_TEAM_KILL_SCORE_MAX;

							}

							if (score <= 0) {
								Log.trace("TurfWars(Team Lost)", "Team " + team.getDisplayName() + " had " + ((int) (killPercentage * 100D)) + "% of all kills but the score was less than 1");
							} else {
								Log.trace("TurfWars(Team Lost)", "Team " + team.getDisplayName() + " had " + ((int) (killPercentage * 100D)) + "% of all kills and should get " + score + " as reward");
								team.sendMessage(ChatColor.GRAY + "Team got " + t.getKills() + " kills. +" + score + " points");
								ScoreManager.getInstance().addTeamScore(team, score);
								team.distributePointsToPlayers(score);
							}
						});
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTurfWarsBegin(TurfWarsBeginEvent e) {
		Bukkit.getOnlinePlayers().forEach(this::setupOverrides);

		if (TournamentSystemCommons.hasSocketAPI()) {
			JSONObject data = new JSONObject();
			JSONObject team1 = turfwarsTeamDataToJSON(e.getTeam1());
			JSONObject team2 = turfwarsTeamDataToJSON(e.getTeam2());

			data.put("team1", team1);
			data.put("team2", team2);
			data.put("team_turf_size", e.getTeamTurfSize());

			TournamentSystemCommons.getSocketAPI().sendEventAsync("turfwars_begin", data);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTurfWarsTurfChange(TurfWarsTurfChangeEvent e) {
		if (TournamentSystemCommons.hasSocketAPI()) {
			JSONObject data = new JSONObject();

			data.put("team1_turf", e.getTeam1Turf());
			data.put("team2_turf", e.getTeam2Turf());

			TournamentSystemCommons.getSocketAPI().sendEventAsync("turfwars_turf_change", data);
		}
	}
}

class TurfWarsTeamResult {
	private final TournamentSystemTeam team;
	private int kills;

	public TurfWarsTeamResult(TournamentSystemTeam team) {
		this.team = team;
		this.kills = 0;
	}

	public TournamentSystemTeam getTeam() {
		return team;
	}

	public int getKills() {
		return kills;
	}

	public void addKills(int kills) {
		this.kills += kills;
	}
}