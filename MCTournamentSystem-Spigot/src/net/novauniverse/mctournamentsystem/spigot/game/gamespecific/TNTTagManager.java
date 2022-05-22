package net.novauniverse.mctournamentsystem.spigot.game.gamespecific;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.novauniverse.games.tnttag.game.TNTTag;
import net.novauniverse.games.tnttag.game.event.PlayerKilledPlayerInTNTTagEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TNTTagManager extends NovaModule implements Listener {
	public static final int TIME_LEFT_LINE = 5;
	public static final int TAGGED_LINE = 6;

	private Task task;
	private boolean timeLeftLineShown;

	public TNTTagManager() {
		super("TournamentSystem.BingoManager");
	}

	@Override
	public void onLoad() {
		timeLeftLineShown = false;

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean didShow = false;
				if (GameManager.getInstance().hasGame()) {
					TNTTag tntTag = (TNTTag) GameManager.getInstance().getActiveGame();
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						if (!GameManager.getInstance().getActiveGame().hasEnded()) {
							long totalSecs = tntTag.getRoundTimer();

							long minutes = (totalSecs % 3600) / 60;
							long seconds = totalSecs % 60;

							ChatColor color;

							if (totalSecs > 30) {
								color = ChatColor.GREEN;
							} else if (totalSecs > 10) {
								color = ChatColor.YELLOW;
							} else {
								color = ChatColor.RED;
							}

							String timeString = String.format("%02d:%02d", minutes, seconds);

							NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LEFT_LINE, ChatColor.GOLD + "Time left: " + color + timeString);

							timeLeftLineShown = true;
							didShow = true;
						}

						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							if (tntTag.getTaggedPlayers().contains(player.getUniqueId())) {
								NetherBoardScoreboard.getInstance().setPlayerLine(TAGGED_LINE, player, ChatColor.RED + "Tagged " + TextUtils.ICON_WARNING);
							} else {
								NetherBoardScoreboard.getInstance().clearPlayerLine(TAGGED_LINE, player);
							}
						});

						if (!didShow && timeLeftLineShown) {
							NetherBoardScoreboard.getInstance().clearGlobalLine(TIME_LEFT_LINE);
							timeLeftLineShown = false;
						}
					}
				}
			}
		}, 10L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKilledPlayerInTNTTag(PlayerKilledPlayerInTNTTagEvent e) {
		if (TournamentSystem.getInstance().getScoreListener().isKillScoreEnabled()) {
			ScoreManager.getInstance().addPlayerScore(e.getKiller(), TournamentSystem.getInstance().getScoreListener().getKillScore());
			if (e.getKiller().isOnline()) {
				e.getKiller().getPlayer().sendMessage(ChatColor.GRAY + "Player killed. +" + TournamentSystem.getInstance().getScoreListener().getKillScore() + " points");
			}
		}
	}
}