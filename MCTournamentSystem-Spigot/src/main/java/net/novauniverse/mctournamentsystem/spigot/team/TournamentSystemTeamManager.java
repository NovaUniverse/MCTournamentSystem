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
import net.novauniverse.mctournamentsystem.spigot.modules.playerprefix.PlayerPrefixManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.NovaItemsAdderUtils;

public class TournamentSystemTeamManager extends TeamManager implements Listener {
	private int teamCount = 12;
	private static TournamentSystemTeamManager instance;

	private TournamentTeamManagerSettings settings;

	private HashMap<UUID, String> playerTeamNameOverrides;
	private HashMap<UUID, ChatColor> playerTeamColorOverrides;

	public int getTeamCount() {
		return teamCount;
	}

	private HashMap<UUID, ChatColor> playerColorCache;

	public static TournamentSystemTeamManager getInstance() {
		return instance;
	}

	public TournamentTeamManagerSettings getSettings() {
		return settings;
	}

	public HashMap<UUID, String> getPlayerTeamNameOverrides() {
		return playerTeamNameOverrides;
	}

	public HashMap<UUID, ChatColor> getPlayerTeamColorOverrides() {
		return playerTeamColorOverrides;
	}

	public void setPlayerTeamNameOverride(UUID playerUUID, String teamName) {
		playerTeamNameOverrides.put(playerUUID, teamName);
	}

	public void setPlayerTeamColorOverride(UUID playerUUID, ChatColor color) {
		playerTeamColorOverrides.put(playerUUID, color);
	}

	public void clearPlayerTeamNameOverride(UUID playerUUID) {
		playerTeamNameOverrides.remove(playerUUID);
	}

	public void clearPlayerTeamColorOverride(UUID playerUUID) {
		playerTeamColorOverrides.remove(playerUUID);
	}

	public TournamentSystemTeamManager(int teamCount) {
		playerTeamNameOverrides = new HashMap<>();
		playerTeamColorOverrides = new HashMap<>();

		TournamentSystemTeamManager.instance = this;

		this.settings = TournamentTeamManagerSettings.defaultSettings();

		this.teamCount = teamCount;
		playerColorCache = new HashMap<UUID, ChatColor>();

		for (int i = 0; i < teamCount; i++) {
			TournamentSystemTeam team = new TournamentSystemTeam(i + 1, 0, 0);

			team.applyTeamMetadataClasses();

			this.teams.add(team);
		}

		updateTeams();

		Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				updateTeams();

				if (settings.shouldUpdateNameColor()) {
					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						updateNetherboardColor(player);
					});
				}
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

	public void updateNetherboardColor(Player player) {
		if (settings.getNameColorUpdateExemptList().contains(player.getUniqueId())) {
			return;
		}

		Team team = getPlayerTeam(player);

		if (team == null) {
			if (playerColorCache.containsKey(player.getUniqueId())) {
				Log.trace("Removing team color for player " + player.getName());
				playerColorCache.remove(player.getUniqueId());
				NetherBoardScoreboard.getInstance().resetPlayerNameColor(player);
			}
		} else {
			ChatColor color = team.getTeamColor();

			if (playerTeamColorOverrides.containsKey(player.getUniqueId())) {
				color = playerTeamColorOverrides.get(player.getUniqueId());
			}

			if (playerColorCache.containsKey(player.getUniqueId())) {
				if (color != playerColorCache.get(player.getUniqueId())) {
					Log.trace("Changing team color for player " + player.getName());
					playerColorCache.put(player.getUniqueId(), color);
					NetherBoardScoreboard.getInstance().setPlayerNameColorBungee(player, color);
				}
			} else {
				Log.trace("Setting team color for player " + player.getName());
				playerColorCache.put(player.getUniqueId(), color);
				NetherBoardScoreboard.getInstance().setPlayerNameColorBungee(player, color);
			}
		}
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

				// Update score and kills
				try {
					String sql = "SELECT t.team_number AS team_number, t.kills AS kills, IFNULL(SUM(s.amount), 0) AS total_score FROM teams AS t LEFT JOIN team_score AS s ON s.team_id = t.id  GROUP BY t.id";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						TournamentSystemTeam team = getTeam(rs.getInt("team_number"));

						if (team != null) {
							team.setKills(rs.getInt("kills"));
							team.setScore(rs.getInt("total_score"));
						}
					}

					rs.close();
					ps.close();
				} catch (Exception e) {
					e.printStackTrace();
					Log.warn("TournamentCoreTeamManager", "Failed to update team score and kills");
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
		TournamentSystemTeam team = (TournamentSystemTeam) getPlayerTeam(player);

		String name = "MissingNo";

		String noTeamIcon = null;
		if (TournamentSystem.getInstance().getIANoTeamIcon() != null) {
			noTeamIcon = NovaItemsAdderUtils.getFontImage(TournamentSystem.getInstance().getIANoTeamIcon());
		}

		String prefix = null;

		if (ModuleManager.isEnabled(PlayerPrefixManager.class)) {
			PlayerPrefixManager prefixManager = ModuleManager.getModule(PlayerPrefixManager.class);
			if (prefixManager.hasPrefix(player)) {
				prefix = prefixManager.getPrefix(player);
			}
		}

		ChatColor color = ChatColor.YELLOW;
		if (team == null) {
			boolean staff = player.hasPermission("tournamentsystem.staff");

			if (TournamentSystem.getInstance().isHideTeamNameNextToPlayerIGN()) {
				name = (noTeamIcon == null ? "" : noTeamIcon + " ") + (prefix == null ? "" : ChatColor.RESET + prefix) + (staff ? ChatColor.WHITE : ChatColor.GRAY) + player.getName() + ChatColor.RESET;
			} else {
				name = (noTeamIcon == null ? "" : noTeamIcon + " ") + color + "" + (TournamentSystem.getInstance().isMakeTeamNamesBold() ? ChatColor.BOLD + "" : "") + "No team : " + (prefix == null ? "" : ChatColor.RESET + prefix) + (staff ? ChatColor.WHITE : ChatColor.GRAY) + player.getName() + ChatColor.RESET;
			}
		} else {
			if (((TournamentSystemTeam) team).getTeamNumber() >= 1) {
				color = team.getTeamColor();

				if (playerTeamColorOverrides.containsKey(player.getUniqueId())) {
					color = playerTeamColorOverrides.get(player.getUniqueId());
				}

				String teamName = team.getDisplayName();

				if (playerTeamNameOverrides.containsKey(player.getUniqueId())) {
					teamName = playerTeamNameOverrides.get(player.getUniqueId());
				}

				if (TournamentSystem.getInstance().isHideTeamNameNextToPlayerIGN()) {
					name = (team.hadBadge() ? team.getBadge() + " " : "") + (prefix == null ? "" : ChatColor.RESET + prefix) + color + player.getName() + ChatColor.RESET;
				} else {
					name = (team.hadBadge() ? team.getBadge() + " " : "") + color + "" + (TournamentSystem.getInstance().isMakeTeamNamesBold() ? ChatColor.BOLD + "" : "") + teamName + ChatColor.WHITE + " : " + (prefix == null ? "" : ChatColor.RESET + prefix) + color + player.getName() + ChatColor.RESET;
				}
			}
		}

		if (settings.shouldUpdateDisplayName() && !settings.getDisplayNameUpdateExemptList().contains(player.getUniqueId())) {
			player.setDisplayName(name);
		}

		if (settings.shouldUpdateListName() && !settings.getListNameUpdateExemptList().contains(player.getUniqueId())) {
			player.setPlayerListName(name);
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