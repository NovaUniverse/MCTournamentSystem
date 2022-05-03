package net.novauniverse.mctournamentsystem.lobby.modules.duels;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.timers.TickCallback;
import net.zeeraa.novacore.commons.utils.Callback;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependantSound;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseWorld;
import net.zeeraa.novacore.spigot.timers.BasicTimer;

public class DuelInstance {
	private UUID instanceUUID;
	private List<Player> players;
	private MultiverseWorld world;

	private DuelStage stage;

	private BasicTimer endTimer;

	public void sendMessage(String message) {
		if (world != null) {
			for (Player player : world.getWorld().getPlayers()) {
				player.sendMessage(message);
			}
		}
	}

	public DuelInstance(UUID instanceUUID, MultiverseWorld world, List<Player> players) {
		this.instanceUUID = instanceUUID;
		this.players = players;
		this.world = world;

		this.stage = DuelStage.IDLE;

		this.endTimer = new BasicTimer(180);

		endTimer.addFinishCallback(new Callback() {
			@Override
			public void execute() {
				sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Duel canceled due to the time limit being reached");
				setStage(DuelStage.ENDED);
			}
		});
	}

	public UUID getInstanceUUID() {
		return instanceUUID;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public MultiverseWorld getWorld() {
		return world;
	}

	public DuelStage getStage() {
		return stage;
	}

	private int quitCount = 0;

	public void onPlayerQuit(Player player) {
		quitCount++;

		if (quitCount <= 1) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					Player otherPlayer = null;

					for (Player p2 : players) {
						if (player.getUniqueId().toString().equalsIgnoreCase(p2.getUniqueId().toString())) {
							continue;
						}

						otherPlayer = p2;
						break;
					}

					if (quitCount <= 1) {
						sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player Eliminated> " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.RED + "" + ChatColor.BOLD + " quit");
						sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Game Over> Winner: " + ChatColor.AQUA + ChatColor.BOLD + (otherPlayer != null ? otherPlayer.getName() : "null"));
						Bukkit.getServer().broadcastMessage(ChatColor.AQUA + (otherPlayer != null ? otherPlayer.getName() : "null") + ChatColor.GOLD + " won a duel against " + ChatColor.AQUA + player.getName());
						setStage(DuelStage.ENDED);
					} else {
						endDraw();
					}
				}
			}, 40L);
		} else {
			endDraw();
		}
	}

	public void endDraw() {
		sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Game Over> It's a draw");
		setStage(DuelStage.ENDED);
	}

	public void setStage(DuelStage stage) {
		this.stage = stage;

		switch (stage) {
		case COUNTDOWN:
			Location location1 = new Location(world.getWorld(), 5.5, 20, 0.5, 90, 0);
			Location location2 = new Location(world.getWorld(), -4.5, 20, 0.5, -90, 0);

			players.get(0).teleport(location1);
			players.get(1).teleport(location2);

			BasicTimer timer = new BasicTimer(5, 20L);
			timer.addTickCallback(new TickCallback() {
				@Override
				public void execute(long timeLeft) {
					if (timeLeft == 0) {
						return;
					}

					sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + timeLeft);

					world.getWorld().getPlayers().forEach(player -> {
						VersionIndependantUtils.get().sendActionBarMessage(player, ChatColor.AQUA + "" + ChatColor.BOLD + timeLeft);
						VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.NOTE_PLING, 1F, 1F);
					});
				}
			});

			timer.addFinishCallback(new Callback() {
				@Override
				public void execute() {
					sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "GO");

					players.get(0).teleport(location1);
					players.get(1).teleport(location2);

					players.forEach(player -> {
						if (player.isOnline()) {
							player.setFireTicks(0);
							player.setFallDistance(0);
						}
					});

					world.getWorld().getPlayers().forEach(player -> {
						VersionIndependantUtils.get().playSound(player, player.getLocation(), VersionIndependantSound.NOTE_PLING, 1F, 1F);
						VersionIndependantUtils.get().sendActionBarMessage(player, ChatColor.GOLD + "" + ChatColor.BOLD + "GO");
					});

					setStage(DuelStage.INGAME);
				}
			});

			timer.start();

			break;

		case INGAME:
			endTimer.start();
			sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "The duel will end in 3 minutes");
			break;

		case ENDED:
			if (endTimer.isRunning()) {
				endTimer.cancel();
			}

			BasicTimer timer2 = new BasicTimer(5, 20L);

			timer2.addFinishCallback(new Callback() {
				@Override
				public void execute() {
					players.forEach(player -> {
						if (player.isOnline()) {
							DuelManager.getInstance().restorePlayer(player);
						}
					});

					Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentSystem.getInstance(), new Runnable() {
						@Override
						public void run() {
							killInstance();
						}
					}, 30L);
				}
			});

			Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					sendMessage(ChatColor.GRAY + "Sending you to the lobby in 5 seconds");
					timer2.start();
				}
			}, 20L);

			break;

		default:
			break;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DuelInstance) {
			return this.getInstanceUUID().toString().equalsIgnoreCase(((DuelInstance) obj).getInstanceUUID().toString());
		}

		if (obj instanceof UUID) {
			return this.getInstanceUUID().toString().equalsIgnoreCase(((UUID) obj).toString());
		}

		return false;
	}

	public void killInstance() {
		DuelManager.getInstance().killDuelInstance(this);
	}
}