package net.novauniverse.mctournamentsystem.spigot.labymod;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.labymod.serverapi.api.LabyAPI;
import net.labymod.serverapi.api.serverinteraction.economy.EconomyBalanceType;
import net.md_5.bungee.api.ChatColor;
import net.novauniverse.labymodapiplus.LabyModAPIPlus;
import net.novauniverse.labymodapiplus.economydisplay.EconomyDisplayWrapper;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.DelayedRunner;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class LabyModIntegration extends NovaModule implements Listener {
	private String bannerURL;

	private String scoreIconURL;
	private String teamScoreIconURL;

	private List<EconomyDisplayWrapper> playerEcomonyDisplayWrappers;

	private Task teamNameSubtileUpdateTask;
	private Task scoreTask;

	private boolean showTeamNames;
	private boolean showScore;

	public LabyModIntegration() {
		super("TournamentSystem.LabyMod.Commons");
		bannerURL = null;
		scoreIconURL = null;
		teamScoreIconURL = null;
		showTeamNames = false;
		showScore = false;
		playerEcomonyDisplayWrappers = new ArrayList<>();
	}

	@Override
	public void onLoad() {
		if (TournamentSystemCommons.getTournamentSystemConfigData().has("labymod")) {
			JSONObject labymodConfig = TournamentSystemCommons.getTournamentSystemConfigData().getJSONObject("labymod");
			bannerURL = labymodConfig.optString("banner");

			scoreIconURL = labymodConfig.optString("solo_score_icon_url");
			teamScoreIconURL = labymodConfig.optString("team_score_icon_url");

			showTeamNames = labymodConfig.optBoolean("show_team_names", true);
			showScore = labymodConfig.optBoolean("show_score", true);

			if (bannerURL != null) {
				Log.info("LabyModIntegration", "Banner URL is: " + bannerURL);
			}
		}

		scoreTask = new SimpleTask(getPlugin(), () -> {
			Bukkit.getServer().getOnlinePlayers().forEach(p -> {
				playerEcomonyDisplayWrappers.stream().filter(w -> w.getPlayer().equals(p.getUniqueId())).findFirst().ifPresent(economy -> {
					int score = ScoreManager.getInstance().getPlayerScore(p);
					int teamScore = 0;
					TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(p);
					if (team != null) {
						teamScore = team.getScore();
					}

					economy.set(EconomyBalanceType.CASH, score);
					economy.set(EconomyBalanceType.BANK, teamScore);

					economy.send();
				});
			});
		}, 20L);

		teamNameSubtileUpdateTask = new SimpleTask(getPlugin(), () -> {
			Bukkit.getOnlinePlayers().forEach(this::sendSubtitles);
		}, 100L);
	}

	@Override
	public void onEnable() throws Exception {
		if (showTeamNames) {
			Task.tryStartTask(teamNameSubtileUpdateTask);
		}

		if (showScore) {
			Task.tryStartTask(scoreTask);
		}
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(teamNameSubtileUpdateTask);
		Task.tryStartTask(scoreTask);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		final Player player = e.getPlayer();

		if (!playerEcomonyDisplayWrappers.stream().anyMatch(d -> d.getPlayer().equals(player.getUniqueId()))) {
			EconomyDisplayWrapper economy = new EconomyDisplayWrapper(player.getUniqueId());

			economy.setVisible(EconomyBalanceType.CASH, true);
			economy.setVisible(EconomyBalanceType.BANK, true);

			if (scoreIconURL != null) {
				economy.setIconURL(EconomyBalanceType.CASH, scoreIconURL);
			}

			if (teamScoreIconURL != null) {
				economy.setIconURL(EconomyBalanceType.BANK, teamScoreIconURL);
			}

			playerEcomonyDisplayWrappers.add(economy);
		}

		DelayedRunner.runDelayed(() -> {
			if (bannerURL != null) {
				LabyModAPIPlus.sendTabListBanner(player.getUniqueId(), bannerURL);
			}

			if (showTeamNames) {
				sendSubtitles(player);
			}

			LabyAPI.getService().getMenuTransmitter().transmit(player.getUniqueId());
		}, 20);
	}

	public void sendSubtitles(Player player) {
		JsonArray subtitles = new JsonArray();
		Bukkit.getOnlinePlayers().forEach(p -> {
			String text = "";
			Team team = TeamManager.getTeamManager().getPlayerTeam(p);
			if (team != null) {
				text = (TeamManager.getTeamManager().isInSameTeam(player, p) ? ChatColor.GREEN : ChatColor.RED) + team.getDisplayName();
			} else {
				text = ChatColor.YELLOW + "No Team";
			}

			JsonObject subtitle = new JsonObject();
			subtitle.addProperty("uuid", p.getUniqueId().toString());
			subtitle.addProperty("size", 1.6D);
			subtitle.addProperty("value", text);

			subtitles.add(subtitle);
		});

		LabyAPI.getService().getPayloadCommunicator().sendLabyModMessage(player.getUniqueId(), "account_subtitle", subtitles);
	}
}