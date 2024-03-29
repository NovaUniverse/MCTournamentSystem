package net.novauniverse.mctournamentsystem.spigot.score;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerWinEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamEliminatedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.utils.ProjectileUtils;

public class ScoreListener implements Listener {
	private boolean killScoreEnabled;
	private int killScore;

	private boolean winScoreEnabled;
	private int[] winScore;

	private boolean participationScoreEnabled;
	private int participationScore;

	public ScoreListener(boolean killScoreEnabled, int killScore, boolean winScoreEnabled, int[] winScore, boolean participationScoreEnabled, int participationScore) {
		this.killScoreEnabled = killScoreEnabled;
		this.killScore = killScore;

		this.winScoreEnabled = winScoreEnabled;
		this.winScore = winScore;

		this.participationScoreEnabled = participationScoreEnabled;
		this.participationScore = participationScore;

		Log.info("Kill score: " + this.killScoreEnabled + " Win score: " + this.winScoreEnabled + " Participation score: " + participationScoreEnabled);
	}

	public boolean isKillScoreEnabled() {
		return killScoreEnabled;
	}

	public boolean isWinScoreEnabled() {
		return winScoreEnabled;
	}

	public boolean isParticipationScoreEnabled() {
		return participationScoreEnabled;
	}

	public int getKillScore() {
		return killScore;
	}

	public int[] getWinScore() {
		return winScore;
	}

	public int getParticipationScore() {
		return participationScore;
	}

	public void setKillScore(int killScore) {
		this.killScore = killScore;
	}

	public void setWinScore(int[] winScore) {
		this.winScore = winScore;
	}

	public void setParticipationScore(int participationScore) {
		this.participationScore = participationScore;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (TournamentSystem.getInstance().isBuiltInScoreSystemDisabled()) {
			return;
		}

		Log.trace("ScoreListener", "PlayerEliminatedEvent. " + e.getPlayer().getUniqueId());
		if (participationScoreEnabled) {
			if (GameManager.getInstance().hasGame()) {
				if (GameManager.getInstance().getActiveGame().hasStarted()) {
					GameManager.getInstance().getActiveGame().getPlayers().forEach(uuid -> {
						Player player = Bukkit.getServer().getPlayer(uuid);
						if (player != null) {
							if (player.isOnline()) {
								ScoreManager.getInstance().addPlayerScore(player, participationScore, true, "Participation score " + player.getName());
								player.sendMessage(ChatColor.GRAY + "+" + participationScore + " Participation score");
							}
						}
					});
				}
			}
		}

		if (e.getPlayer().isOnline()) {
			Entity killer = e.getKiller();

			Player killerPlayer = null;

			if (ProjectileUtils.isProjectile(killer)) {
				Entity shooter = ProjectileUtils.getProjectileShooterEntity(killer);

				if (shooter != null) {
					if (shooter instanceof Player) {
						killerPlayer = (Player) shooter;
					}
				}
			} else if (killer instanceof Player) {
				killerPlayer = (Player) killer;
			}

			if (TournamentSystem.getInstance().isAddXpLevelOnKill()) {
				if (killerPlayer != null) {
					if (!killerPlayer.isDead()) {
						killerPlayer.setLevel(killerPlayer.getLevel() + 1);
					}
				}
			}

			if (killScoreEnabled) {
				if (killerPlayer != null) {
					ScoreManager.getInstance().addPlayerScore(killerPlayer, killScore, true, "Kill score " + killerPlayer.getName() + " eliminated " + e.getPlayer().getName());
					PlayerKillCache.getInstance().invalidate(killerPlayer);
				}
			}
		}

		if (winScoreEnabled) {
			if (e.getPlacement() > 1) {
				addPlayerPlacementScore(e.getPlayer(), e.getPlacement());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeamEliminated(TeamEliminatedEvent e) {
		if (TournamentSystem.getInstance().isBuiltInScoreSystemDisabled()) {
			return;
		}

		Log.trace("ScoreListener", "TeamEliminatedEvent. " + e.getTeam().getTeamUuid());
		if (winScoreEnabled) {
			if (e.getPlacement() > 1) {
				addTeamPlacementScore(e.getTeam(), e.getPlacement());
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onTeamWin(TeamWinEvent e) {
		if (TournamentSystem.getInstance().isBuiltInScoreSystemDisabled()) {
			return;
		}

		Log.trace("ScoreListener", "TeamWinEvent called");
		addTeamPlacementScore(e.getTeam(), 1);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerWin(PlayerWinEvent e) {
		if (TournamentSystem.getInstance().isBuiltInScoreSystemDisabled()) {
			return;
		}

		Log.trace("ScoreListener", "PlayerWinEvent called");
		addPlayerPlacementScore(e.getPlayer(), 1);
	}

	private void addPlayerPlacementScore(OfflinePlayer player, int placement) {
		Log.trace("ScoreListener", "ScoreListener.addPlayerPlacementScore()");
		if (!GameManager.getInstance().isUseTeams()) {
			if (placement <= winScore.length) {
				int score = winScore[placement - 1];

				ScoreManager.getInstance().addPlayerScore(player, score, false, "Player placement score");
				if (player.isOnline()) {
					((Player) player).sendMessage(ChatColor.GRAY + "+" + score + " score");
				}
			}
		}
	}

	private void addTeamPlacementScore(Team team, int placement) {
		Log.trace("ScoreListener", "ScoreListener.addTeamPlacementScore()");
		if (placement <= winScore.length) {
			int score = winScore[placement - 1];

			double individualScore = Math.ceil(((double) score) / ((double) team.getMembers().size()));

			team.getMembers().forEach(uuid -> ScoreManager.getInstance().addPlayerScore(uuid, (int) individualScore, false, "Team placement split to players at " + TextUtils.ordinal(placement) + " place. total: " + score));

			ScoreManager.getInstance().addTeamScore((TournamentSystemTeam) team, score, "Team placement " + TextUtils.ordinal(placement) + " place. total: " + score);

			team.sendMessage(ChatColor.GRAY + "+" + score + " score (Shared with team members)");
		}
	}
}