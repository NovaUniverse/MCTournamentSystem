package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.hive;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.games.hive.game.Hive;
import net.novauniverse.games.hive.game.event.HiveTeamCompletedEvent;
import net.novauniverse.games.hive.game.object.hive.HiveData;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class HiveManager extends NovaModule implements Listener {
	public static final int TIME_LINE = 5;
	public static final int HONEY_LINE = 6;

	private Task task;

	public HiveManager() {
		super("TournamentSystem.GameSpecific.HiveManager");
	}

	@Override
	public void onLoad() {
		GameSetup.disableEliminationMessages();
		GameManager.getInstance().setPlayerEliminationMessage(new HiveEliminationMessages());
		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);

		ModuleManager.disable(PlayerHeadDrop.class);

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					Hive game = (Hive) GameManager.getInstance().getActiveGame();

					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LINE, ChatColor.GOLD + "Time left: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getTimeLeft()));

						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
							if (team != null) {
								HiveData hive = game.getHives().stream().filter(h -> h.getOwner().equals(team)).findFirst().orElse(null);
								if (hive != null) {
									boolean filled = hive.getHoney() >= game.getConfig().getHoneyRequiredtoFillJar();
									NetherBoardScoreboard.getInstance().setPlayerLine(HONEY_LINE, player, ChatColor.GOLD + "Honey: " + (filled ? ChatColor.GREEN : ChatColor.AQUA) + hive.getHoney() + " / " + game.getConfig().getHoneyRequiredtoFillJar());
								}
							}
						});
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
	public void onHiveTeamCompleted(HiveTeamCompletedEvent e) {
		TournamentSystemTeam team = (TournamentSystemTeam) e.getTeam();
		int score = 0;

		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if (winScore.length >= e.getPlacement()) {
			score = winScore[e.getPlacement() - 1];
		}

		if (score > 0) {
			ScoreManager.getInstance().addTeamScore(team, score);
			List<Player> members = team.getOnlinePlayers();
			int playerScore = (int) Math.floor(score / members.size());
			members.forEach(player -> ScoreManager.getInstance().addPlayerScore(player, playerScore, false));
		}
	}
}