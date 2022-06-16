package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bingo;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.novauniverse.games.bingo.NovaBingo;
import net.novauniverse.games.bingo.game.Bingo;
import net.novauniverse.games.bingo.game.event.BingoPlayerFindItemEvent;
import net.novauniverse.games.bingo.game.event.BingoTeamCompleteEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class BingoManager extends NovaModule implements Listener {
	public static final int POINTS_PER_ITEM = 10;
	public static final int TIME_LEFT_LINE = 5;

	private Task task;
	private boolean timeLeftLineShown;

	public BingoManager() {
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
					if (GameManager.getInstance().getActiveGame().hasStarted() && !GameManager.getInstance().getActiveGame().hasEnded()) {
						long totalSecs = ((Bingo) GameManager.getInstance().getActiveGame()).getTimeLeft();

						long hours = totalSecs / 3600;
						long minutes = (totalSecs % 3600) / 60;
						long seconds = totalSecs % 60;

						ChatColor color;

						if (totalSecs > (NovaBingo.getInstance().getGameDuration() * 60) / 2) {
							color = ChatColor.GREEN;
						} else if (totalSecs > (NovaBingo.getInstance().getGameDuration() * 60) / 4) {
							color = ChatColor.YELLOW;
						} else {
							color = ChatColor.RED;
						}

						String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

						NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LEFT_LINE, ChatColor.GOLD + "Time left: " + color + timeString);

						timeLeftLineShown = true;
						didShow = true;
					}
				}

				if (!didShow && timeLeftLineShown) {
					NetherBoardScoreboard.getInstance().clearGlobalLine(TIME_LEFT_LINE);
					timeLeftLineShown = false;
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

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setFireTicks(0);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBingoPlayerFindItem(BingoPlayerFindItemEvent e) {

		e.getTeam().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + e.getPlayer().getName() + " found item " + ChatColor.AQUA + ChatColor.BOLD + e.getItemDisplayName());

		e.getTeam().sendMessage(ChatColor.GRAY + "+" + POINTS_PER_ITEM + " points");
		ScoreManager.getInstance().addPlayerScore(e.getPlayer(), POINTS_PER_ITEM, true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onBingoTeamComplete(BingoTeamCompleteEvent e) {
		if (!(e.getTeam() instanceof TournamentSystemTeam)) {
			Log.fatal("BingoManager", "Team is not of type TournamentSystemTeam. Detected type: " + e.getTeam().getClass().getName());
			return;
		}

		int score = TournamentSystem.getInstance().getWinScore()[TournamentSystem.getInstance().getWinScore().length - 1];

		if (e.getPlacement() <= TournamentSystem.getInstance().getWinScore().length) {
			score = TournamentSystem.getInstance().getWinScore()[e.getPlacement() - 1];
		}

		e.getTeam().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "All items found");

		Bukkit.getServer().broadcastMessage(e.getTeam().getTeamColor() + "" + ChatColor.BOLD + e.getTeam().getDisplayName() + ChatColor.GREEN + ChatColor.BOLD + " has found all items. " + TextUtils.ordinal(e.getPlacement()) + " place");

		e.getTeam().sendMessage(ChatColor.GRAY + "+" + score + " points");
		ScoreManager.getInstance().addTeamScore((TournamentSystemTeam) e.getTeam(), score);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onGameEnd(GameEndEvent e) {
		if (e.getReason() == GameEndReason.TIME) {
			int teamsLeft = NovaBingo.getInstance().getGame().getTeamsLeft();

			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Time is up. " + teamsLeft + " team" + (teamsLeft == 1 ? "" : "s") + " did not finish");
		} else if (e.getReason() == GameEndReason.ALL_FINISHED) {
			Bukkit.getServer().broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Game over. all teams finished in time");
		}
	}
}