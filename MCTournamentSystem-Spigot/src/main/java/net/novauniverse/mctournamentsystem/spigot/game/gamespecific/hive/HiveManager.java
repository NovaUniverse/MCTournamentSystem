package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.hive;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.hive.NovaHive;
import net.novauniverse.games.hive.game.Hive;
import net.novauniverse.games.hive.game.event.HivePlayerDepositHoneyEvent;
import net.novauniverse.games.hive.game.event.HiveTeamCompletedEvent;
import net.novauniverse.games.hive.game.object.hive.HiveData;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.kills.KillManager;
import net.novauniverse.mctournamentsystem.spigot.modules.head.EdibleHeads;
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
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class HiveManager extends NovaModule implements Listener {
	public static int TIME_LINE = 5;
	public static int HONEY_LINE = 6;

	public static double HONEY_SCORE_MULTIPLIER = 0D;

	public static String LINE_PREFIX = ChatColor.GOLD.toString();

	private Task task;

	private GameManager gameManager;

	public HiveManager() {
		super("TournamentSystem.GameSpecific.HiveManager");
	}

	@Override
	public void onLoad() {
		JSONObject scoreConfig = TournamentSystem.getInstance().getGameSpecificScoreSettings().optJSONObject("hive");
		if (scoreConfig != null) {
			if (scoreConfig.has("honey_score_multiplier")) {
				HONEY_SCORE_MULTIPLIER = scoreConfig.getDouble("honey_score_multiplier");
				Log.info(getName(), "Setting honey score multiplier " + HONEY_SCORE_MULTIPLIER);
			}
		}

		gameManager = GameManager.getInstance();

		GameSetup.disableEliminationMessages();
		gameManager.setPlayerEliminationMessage(new HiveEliminationMessages());
		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);
		TournamentSystem.getInstance().disableEliminationTitleMessage();

		ModuleManager.disable(PlayerHeadDrop.class);

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			new BukkitRunnable() {
				@Override
				public void run() {
					NovaHive.getInstance().getGame().spawnPlayer(player);
				}
			}.runTaskLater(getPlugin(), 2L);
		});

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (gameManager.hasGame()) {
					Hive game = (Hive) gameManager.getActiveGame();

					if (!TournamentSystem.getInstance().isDisableScoreboard()) {
						if (game.hasStarted()) {
							NovaScoreboardManager.getInstance().setGlobalLine(TIME_LINE, new StaticTextLine(LINE_PREFIX + "Time left: " + ChatColor.WHITE + TextUtils.secondsToTime(game.getTimeLeft() + 1)));

							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
								if (team != null) {
									HiveData hive = game.getHives().stream().filter(h -> h.getOwner().equals(team)).findFirst().orElse(null);
									if (hive != null) {
										boolean filled = hive.getHoney() >= game.getConfig().getHoneyRequiredtoFillJar();
										NovaScoreboardManager.getInstance().setPlayerLine(player, HONEY_LINE, new StaticTextLine(LINE_PREFIX + "Honey: " + (filled ? ChatColor.GREEN : ChatColor.WHITE) + hive.getHoney() + " / " + game.getConfig().getHoneyRequiredtoFillJar()));
									}
								}
							});
						}
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);

		ModuleManager.disable(CompassTracker.class);
		ModuleManager.disable(EdibleHeads.class);
		ModuleManager.disable(PlayerHeadDrop.class);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (!NovaHive.getInstance().getGame().hasEnded() && NovaHive.getInstance().getGame().hasStarted()) {
			Player killer = e.getEntity().getKiller();
			if (killer != null) {
				if (NovaHive.getInstance().getGame().getPlayers().contains(killer.getUniqueId())) {
					KillManager.addPlayerKill(killer);

					TeamManager.getTeamManager().ifHasTeam(killer, (team -> KillManager.addTeamKill((TournamentSystemTeam) team)));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (e.getPlayer().isOnline()) {
			Player player = e.getPlayer().getPlayer();
			VersionIndependentUtils.get().sendTitle(player, ChatColor.RED + "Eliminated", "", 10, 60, 10);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHivePlayerDepositHoney(HivePlayerDepositHoneyEvent e) {
		Player player = e.getPlayer();
		if (HONEY_SCORE_MULTIPLIER > 0) {
			int score = (int) Math.ceil(((double) e.getAmount()) * HONEY_SCORE_MULTIPLIER);

			ScoreManager.getInstance().addPlayerScore(player, score, true, "Hive " + e.getAmount() + " honey deposited");
			player.sendMessage(ChatColor.GRAY + "+" + score + " points");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHiveTeamCompleted(HiveTeamCompletedEvent e) {
		TournamentSystemTeam team = (TournamentSystemTeam) e.getTeam();
		int score = 0;

		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if (winScore.length >= e.getPlacement()) {
			score = winScore[e.getPlacement() - 1];
		}

		if (score > 0) {
			team.sendMessage(ChatColor.GRAY + "+" + score + " points");
			ScoreManager.getInstance().addTeamScore(team, score, "Hive completion at " + TextUtils.ordinal(e.getPlacement()) + " place");
			List<Player> members = team.getOnlinePlayers();
			int playerScore = (int) Math.floor(score / members.size());
			members.forEach(player -> ScoreManager.getInstance().addPlayerScore(player, playerScore, false, "Hive completion distrubution at " + TextUtils.ordinal(e.getPlacement()) + " place"));
		}
	}
}