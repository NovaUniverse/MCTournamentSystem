package net.novauniverse.mctournamentsystem.lobby.modules.scoreboard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import net.novauniverse.mctournamentsystem.lobby.TournamentSystemLobby;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
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
			teamHologram.clearLines();
			teamHologram.delete();
		}

		if (playerHologram != null) {
			playerHologram.clearLines();
			playerHologram.delete();
		}
	}

	public void update() {
		if (playerHologram != null) {
			int lineIndex = 0;

			if (playerHologram.size() <= lineIndex) {
				playerHologram.appendTextLine(ChatColor.GREEN + "" + ChatColor.BOLD + "Top player scores");
			}

			ArrayList<PlayerScoreData> scores = TopScore.getPlayerTopScore(lines);

			for (PlayerScoreData scoreData : scores) {
				lineIndex++;

				if (playerHologram.size() <= lineIndex) {
					playerHologram.appendTextLine("-----------");
				}
				((TextLine) playerHologram.getLine(lineIndex)).setText(ChatColor.YELLOW + "" + lineIndex + ChatColor.GOLD + " : " + scoreData.toString());
			}

			while (playerHologram.size() > lineIndex + 1) {
				playerHologram.removeLine(playerHologram.size() - 1);
			}
		}
		if (TeamManager.hasTeamManager()) {
			if (teamHologram != null) {
				int lineIndex = 0;

				if (teamHologram.size() <= lineIndex) {
					teamHologram.appendTextLine(ChatColor.GREEN + "" + ChatColor.BOLD + "Top team scores");
				}

				List<TeamScoreData> scores = TopScore.getTeamTopScore(lines);

				for (TeamScoreData scoreData : scores) {
					lineIndex++;

					if (teamHologram.size() <= lineIndex) {
						teamHologram.appendTextLine("-----------");
					}
					((TextLine) teamHologram.getLine(lineIndex)).setText(ChatColor.YELLOW + "" + lineIndex + ChatColor.GOLD + " : " + scoreData.toString());
				}

				while (teamHologram.size() > lineIndex + 1) {
					teamHologram.removeLine(teamHologram.size() - 1);
				}
			}
		}
	}

	public void setTeamHologramLocation(Location location) {
		if (teamHologram != null) {
			teamHologram.delete();
		}

		teamHologram = HologramsAPI.createHologram(TournamentSystem.getInstance(), location);
	}

	public void setPlayerHologramLocation(Location location) {
		if (playerHologram != null) {
			playerHologram.delete();
		}

		playerHologram = HologramsAPI.createHologram(TournamentSystem.getInstance(), location);
	}

	public int getLines() {
		return lines;
	}

	public void setLines(int lines) {
		this.lines = lines;
	}
}