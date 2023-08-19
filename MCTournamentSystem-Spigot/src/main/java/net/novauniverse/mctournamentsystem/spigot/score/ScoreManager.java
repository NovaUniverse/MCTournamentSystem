package net.novauniverse.mctournamentsystem.spigot.score;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.EssentialModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = true)
@EssentialModule
public class ScoreManager extends NovaModule implements Listener {
	private static ScoreManager instance;
	private HashMap<UUID, Integer> playerScoreCache;

	private Task fastUpdate;
	private Task slowUpdate;

	public static ScoreManager getInstance() {
		return instance;
	}

	public ScoreManager() {
		super("TournamentSystem.ScoreManager");
	}

	@Override
	public void onLoad() {
		ScoreManager.instance = this;
		this.playerScoreCache = new HashMap<UUID, Integer>();

		this.fastUpdate = null;
		this.slowUpdate = null;
	}

	@Override
	public void onEnable() {
		if (fastUpdate != null) {
			Task.tryStartTask(fastUpdate);
		}

		if (slowUpdate != null) {
			Task.tryStartTask(slowUpdate);
		}

		fastUpdate = new SimpleTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> asyncScoreUpdate(player.getUniqueId()));
			}
		}, 40L);

		slowUpdate = new SimpleTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				asyncUpdateAll();
			}
		}, 20 * 60);

		Task.tryStartTask(fastUpdate);
		Task.tryStartTask(slowUpdate);

		asyncUpdateAll();
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(fastUpdate);
		Task.tryStopTask(slowUpdate);
	}

	public void asyncScoreUpdate(UUID uuid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					doSynchronousScoreUpdate(uuid);
				} catch (Exception e) {
					e.printStackTrace();
					Log.warn("ScoreManager", "Failed to fetch the score of player with uuid: " + uuid.toString());
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public void doSynchronousScoreUpdate(UUID uuid) throws SQLException {
		String sql = "SELECT IFNULL(SUM(amount), 0) as total_score FROM player_score WHERE player_id = (SELECT id FROM players WHERE uuid = ?)";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
		ps.setString(1, uuid.toString());

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			int score = rs.getInt("total_score");
			playerScoreCache.put(uuid, score);
		}

		rs.close();
		ps.close();
	}

	public void asyncUpdateAll() {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "SELECT p.uuid AS uuid, IFNULL(SUM(s.amount), 0) AS score FROM players AS p LEFT JOIN player_score AS s ON s.player_id = p.id GROUP BY p.id";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						UUID uuid = UUID.fromString(rs.getString("uuid"));
						int score = rs.getInt("score");

						playerScoreCache.put(uuid, score);
					}

					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.warn("ScoreManager", "Failed to fetch the score of all player");
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public HashMap<UUID, Integer> getPlayerScoreCache() {
		return playerScoreCache;
	}

	public int getPlayerScore(OfflinePlayer player) {
		return getPlayerScore(player.getUniqueId());
	}

	public int getPlayerScore(UUID uuid) {
		if (playerScoreCache.containsKey(uuid)) {
			return playerScoreCache.get(uuid);
		}

		return 0; // Return 0 until we successfully fetch the correct score
	}
	
	public void addPlayerScore(UUID uuid, int score, String reason) {
		this.addPlayerScore(uuid, score, true, reason);
	}

	public int getTeamScore(TournamentSystemTeam team) {
		if (team == null) {
			return 0; // No team, score is 0
		}

		return team.getScore();
	}

	public int getTeamScore(int teamId) {
		return getTeamScore(TournamentSystem.getInstance().getTeamManager().getTeam(teamId));
	}
	
	
	public void addPlayerScore(OfflinePlayer player, int score, String reason) {
		this.addPlayerScore(player, score, true, reason);
	}

	public void addPlayerScore(OfflinePlayer player, int score, boolean addToTeam, String reason) {
		this.addPlayerScore(player.getUniqueId(), score, addToTeam, reason);
	}
	
	public void addPlayerScore(UUID uuid, int score, boolean addToTeam, String reason) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "INSERT INTO player_score (player_id, server, reason, amount) SELECT id, ?, ?, ? FROM players WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, TournamentSystem.getInstance().getServerName());
					ps.setString(2, reason);
					ps.setInt(3, score);
					ps.setString(4, uuid.toString());

					if (playerScoreCache.containsKey(uuid)) {
						int oldScore = playerScoreCache.get(uuid);

						playerScoreCache.put(uuid, oldScore + score);
					}

					ps.executeUpdate();

					ps.close();

					if (addToTeam) {
						if (TeamManager.hasTeamManager()) {
							TournamentSystemTeam team = (TournamentSystemTeam) TournamentSystem.getInstance().getTeamManager().getPlayerTeam(uuid);
							if (team != null) {
								addTeamScore(team, score, "team score from player score: " + reason);
							}
						}
					}
				} catch (Exception ee) {
					ee.printStackTrace();
					Log.error("Failed to add score to a player " + uuid + ". Please compensate them with " + score + " points to fix score error");
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public void addTeamScore(TournamentSystemTeam team, int score, String reason) {
		this.addTeamScore(team.getTeamNumber(), score, reason);
	}

	public void addTeamScore(int teamId, int score, String reason) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					TournamentSystemTeam team = TournamentSystemTeamManager.getInstance().getTeam(teamId);
					if(team != null) {
					}
					String sql = "INSERT INTO team_score (team_id, server, reason, amount) SELECT id, ?, ?, ? FROM teams WHERE team_number = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, TournamentSystem.getInstance().getServerName());
					ps.setString(2, reason);
					ps.setInt(3, score);
					ps.setInt(4, teamId);

					ps.executeUpdate();

					ps.close();
				} catch (Exception ee) {
					ee.printStackTrace();
					Log.error("Failed to add score to team " + teamId + ". Please compensate them with " + score + " points to fix score error");
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}
}