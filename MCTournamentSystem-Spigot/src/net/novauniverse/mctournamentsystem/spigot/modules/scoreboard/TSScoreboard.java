package net.novauniverse.mctournamentsystem.spigot.modules.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
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
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = true)
public class TSScoreboard extends NovaModule implements Listener {
	private int taskId;

	private boolean gameCountdownShown;
	private boolean borderCountdownShown;

	public static final int COUNTDOWN_LINE = 6;
	public static final int WORLDBORDER_LINE = 7;

	public TSScoreboard() {
		super("TournamentSystem.Scoreboard");
	}

	@Override
	public void onLoad() {
		this.taskId = -1;
		this.gameCountdownShown = false;
		this.borderCountdownShown = false;
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {

				@Override
				public void run() {
					final double[] recentTps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps();

					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						int playerScore = ScoreManager.getInstance().getPlayerScore(player);
						int teamScore = 0;

						TournamentSystemTeam team = null;

						if (TeamManager.hasTeamManager()) {
							team = (TournamentSystemTeam) TournamentSystemTeamManager.getInstance().getPlayerTeam(player);
						}

						if (team != null) {
							teamScore = team.getScore();
						}

						NetherBoardScoreboard.getInstance().setPlayerLine(2, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.score") + ChatColor.AQUA + playerScore);
						NetherBoardScoreboard.getInstance().setPlayerLine(3, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.kills") + ChatColor.AQUA + PlayerKillCache.getInstance().getPlayerKills(player.getUniqueId()));
						NetherBoardScoreboard.getInstance().setPlayerLine(4, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.team_score") + ChatColor.AQUA + teamScore);

						int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);

						NetherBoardScoreboard.getInstance().setPlayerLine(12, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.your_ping") + formatPing(ping) + "ms " + (ping > 800 ? ChatColor.YELLOW + TextUtils.ICON_WARNING : ""));

						if (recentTps.length > 0) {
							double tps = recentTps[0];
							NetherBoardScoreboard.getInstance().setPlayerLine(13, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.average_tps") + formatTps(tps) + (tps < 18 ? " " + ChatColor.RED + TextUtils.ICON_WARNING : ""));
						}
					});

					if (NovaCore.isNovaGameEngineEnabled()) {
						if (GameManager.getInstance().hasGame()) {
							if (GameManager.getInstance().getActiveGame().hasStarted()) {
								if (GameManager.getInstance().getActiveGame() instanceof MapGame) {
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
														NetherBoardScoreboard.getInstance().setGlobalLine(WORLDBORDER_LINE, ChatColor.GOLD + "Worldborder: " + ChatColor.AQUA + formatTime(ticks / 20));
													} else {
														if (borderCountdownShown) {
															borderCountdownShown = false;
															NetherBoardScoreboard.getInstance().clearGlobalLine(WORLDBORDER_LINE);
														}
													}
												}
											}
										}
									}
								}
							}
						}

						if (GameManager.getInstance().getCountdown().isCountdownRunning()) {
							gameCountdownShown = true;

							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								NetherBoardScoreboard.getInstance().setPlayerLine(COUNTDOWN_LINE, player, ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.starting_in") + ChatColor.AQUA + TextUtils.secondsToHoursMinutes(GameManager.getInstance().getCountdown().getTimeLeft()));
							});
						} else {
							if (gameCountdownShown) {
								gameCountdownShown = false;
								Bukkit.getServer().getOnlinePlayers().forEach(player -> NetherBoardScoreboard.getInstance().clearPlayerLine(COUNTDOWN_LINE, player));
							}
						}
					}
				}
			}, 10L, 10L);
		}
	}

	private static String formatTime(long seconds) {
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

	private String formatPing(int ping) {
		ChatColor color = ChatColor.DARK_RED;

		if (ping < 200) {
			color = ChatColor.GREEN;
		} else if (ping < 400) {
			color = ChatColor.DARK_GREEN;
		} else if (ping < 600) {
			color = ChatColor.YELLOW;
		} else if (ping < 800) {
			color = ChatColor.RED;
		}

		return color + "" + ping;
	}

	private String formatTps(double tps) {
		return ((tps > 18.0) ? ChatColor.GREEN : (tps > 16.0) ? ChatColor.YELLOW : ChatColor.RED).toString() + ((tps > 20.0) ? "*" : "") + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
	}
}
