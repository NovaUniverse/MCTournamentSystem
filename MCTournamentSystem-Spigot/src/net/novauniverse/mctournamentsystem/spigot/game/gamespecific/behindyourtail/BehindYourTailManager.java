package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.behindyourtail;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.behindyourtail.NovaBehindYourTail;
import net.novauniverse.behindyourtail.game.BehindYourTail;
import net.novauniverse.behindyourtail.game.event.BehindYourTailPlayerDamageFoxEvent;
import net.novauniverse.behindyourtail.game.role.Role;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.CustomDefaultPlayerEliminationMessaageProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.tracker.BehindYourTailCompassTracker;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.particle.NovaDustOptions;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTracker;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.VectorUtils;

public class BehindYourTailManager extends NovaModule implements Listener {
	public static int PLAYER_ATTACK_FOX_SCORE = 10;

	public static double LINE_PARTICLE_COUNT = 50D;

	public BehindYourTailManager() {
		super("TournamentSystem.GameSpecific.BehindYourTailManager");
	}

	private ItemStack tracker;
	private ItemStack tracer;

	private boolean tracersDisabled;

	private Task particleTask;

	public boolean isTracersDisabled() {
		return tracersDisabled;
	}

	public void setTracersDisabled(boolean tracersDisabled) {
		this.tracersDisabled = tracersDisabled;
	}

	@Override
	public void onLoad() {
		this.tracersDisabled = false;

		CommandRegistry.registerCommand(new ToggleBehindYourTailTracers());

		ModuleManager.disable(PlayerHeadDrop.class);

		ModuleManager.enable(CompassTracker.class);
		CompassTracker.getInstance().setStrictMode(false);
		CompassTracker.getInstance().setCompassTrackerTarget(new BehindYourTailCompassTracker());

		ItemBuilder trackerBuilder = new ItemBuilder(Material.COMPASS);
		trackerBuilder.setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Enemy fox tracker");
		tracker = trackerBuilder.build();

		ItemBuilder tracerBuilder = new ItemBuilder(Material.REDSTONE);
		tracerBuilder.setName(org.bukkit.ChatColor.RED + "" + ChatColor.BOLD + "Show tracers");
		tracerBuilder.addLore(ChatColor.WHITE + "Hold this in your hand to", ChatColor.WHITE + "show particle lines to enemies");
		tracer = tracerBuilder.build();

		TournamentSystem.getInstance().getDefaultPlayerEliminationMessage().addCustomProvider(PlayerEliminationReason.OTHER, new CustomDefaultPlayerEliminationMessaageProvider() {
			@Override
			public String getEliminationMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
				Role role = NovaBehindYourTail.getInstance().getGame().getPlayerRole(player.getUniqueId());

				ChatColor color = ChatColor.AQUA;
				if (TeamManager.hasTeamManager()) {
					Team team = TeamManager.getTeamManager().getPlayerTeam(player.getUniqueId());
					if (team != null) {
						color = team.getTeamColor();
					}
				}

				if (role == Role.HUNTER) {
					return ChatColor.RED + "" + ChatColor.BOLD + "Player Eliminated> " + color + ChatColor.BOLD + player.getName() + ChatColor.RED + ChatColor.BOLD + " failed to protect their fox";
				}

				return "";
			}
		});

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			new BukkitRunnable() {
				@Override
				public void run() {
					setupInventory(player);
				}
			}.runTaskLater(getPlugin(), 5L);
		});

		particleTask = new SimpleTask(getPlugin(), () -> {
			BehindYourTail game = NovaBehindYourTail.getInstance().getGame();
			Bukkit.getServer().getOnlinePlayers().forEach(player -> {
				Team playerTeam = TeamManager.getTeamManager().getPlayerTeam(player);
				Bukkit.getServer().getOnlinePlayers().forEach(player2 -> {
					if (player == player2) {
						return;
					}

					if (!game.isPlayerInGame(player2)) {
						return;
					}

					if (player.getWorld() != player2.getWorld()) {
						return;
					}

					if (player2.getGameMode() == GameMode.SPECTATOR) {
						return;
					}

					Team targetTeam = TeamManager.getTeamManager().getPlayerTeam(player2);

					boolean isFriend = false;
					if (targetTeam == playerTeam) {
						isFriend = true;
					}

					// ParticleEffect.REDSTONE.display(player2.getLocation().clone().add(0D, 2.5D,
					// 0D), isFriend ? Color.GREEN : Color.RED, player);

					NovaCore.getInstance().getNovaParticleProvider().showColoredRedstoneParticle(player2.getLocation().clone().add(0D, 2.5D, 0D), isFriend ? NovaDustOptions.GREEN : NovaDustOptions.RED, player);

					if (game.isPlayerInGame(player)) {
						ItemStack mainHand = VersionIndependentUtils.get().getItemInMainHand(player);
						if (mainHand != null) {
							if (VersionIndependentUtils.get().getItemInMainHand(player).getType() == Material.REDSTONE) {
								if (tracersDisabled) {
									VersionIndependentUtils.get().sendActionBarMessage(player, org.bukkit.ChatColor.RED + "Tracers are disabled right now");
								} else {
									Role myRole = game.getPlayerRole(player.getUniqueId());
									Role targetRole = game.getPlayerRole(player2.getUniqueId());
									if (!isFriend) {
										if (myRole != targetRole) {
											Vector diff = VectorUtils.getDifferential(player.getLocation().toVector(), player2.getLocation().toVector());
											Vector step = new Vector(diff.getX() / LINE_PARTICLE_COUNT, diff.getY() / LINE_PARTICLE_COUNT, diff.getZ() / LINE_PARTICLE_COUNT);

											step = step.multiply(-1D);

											for (int i = 0; i < LINE_PARTICLE_COUNT; i++) {
												Location point = player.getLocation().clone().add(step.getX() * ((double) i), step.getY() * ((double) i), step.getZ() * ((double) i));
												if (point.distance(player.getLocation()) > 3) {
													NovaCore.getInstance().getNovaParticleProvider().showColoredRedstoneParticle(player2.getLocation().clone().add(0D, 2.5D, 0D), NovaDustOptions.RED, player);
													// ParticleEffect.REDSTONE.display(point, Color.RED, player);
												}
											}
										}
									}
								}
							}
						}
					}
				});
			});
		}, 2L);

	}

	public void setupInventory(Player player) {
		if (TournamentSystem.getInstance().isEnableBehindYourTailcompass()) {
			player.getInventory().addItem(tracker.clone());
		}

		if (TournamentSystem.getInstance().isBehindYourTailParticles()) {
			player.getInventory().addItem(tracer.clone());
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		if (TournamentSystem.getInstance().isBehindYourTailParticles()) {
			Task.tryStartTask(particleTask);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> setupInventory(player));
			}
		}.runTaskLater(getPlugin(), 5L);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		Task.tryStopTask(particleTask);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBehindYourTailPlayerDamageFox(BehindYourTailPlayerDamageFoxEvent e) {
		Player player = e.getAttacker();
		player.sendMessage(ChatColor.GRAY + "Damaged enemy fox. +" + PLAYER_ATTACK_FOX_SCORE + " points");
		ScoreManager.getInstance().addPlayerScore(player, PLAYER_ATTACK_FOX_SCORE, true);
	}
}