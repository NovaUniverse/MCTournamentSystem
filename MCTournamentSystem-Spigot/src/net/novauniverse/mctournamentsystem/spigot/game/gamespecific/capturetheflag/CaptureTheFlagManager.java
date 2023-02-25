package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.capturetheflag;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.capturetheflag.NovaCaptureTheFlag;
import net.novauniverse.capturetheflag.game.CaptureTheFlag;
import net.novauniverse.capturetheflag.game.event.FlagCapturedEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class CaptureTheFlagManager extends NovaModule implements Listener {
	public static int SUDDEN_DEATH_COUNTDOWN_LINE = 5;
	public static int FLAG_CAPTURE_SCORE = 20;
	public static int ELIMINATE_PLAYER_SCORE = 20;

	private Task task;

	public CaptureTheFlagManager() {
		super("TournamentSystem.GameSpecific.CaptureTheFlagManager");
	}

	@Override
	public void onLoad() {
		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				CaptureTheFlag game = NovaCaptureTheFlag.getInstance().getGame();
				if (game.hasStarted()) {
					if (!TournamentSystem.getInstance().isDisableScoreboard()) {
						if (!game.isSuddenDeathActive()) {
							if (game.getSuddenDeathTask() != null) {
								int secondsLeft = (int) (game.getSuddenDeathTask().getMSLeft() / 1000);
								NetherBoardScoreboard.getInstance().setGlobalLine(SUDDEN_DEATH_COUNTDOWN_LINE, ChatColor.GOLD + "Sudden death: " + ChatColor.AQUA + TextUtils.secondsToTime(secondsLeft));
							}
						} else {
							NetherBoardScoreboard.getInstance().clearGlobalLine(SUDDEN_DEATH_COUNTDOWN_LINE);
						}
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFlagCaptured(FlagCapturedEvent e) {
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Flag Captured> " + e.getCarrierTeam().getTeam().getTeamColor() + ChatColor.BOLD + e.getCarrier().getName() + ChatColor.GOLD + ChatColor.BOLD + " captured the flag of team " + e.getFlag().getTeam().getTeam().getTeamColor() + ChatColor.BOLD + e.getFlag().getTeam().getTeam().getDisplayName());
		e.getCarrier().sendMessage(ChatColor.GRAY + "Flag captured. +" + FLAG_CAPTURE_SCORE + " points");
		ScoreManager.getInstance().addPlayerScore(e.getCarrier(), FLAG_CAPTURE_SCORE, true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (e.getKiller() != null) {
			if (e.getKiller() instanceof Player) {
				Player killer = (Player) e.getKiller();
				killer.sendMessage(ChatColor.GRAY + "Player eliminated. +" + ELIMINATE_PLAYER_SCORE + " points");
				ScoreManager.getInstance().addPlayerScore(killer, ELIMINATE_PLAYER_SCORE, true);
			}
		}
	}
}