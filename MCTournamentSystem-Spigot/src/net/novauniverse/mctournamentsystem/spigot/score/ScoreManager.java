package net.novauniverse.mctournamentsystem.spigot.score;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.EssentialModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = true)
@EssentialModule
public class ScoreManager extends NovaModule implements Listener {
	private static ScoreManager instance;
	private HashMap<UUID, Integer> playerScoreCache;

	private int taskId;

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

		this.taskId = -1;
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					Bukkit.getServer().getOnlinePlayers().forEach(player -> asyncScoreUpdate(player.getUniqueId()));
				}
			}, 40L, 40L);
		}
	}

	@Override
	public void onDisable() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}

	/*
	 * public int updatePlayerScore(UUID uuid) { int score = 0; try { String sql =
	 * "SELECT score FROM players WHERE uuid = ?"; PreparedStatement ps =
	 * TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(
	 * sql);
	 * 
	 * ps.setString(1, uuid.toString());
	 * 
	 * ResultSet rs = ps.executeQuery(); if (rs.next()) { score =
	 * rs.getInt("score"); playerScoreCache.put(uuid, score); }
	 * 
	 * rs.close(); ps.close(); } catch (Exception e) { e.printStackTrace();
	 * Log.warn("ScoreManager", "Failed to fetch the score of player with uuid: " +
	 * uuid.toString()); } return score; }
	 */

	public void asyncScoreUpdate(UUID uuid) {
		new BukkitRunnable() {
			@Override
			public void run() {
				int score = 0;
				try {
					String sql = "SELECT score FROM players WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, uuid.toString());

					ResultSet rs = ps.executeQuery();
					if (rs.next()) {
						score = rs.getInt("score");
						playerScoreCache.put(uuid, score);
					}

					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.warn("ScoreManager", "Failed to fetch the score of player with uuid: " + uuid.toString());
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	/*
	 * public int updatePlayerScore(UUID uuid) { int score = 0; try { String sql =
	 * "SELECT score FROM players WHERE uuid = ?"; PreparedStatement ps =
	 * TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(
	 * sql);
	 * 
	 * ps.setString(1, uuid.toString());
	 * 
	 * ResultSet rs = ps.executeQuery(); if (rs.next()) { score =
	 * rs.getInt("score"); playerScoreCache.put(uuid, score); }
	 * 
	 * rs.close(); ps.close(); } catch (Exception e) { e.printStackTrace();
	 * Log.warn("ScoreManager", "Failed to fetch the score of player with uuid: " +
	 * uuid.toString()); } return score; }
	 */

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

		return 0;// updatePlayerScore(uuid);
	}

	public void addPlayerScore(OfflinePlayer player, int score) {
		this.addPlayerScore(player, score, true);
	}

	public void addPlayerScore(OfflinePlayer player, int score, boolean addToTeam) {
		this.addPlayerScore(player.getUniqueId(), score, addToTeam);
	}

	public void addPlayerScore(UUID uuid, int score) {
		this.addPlayerScore(uuid, score, true);
	}

	public int getTeamScore(TournamentSystemTeam team) {
		if (team == null) {
			return 0;
		}

		return team.getScore();
	}

	public int getTeamScore(int teamId) {
		return getTeamScore(TournamentSystem.getInstance().getTeamManager().getTeam(teamId));
	}

	public void addPlayerScore(UUID uuid, int score, boolean addToTeam) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "CALL increment_player_score(?, ?)"; // "UPDATE players SET score = score + ? WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, uuid.toString());
					ps.setInt(2, score);

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
								addTeamScore(team, score);
							}
						}
					}
				} catch (Exception ee) {
					ee.printStackTrace();

					String message = "!!!Score update failure!!! Player with uuid: " + uuid.toString() + " failed to add " + score + " score";
					String query = "CALL increment_player_score(" + uuid.toString() + ", " + score + ")"; // "UPDATE players SET score = score + " + score + " WHERE uuid = '" +
																											// uuid.toString() + "';";

					Log.error("Failed to add score to a player. Please check the sql_fix.sql file");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);

					logFailedQuery(query);
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public void addTeamScore(TournamentSystemTeam team, int score) {
		this.addTeamScore(team.getTeamNumber(), score);
	}

	public void addTeamScore(int teamId, int score) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "CALL increment_team_score(?, ?)"; // "UPDATE teams SET score = score + ? WHERE team_number = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setInt(1, teamId);
					ps.setInt(2, score);

					ps.executeUpdate();

					ps.close();
				} catch (Exception ee) {
					ee.printStackTrace();
					String message = "!!!Score update failure!!! Team with id: " + teamId + " failed to add " + score + " score";
					String query = "CALL increment_team_score(" + teamId + ", " + score + ")"; // "UPDATE teams SET score = score + " + score + " WHERE team_number = " +
																								// teamId + ";";

					Log.error("Failed to add score to a team. Please check the sql_fix.sql file");
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED + message);

					logFailedQuery(query);
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	private void logFailedQuery(String query) {
		try (BufferedWriter writer = Files.newBufferedWriter(TournamentSystem.getInstance().getSqlFixFile().toPath(), StandardOpenOption.APPEND)) {
			writer.write(query + System.lineSeparator());
		} catch (IOException ioe) {
			System.err.format("IOException: %s%n", ioe);
			Log.error("Emergency score error", "Failled to write score log to sql_fix.sql! Please run this query to fix the score: " + query);
		}
	}
}