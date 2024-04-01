package net.novauniverse.mctournamentsystem.lobby.modules.scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import net.novauniverse.mctournamentsystem.lobby.TournamentSystemLobby;
import net.novauniverse.mctournamentsystem.spigot.score.PlayerScoreData;
import net.novauniverse.mctournamentsystem.spigot.score.TeamScoreData;
import net.novauniverse.mctournamentsystem.spigot.score.TopScore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = true)
public class TSLeaderboard extends NovaModule {
	private static TSLeaderboard instance;

	private Hologram playerHologram;
	private Hologram teamHologram;

	private int lines;

	private int taskId;

	public static TSLeaderboard getInstance() {
		return instance;
	}

	public TSLeaderboard() {
		super("TournamentSystem.Lobby.LeaderBoard");
	}

	@Override
	public void onLoad() {
		TSLeaderboard.instance = this;

		this.playerHologram = null;
		this.teamHologram = null;

		this.lines = 5;

		this.taskId = -1;
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystemLobby.getInstance(), new Runnable() {
				@Override
				public void run() {
					update();
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

		if (teamHologram != null) {
			teamHologram.delete();
		}

		if (playerHologram != null) {
			playerHologram.delete();
		}
	}

	public void update() {
		if (playerHologram != null) {
			List<String> content = new ArrayList<String>();
			
			content.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Top player scores");
			
			List<PlayerScoreData> scores = TopScore.getPlayerTopScore(lines);

			int lineIndex = 0;
			for (PlayerScoreData scoreData : scores) {
				lineIndex++;
				content.add(ChatColor.YELLOW + "" + lineIndex + ChatColor.GOLD + " : " + scoreData.toString());
			}

			DHAPI.setHologramLines(playerHologram, content);
		}
		
		if (TeamManager.hasTeamManager()) {
			if (teamHologram != null) {
				List<String> content = new ArrayList<String>();
				
				content.add(ChatColor.GREEN + "" + ChatColor.BOLD + "Top team scores");
				
				int lineIndex = 0;

				List<TeamScoreData> scores = TopScore.getTeamTopScore(lines);

				for (TeamScoreData scoreData : scores) {
					lineIndex++;
					content.add(ChatColor.YELLOW + "" + lineIndex + ChatColor.GOLD + " : " + scoreData.toString());
				}

				DHAPI.setHologramLines(teamHologram, content);
			}
		}
	}

	public void setTeamHologramLocation(Location location) {
		if (teamHologram != null) {
			teamHologram.delete();
		}

		teamHologram = DHAPI.createHologram(UUID.randomUUID().toString(), location, false); // HologramsAPI.createHologram(TournamentSystem.getInstance(), location);
	}

	public void setPlayerHologramLocation(Location location) {
		if (playerHologram != null) {
			playerHologram.delete();
		}

		playerHologram = DHAPI.createHologram(UUID.randomUUID().toString(), location, false); // HologramsAPI.createHologram(TournamentSystem.getInstance(), location);
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}
}