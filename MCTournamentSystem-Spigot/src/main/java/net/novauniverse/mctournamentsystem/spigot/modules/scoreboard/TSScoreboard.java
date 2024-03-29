package net.novauniverse.mctournamentsystem.spigot.modules.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import me.lucko.spark.api.Spark;
import me.lucko.spark.api.SparkProvider;
import me.lucko.spark.api.statistic.StatisticWindow.TicksPerSecond;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.modules.nextminigame.NextMinigameManager;
import net.novauniverse.mctournamentsystem.spigot.modules.scoreboard.provider.ScoreboardStatsTextProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.scoreboard.provider.implementation.DefaultTournamentSystemScoreboardStatsTextProvider;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.novauniverse.mctournamentsystem.spigot.utils.TournamentUtils;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.MapGame;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.GameMapData;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.map.mapmodules.worldborder.WorldborderMapModule;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = true)
public class TSScoreboard extends NovaModule implements Listener {
	public static int COUNTDOWN_LINE = 6;
	public static int WORLDBORDER_LINE = 7;
	public static int NEXT_GAME_LINE = 11;

	public static int PLAYER_TEAM_LINE = 1;
	public static int PLAYER_SCORE_LINE = 2;
	public static int PLAYER_KILLS_LINE = 3;
	public static int PLAYER_TEAM_SCORE_LINE = 4;

	public static int PING_LINE = 12;
	public static int TPS_LINE = 13;

	public static String WORLDBORDER_LINE_COLOR = ChatColor.GOLD.toString();

	private int taskId;

	private boolean gameCountdownShown;
	private boolean borderCountdownShown;
	private boolean nextGameShown;

	private boolean disablePing;
	private boolean disableTPS;

	private boolean minimalMode;

	private boolean useSpark;

	private ScoreboardStatsTextProvider statsTextProvider;

	private GameManager gameManager;

	private Spark spark;

	public TSScoreboard() {
		super("TournamentSystem.Scoreboard");
	}

	public void setUseSpark(boolean useSpark) {
		this.useSpark = useSpark;
	}

	public boolean isUseSpark() {
		return useSpark;
	}

	public ScoreboardStatsTextProvider getStatsTextProvider() {
		return statsTextProvider;
	}

	public void setStatsTextProvider(ScoreboardStatsTextProvider statsTextProvider) {
		this.statsTextProvider = statsTextProvider;
	}

	public boolean isDisablePing() {
		return disablePing;
	}

	public boolean isDisableTPS() {
		return disableTPS;
	}

	public void setDisablePing(boolean disablePing) {
		this.disablePing = disablePing;
	}

	public void setDisableTPS(boolean disableTPS) {
		this.disableTPS = disableTPS;
	}

	@Override
	public void onLoad() {
		this.statsTextProvider = new DefaultTournamentSystemScoreboardStatsTextProvider();

		this.taskId = -1;
		this.gameCountdownShown = false;
		this.borderCountdownShown = false;
		this.nextGameShown = false;
		this.minimalMode = false;

		this.disablePing = false;
		this.disableTPS = false;

		this.useSpark = true;

		spark = SparkProvider.get();

		this.gameManager = null;
		if (NovaCore.isNovaGameEngineEnabled()) {
			this.gameManager = GameManager.getInstance();
		}
	}

	public void setMinimalMode(boolean minimalMode) {
		this.minimalMode = minimalMode;
	}

	public boolean isMinimalMode() {
		return minimalMode;
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					if (TournamentSystem.getInstance().isDisableScoreboard()) {
						return;
					}

					double lastTps = 0;
					if (!disableTPS) {
						if (useSpark) {
							lastTps = spark.tps().poll(TicksPerSecond.SECONDS_5);
						} else {
							lastTps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps()[0];
						}
					}

					final double tps = lastTps;

					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						if (!minimalMode) {
							int playerScore = ScoreManager.getInstance().getPlayerScore(player);
							int teamScore = 0;

							TournamentSystemTeam team = null;

							if (TeamManager.hasTeamManager()) {
								team = (TournamentSystemTeam) TournamentSystemTeamManager.getInstance().getPlayerTeam(player);
							}

							if (team != null) {
								teamScore = team.getScore();
							}

							if (PLAYER_SCORE_LINE >= 0) {
								NovaScoreboardManager.getInstance().setPlayerLine(player, PLAYER_SCORE_LINE, new StaticTextLine(statsTextProvider.getPlayerScoreLineContent(player, playerScore)));
							}

							if (PLAYER_KILLS_LINE >= 0) {
								NovaScoreboardManager.getInstance().setPlayerLine(player, PLAYER_KILLS_LINE, new StaticTextLine(statsTextProvider.getKillLineContent(player, PlayerKillCache.getInstance().getPlayerKills(player.getUniqueId()))));
							}

							if (PLAYER_TEAM_SCORE_LINE >= 0) {
								NovaScoreboardManager.getInstance().setPlayerLine(player, PLAYER_TEAM_SCORE_LINE, new StaticTextLine(statsTextProvider.getTeamScoreLineContent(player, team, teamScore)));
							}

							String teamName = "";
							net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.YELLOW;
							if (team == null) {
								teamName = ChatColor.YELLOW + (TournamentSystem.getInstance().isMakeTeamNamesBold() ? ChatColor.BOLD + "" : "") + "No team";
							} else {
								color = team.getTeamColor();
								teamName = color + (TournamentSystem.getInstance().isMakeTeamNamesBold() ? ChatColor.BOLD + "" : "") + team.getDisplayName();
							}

							if (PLAYER_TEAM_LINE >= 0) {
								NovaScoreboardManager.getInstance().setPlayerLine(player, 1, new StaticTextLine(teamName));
							}
						}

						if (!disablePing) {
							int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);

							NovaScoreboardManager.getInstance().setPlayerLine(player, PING_LINE, new StaticTextLine(ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.your_ping") + TournamentUtils.formatPing(ping) + "ms " + (ping > 800 ? ChatColor.YELLOW + TextUtils.ICON_WARNING : "")));
						}

						if (!disableTPS) {
							NovaScoreboardManager.getInstance().setPlayerLine(player, TPS_LINE, new StaticTextLine(ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.average_tps") + TournamentUtils.formatTps(tps) + (tps < 18 ? " " + ChatColor.RED + TextUtils.ICON_WARNING : "")));
						}
					});

					String nextGame = NextMinigameManager.getInstance().getNextMinigame();
					if (nextGame == null) {
						if (nextGameShown) {
							NovaScoreboardManager.getInstance().clearGlobalLine(NEXT_GAME_LINE);
							nextGameShown = false;
						}
					} else {
						nextGameShown = true;
						nextGame = ChatColor.GOLD + "Next game: " + ChatColor.AQUA + nextGame;
						if (nextGame.length() > 40) {
							nextGame = nextGame.substring(0, 40);
						}
						NovaScoreboardManager.getInstance().setGlobalLine(NEXT_GAME_LINE, new StaticTextLine(nextGame));
					}

					if (gameManager != null) {
						if (gameManager.hasGame()) {
							if (gameManager.getActiveGame().hasStarted()) {
								if (gameManager.getActiveGame() instanceof MapGame) {
									MapGame game = (MapGame) GameManager.getInstance().getActiveGame();
									if (game.hasActiveMap()) {
										if (game.getActiveMap().getAbstractMapData() instanceof GameMapData) {
											GameMapData mapData = (GameMapData) game.getActiveMap().getAbstractMapData();
											if (mapData.hasMapModule(WorldborderMapModule.class)) {
												WorldborderMapModule borderModule = (WorldborderMapModule) mapData.getMapModule(WorldborderMapModule.class);
												if (borderModule != null) {
													if (borderModule.getStartTrigger().isRunning()) {
														DelayedGameTrigger trigger = borderModule.getStartTrigger();
														long ticks = trigger.getTicksLeft();

														borderCountdownShown = true;
														NovaScoreboardManager.getInstance().setGlobalLine(WORLDBORDER_LINE, new StaticTextLine(WORLDBORDER_LINE_COLOR + "Worldborder: " + ChatColor.WHITE + formatTime(ticks / 20)));
													} else {
														if (borderCountdownShown) {
															borderCountdownShown = false;
															NovaScoreboardManager.getInstance().clearGlobalLine(WORLDBORDER_LINE);
														}
													}
												}
											}
										}
									}
								}
							}
						}

						if (gameManager.getCountdown().isCountdownRunning()) {
							gameCountdownShown = true;

							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								NovaScoreboardManager.getInstance().setPlayerLine(player, COUNTDOWN_LINE, new StaticTextLine(ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.starting_in") + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(GameManager.getInstance().getCountdown().getTimeLeft())));
							});
						} else {
							if (gameCountdownShown) {
								gameCountdownShown = false;
								Bukkit.getServer().getOnlinePlayers().forEach(player -> NovaScoreboardManager.getInstance().clearPlayerLine(player, COUNTDOWN_LINE));
							}
						}
					}
				}
			}, 10L, 10L);
		}

	}

	public static String formatTime(long seconds) {
		int h = (int) (seconds / 3600);
		int m = (int) ((seconds % 3600) / 60);
		int s = (int) (seconds % 60);

		String time = "";

		if (h > 0) {
			time += h + ":";
		}

		if (m == 0) {
			time += "00:";
		} else {
			time += (m >= 10 ? m : "0" + m) + ":";
		}

		if (s == 0) {
			time += "00";
		} else {
			time += (s >= 10 ? s : "0" + s);
		}

		return time;
	}

	@Override
	public void onDisable() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}
}
