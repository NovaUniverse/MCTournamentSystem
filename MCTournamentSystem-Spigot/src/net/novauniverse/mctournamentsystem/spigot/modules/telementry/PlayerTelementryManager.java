package net.novauniverse.mctournamentsystem.spigot.modules.telementry;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;

@NovaAutoLoad(shouldEnable = true)
public class PlayerTelementryManager extends NovaModule {
	private Task task;

	@Override
	public String getName() {
		return "ts.playertelementrymanager";
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

			ByteArrayDataOutput out = ByteStreams.newDataOutput();

			out.writeDouble(health);
			out.writeDouble(maxHealth);

			out.writeInt(food);
			out.writeInt(closestEnemyDistance);

			out.writeUTF(gamemode);

			out.writeBoolean(gameEnabled);
			out.writeBoolean(isInGame);

			player.sendPluginMessage(TournamentSystem.getInstance(), TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL, out.toByteArray());
		});
	}
}