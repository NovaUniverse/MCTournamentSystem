package net.novauniverse.mctournamentsystem.spigot.team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class TournamentSystemTeamManager extends TeamManager implements Listener {
	private int teamCount = 12;
	private static TournamentSystemTeamManager instance;

	public int getTeamCount() {
		return teamCount;
	}

	private HashMap<UUID, ChatColor> playerColorCache;

	public static TournamentSystemTeamManager getInstance() {
		return instance;
	}

	public TournamentSystemTeamManager(int teamCount) {
		TournamentSystemTeamManager.instance = this;

		this.teamCount = teamCount;
		playerColorCache = new HashMap<UUID, ChatColor>();

		for (int i = 0; i < teamCount; i++) {
			TournamentSystemTeam team = new TournamentSystemTeam(i + 1, 0);

			this.teams.add(team);
		}

		updateTeams();

		Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				updateTeams();

				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					Team team = getPlayerTeam(player);

					if (team == null) {
						if (playerColorCache.containsKey(player.getUniqueId())) {
							Log.trace("Removing team color for player " + player.getName());
							playerColorCache.remove(player.getUniqueId());
							NetherBoardScoreboard.getInstance().resetPlayerNameColor(player);
						}
					} else {
						if (playerColorCache.containsKey(player.getUniqueId())) {
							if (team.getTeamColor() != playerColorCache.get(player.getUniqueId())) {
								Log.trace("Changing team color for player " + player.getName());
								playerColorCache.put(player.getUniqueId(), team.getTeamColor());
								NetherBoardScoreboard.getInstance().setPlayerNameColorBungee(player, team.getTeamColor());
							}
						} else {
							Log.trace("Setting team color for player " + player.getName());
							playerColorCache.put(player.getUniqueId(), team.getTeamColor());
							NetherBoardScoreboard.getInstance().setPlayerNameColorBungee(player, team.getTeamColor());
						}
					}
				});
			}
		}, 100L, 100L);

		List<Integer> missingTeams = new ArrayList<Integer>();
		for (int i = 0; i < teamCount; i++) {
			missingTeams.add((Integer) i + 1);
		}

		try {
			String sql = "SELECT team_number FROM teams";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				if (missingTeams.contains((Integer) teamNumber)) {
					missingTeams.remove((Integer) teamNumber);
				}
			}

			rs.close();
			ps.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}

		missingTeams.forEach(i -> {
			try {
				String sql = "INSERT INTO teams (team_number) VALUES (?)";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setInt(1, i);

				ps.execute();

				ps.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		});
	}

	private void updateTeams() {
		new BukkitRunnable() {
			@Override
			public void run() {
				// Update players
				List<UUID> fullPlayerList = new ArrayList<>();

				try {
					String sql = "SELECT uuid, team_number FROM players";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						UUID uuid = UUID.fromString(rs.getString("uuid"));
						int teamNumber = rs.getInt("team_number");

						fullPlayerList.add(uuid);

						teams.forEach(team -> {
							if (teamNumber <= 0 || ((TournamentSystemTeam) team).getTeamNumber() != teamNumber) {
								if (team.getMembers().contains(uuid)) {
									team.getMembers().remove(uuid);
									Log.trace("TournamentCoreTeamManager", "Removing player with uuid " + uuid.toString() + " from team " + ((TournamentSystemTeam) team).getTeamNumber());
								}
							} else {
								if (teamNumber == ((TournamentSystemTeam) team).getTeamNumber()) {
									if (!team.getMembers().contains(uuid)) {
										team.getMembers().add(uuid);
										Log.trace("TournamentCoreTeamManager", "Adding player with uuid " + uuid.toString() + " to team " + ((TournamentSystemTeam) team).getTeamNumber());
									}
								}
							}
						});
					}

					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.warn("TournamentCoreTeamManager", "Failed to update teams");
					return;
				}

				getTeams().forEach(team -> {
					List<UUID> toRemove = new ArrayList<>();
					team.getMembers().forEach(member -> {
						if (!fullPlayerList.contains(member)) {
							toRemove.add(member);
						}
					});

					toRemove.forEach(uuid -> {
						Log.trace("TournamentCoreTeamManager", "Removing player with uuid " + uuid.toString() + " from team " + ((TournamentSystemTeam) team).getTeamNumber() + " since they are no longer in the team list");
						team.getMembers().remove(uuid);
					});
				});

				// Update score
				try {
					String sql = "SELECT score, team_number FROM teams";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						TournamentSystemTeam team = getTeam(rs.getInt("team_number"));

						if (team != null) {
							team.setScore(rs.getInt("score"));
						}
					}

					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.warn("TournamentCoreTeamManager", "Failed to update team score");
					return;
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						// Update player names
						Bukkit.getServer().getOnlinePlayers().forEach(player -> updatePlayerName(player));
					}
				}.runTask(TournamentSystem.getInstance());
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public TournamentSystemTeam getTeam(int teamNumber) {
		// ream is not a typo. its an inside joke in our discord server
		return (TournamentSystemTeam) teams.stream().filter(ream -> ((TournamentSystemTeam) ream).getTeamNumber() == teamNumber).findFirst().orElse(null);
	}

	public void updatePlayerName(Player player) {
		Team team = getPlayerTeam(player);

		String name = "MissingNo";

		ChatColor color = ChatColor.YELLOW;
		if (team == null) {
			name = color + "No team : " + ChatColor.RESET + player.getName();
		} else {
			if (((TournamentSystemTeam) team).getTeamNumber() >= 1) {
				color = team.getTeamColor();
				name = color + team.getDisplayName() + ChatColor.WHITE + " : " + ChatColor.RESET + player.getName();
			}
		}

		player.setDisplayName("| " + name);
		player.setPlayerListName(name);

		if (NetherBoardScoreboard.getInstance().isEnabled()) {
			String teamName = "";

			if (team == null) {
				teamName = ChatColor.YELLOW + (TournamentSystem.getInstance().isMakeTeamNamesBold() ? org.bukkit.ChatColor.BOLD + "" : "") + "No team";
			} else {
				teamName = color + (TournamentSystem.getInstance().isMakeTeamNamesBold() ? org.bukkit.ChatColor.BOLD + "" : "") + team.getDisplayName();
			}

			NetherBoardScoreboard.getInstance().setPlayerLine(1, player, teamName);
		}
	}

	public boolean requireTeamToJoin(Player player) {
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		this.updatePlayerName(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (playerColorCache.containsKey(e.getPlayer().getUniqueId())) {
			playerColorCache.remove(e.getPlayer().getUniqueId());
		}
	}
}