package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bedwars;

import java.util.Comparator;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.JSONObject;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.Bedwars;
import net.novauniverse.bedwars.game.config.event.BedwarsEvent;
import net.novauniverse.bedwars.game.events.BedDestructionEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class BedwarsManager extends NovaModule implements Listener {
	public static int BEDWARS_COUNTDOWN_LINE = 5;
	public static int HAS_BED_LINE = 6;

	public static int BED_DESTRUCTION_SCORE = 0;
	private Task task;

	private Comparator<BedwarsEvent> eventSorter;

	public BedwarsManager() {
		super("TournamentSystem.GameSpecific.BedwarsManager");

		this.eventSorter = new TimeBasedBedwarsEventSorter();
	}

	@Override
	public void onLoad() {
		JSONObject scoreConfig = TournamentSystem.getInstance().getGameSpecificScoreSettings().optJSONObject("bedwars");
		if (scoreConfig != null) {
			if (scoreConfig.has("bed_destruction_score")) {
				BED_DESTRUCTION_SCORE = scoreConfig.getInt("bed_destruction_score");
				Log.info(getName(), "Setting bed destruction score to " + BED_DESTRUCTION_SCORE);
			}
		}

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bedwars game = NovaBedwars.getInstance().getGame();
				if (game.hasStarted()) {
					if (!TournamentSystem.getInstance().isDisableScoreboard()) {
						BedwarsEvent nextEvent = game.getEvents().stream().filter(u -> u.getTimeLeft() > 0).sorted(eventSorter).findFirst().orElse(null);

						if (nextEvent != null) {
							NetherBoardScoreboard.getInstance().setGlobalLine(BEDWARS_COUNTDOWN_LINE, ChatColor.GOLD + nextEvent.getName() + " in: " + ChatColor.AQUA + TextUtils.secondsToTime(nextEvent.getTimeLeft()));
						} else {
							NetherBoardScoreboard.getInstance().clearGlobalLine(BEDWARS_COUNTDOWN_LINE);
						}

						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							NetherBoardScoreboard.getInstance().setPlayerLine(HAS_BED_LINE, player, ChatColor.GOLD + "Has bed: " + (game.hasBed(player) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
						});
					}
				}
			}
		}, 10L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBedDestruction(BedDestructionEvent e) {
		if (BED_DESTRUCTION_SCORE > 0) {
			Player player = e.getPlayer();
			player.sendMessage(ChatColor.GRAY + "Enemy bed destroyed. +" + BED_DESTRUCTION_SCORE + " points");
			ScoreManager.getInstance().addPlayerScore(player, BED_DESTRUCTION_SCORE, true, "Bedwars player destroyed bed of team " + e.getOwnerTeam().getDisplayName());
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