package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.swordgame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.md_5.bungee.api.ChatColor;
import net.novauniverse.game.swordgame.game.SwordGame;
import net.novauniverse.game.swordgame.game.data.PlayerDataWrapper;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class SwordGameManager extends NovaModule implements Listener {
	public static int KILL_LINE = 6;
	public static int TIER_LINE = 7;
	public static int TIME_LINE = 8;

	private Task updateTask;

	private GameManager gameManager;

	public SwordGameManager() {
		super("TournamentSystem.GameSpecific.SwordGameManager");
	}

	@Override
	public void onLoad() {
		gameManager = ModuleManager.getModule(GameManager.class);

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			if (gameManager.hasGame()) {
				if (gameManager.getActiveGame() instanceof SwordGame) {
					SwordGame game = (SwordGame) gameManager.getActiveGame();
					game.tpToArena(player);
				}
			}
		});

		updateTask = new SimpleTask(getPlugin(), () -> {
			if (gameManager.hasGame()) {
				if (gameManager.getActiveGame() instanceof SwordGame) {
					SwordGame game = (SwordGame) gameManager.getActiveGame();

					int maxTier = game.getTierProvider().getTiers().size();

					if (game.hasStarted()) {
						DelayedGameTrigger endTrigger = game.getGameEndTrigger();

						if (endTrigger.isRunning()) {
							NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LINE, ChatColor.GOLD + "Game ends in: " + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(endTrigger.getTicksLeft() / 20));
						}

						final List<SwordGameTeamResult> teamScore = getTeamResults();

						Bukkit.getOnlinePlayers().forEach(player -> {
							AtomicInteger kills = new AtomicInteger(0);
							AtomicInteger tier = new AtomicInteger(0);
							if (game.isPlayerInGame(player)) {
								PlayerDataWrapper data = game.getPlayerData(player);
								tier.set(data.getTier());

								TeamManager.getTeamManager().ifHasTeam(player, team -> {
									teamScore.stream().filter(t -> t.getTeam().equals(team)).findFirst().ifPresent(teamData -> {
										kills.set(teamData.getKills());
									});
								});
							}

							NetherBoardScoreboard.getInstance().setPlayerLine(TIER_LINE, player, ChatColor.GOLD + "Tier: " + ChatColor.AQUA + tier.get() + " / " + maxTier);
							NetherBoardScoreboard.getInstance().setPlayerLine(KILL_LINE, player, ChatColor.GOLD + "Sword Game Kills: " + ChatColor.AQUA + kills.get());
						});
					}
				}
			}
		}, 10L);
	}

	public List<SwordGameTeamResult> getTeamResults() {
		List<SwordGameTeamResult> result = new ArrayList<>();
		TeamManager.getTeamManager().stream().forEach(team -> {
			result.add(new SwordGameTeamResult(team, team.getMembers().stream().mapToInt(member -> {
				PlayerDataWrapper data = ((SwordGame) gameManager.getActiveGame()).getPlayerData(member);
				return data.getKills();
			}).sum()));
		});
		return result;
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(updateTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGameEnd(GameEndEvent e) {
		final List<SwordGameTeamResult> teamScore = getTeamResults();
		Collections.sort(teamScore, Comparator.comparing(SwordGameTeamResult::getKills).reversed());
		
		Bukkit.broadcastMessage(ChatColor.AQUA + ChatColor.BOLD.toString()+ "----- Top teams -----");
	}

	public class SwordGameTeamResult {
		protected final Team team;
		protected final int kills;

		public SwordGameTeamResult(Team team, int kills) {
			this.team = team;
			this.kills = kills;
		}

		public Team getTeam() {
			return team;
		}

		public int getKills() {
			return kills;
		}
	}
}