package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.tnttag;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.games.tnttag.game.TNTTag;
import net.novauniverse.games.tnttag.game.event.PlayerKilledPlayerInTNTTagEvent;
import net.novauniverse.games.tnttag.game.event.TNTTagCountdownEvent;
import net.novauniverse.games.tnttag.game.event.TNTTagPlayerTaggedEvent;
import net.novauniverse.games.tnttag.game.event.TNTTagRoundEndEvent;
import net.novauniverse.games.tnttag.game.event.TNTTagRoundStartEvent;
import net.novauniverse.mctournamentsystem.commons.socketapi.SocketAPI;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TNTTagManager extends NovaModule implements Listener {
	public static int TIME_LEFT_LINE = 5;
	public static int TAGGED_LINE = 6;

	private Task task;
	private boolean timeLeftLineShown;

	private GameManager gameManager;

	public TNTTagManager() {
		super("TournamentSystem.GameSpecific.TNTTagManager");
	}

	@Override
	public void onLoad() {
		gameManager = GameManager.getInstance();
		timeLeftLineShown = false;

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean didShow = false;
				if (gameManager.hasGame()) {
					TNTTag tntTag = (TNTTag) gameManager.getActiveGame();
					if (tntTag.hasStarted()) {
						if (!TournamentSystem.getInstance().isDisableScoreboard()) {
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

								NovaScoreboardManager.getInstance().setGlobalLine(TIME_LEFT_LINE, new StaticTextLine(ChatColor.GOLD + "Time left: " + color + timeString));

								timeLeftLineShown = true;
								didShow = true;
							}

							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								if (tntTag.getTaggedPlayers().contains(player.getUniqueId())) {
									NovaScoreboardManager.getInstance().setPlayerLine(player, TAGGED_LINE, new StaticTextLine(ChatColor.RED + "Tagged " + TextUtils.ICON_WARNING));
								} else {
									NovaScoreboardManager.getInstance().clearPlayerLine(player, TAGGED_LINE);
								}
							});

							if (!didShow && timeLeftLineShown) {
								NovaScoreboardManager.getInstance().clearGlobalLine(TIME_LEFT_LINE);
								timeLeftLineShown = false;
							}
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
			ScoreManager.getInstance().addPlayerScore(e.getKiller(), TournamentSystem.getInstance().getScoreListener().getKillScore(), "TNT Tag kill score. " + e.getKiller().getName() + " killed " + e.getKilledPlayer().getName());
			if (e.getKiller().isOnline()) {
				e.getKiller().getPlayer().sendMessage(ChatColor.GRAY + "Player killed. +" + TournamentSystem.getInstance().getScoreListener().getKillScore() + " points");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTNTTagCountdown(TNTTagCountdownEvent e) {
		if (SocketAPI.isAvailable()) {
			JSONObject data = new JSONObject();
			data.put("time", e.getSecondsLeft());
			SocketAPI.trySendAsync("tnttag_time_left", data);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTNTTagRoundStart(TNTTagRoundStartEvent e) {
		if (SocketAPI.isAvailable()) {
			JSONObject data = new JSONObject();
			JSONArray taggedPlayers = new JSONArray();
			e.getTaggedPlayers().forEach(p -> {
				JSONObject player = new JSONObject();
				player.put("uuid", p.getUniqueId().toString());
				player.put("name", p.getName());
				taggedPlayers.put(player);
			});
			data.put("time", e.getRoundTime());
			data.put("tagged_players", taggedPlayers);
			SocketAPI.trySendAsync("tnttag_round_start", data);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTNTTagRoundEnd(TNTTagRoundEndEvent e) {
		if (SocketAPI.isAvailable()) {
			JSONObject data = new JSONObject();
			JSONArray eliminatedPlayers = new JSONArray();
			e.getEliminatedPlayers().forEach(uuid -> eliminatedPlayers.put(uuid.toString()));
			data.put("eliminated_players", eliminatedPlayers);
			SocketAPI.trySendAsync("tnttag_round_end", data);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTNTTagPlayerTagged(TNTTagPlayerTaggedEvent e) {
		if (SocketAPI.isAvailable()) {
			JSONObject data = new JSONObject();
			JSONObject player = new JSONObject();
			player.put("uuid", e.getTaggedPlayer().getUniqueId().toString());
			player.put("name", e.getTaggedPlayer().getName());

			data.put("player", player);
			if (e.getAttacker() != null) {
				JSONObject attacker = new JSONObject();
				attacker.put("uuid", e.getAttacker().getUniqueId().toString());
				attacker.put("name", e.getAttacker().getName());
				data.put("attacker", attacker);
			}
			SocketAPI.trySendAsync("tnttag_player_tagged", data);
		}
	}
}