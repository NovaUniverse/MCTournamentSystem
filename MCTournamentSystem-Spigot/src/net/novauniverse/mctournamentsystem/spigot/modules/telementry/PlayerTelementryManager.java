package net.novauniverse.mctournamentsystem.spigot.modules.telementry;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

@NovaAutoLoad(shouldEnable = true)
public class PlayerTelementryManager extends NovaModule {
	private static PlayerTelementryManager instance;

	private Task task;

	private List<ITelementryMetadataProvider> telementryMetadataProviders;

	public PlayerTelementryManager() {
		super("TournamentSystem.PlayerTelementryManager");
		PlayerTelementryManager.instance = this;
		telementryMetadataProviders = new ArrayList<ITelementryMetadataProvider>();
	}

	public void addMetadataProvider(ITelementryMetadataProvider provider) {
		telementryMetadataProviders.add(provider);
		Log.debug("PlayerTelementryManager", "Added telementry metadata provider " + provider.getClass().getName());
	}

	public static PlayerTelementryManager getInstance() {
		return instance;
	}

	@Override
	public void onLoad() {
		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				sendData();
			}
		}, 10L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	public void sendData() {
		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			double health = player.getHealth();
			double maxHealth = PlayerUtils.getPlayerMaxHealth(player);

			int food = player.getFoodLevel();

			String gamemode = player.getGameMode().name();

			boolean gameEnabled = false;
			boolean isInGame = false;

			int closestEnemyDistance = Integer.MAX_VALUE;

			if (NovaCore.isNovaGameEngineEnabled()) {
				if (GameManager.getInstance().hasGame()) {
					gameEnabled = true;
					if (GameManager.getInstance().getActiveGame().getPlayers().contains(player.getUniqueId())) {
						isInGame = true;

						if (TeamManager.hasTeamManager()) {
							Team team = TeamManager.getTeamManager().getPlayerTeam(player);

							if (team != null) {
								for (Player player2 : Bukkit.getServer().getOnlinePlayers()) {
									Team team2 = TeamManager.getTeamManager().getPlayerTeam(player2);
									if (team2 != null) {
										if (team.equals(team2)) {
											continue;
										}

										if (GameManager.getInstance().getActiveGame().getPlayers().contains(player2.getUniqueId())) {
											if (player.getWorld().equals(player2.getWorld())) {
												int dist = (int) player.getLocation().distance(player2.getLocation());
												if (dist < closestEnemyDistance) {
													closestEnemyDistance = dist;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}

			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeDouble(health);
			out.writeDouble(maxHealth);

			out.writeInt(food);
			out.writeInt(closestEnemyDistance);

			out.writeUTF(gamemode);

			out.writeBoolean(gameEnabled);
			out.writeBoolean(isInGame);

			JSONObject metadata = new JSONObject();

			telementryMetadataProviders.forEach(provider -> provider.process(player, metadata));

			out.writeUTF(metadata.toString());

			player.sendPluginMessage(TournamentSystem.getInstance(), TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL, out.toByteArray());
		});
	}
}