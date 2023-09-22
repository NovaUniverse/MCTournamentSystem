package net.novauniverse.mctournamentsystem.lobby.modules.lobby;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.lobby.TournamentSystemLobby;
import net.novauniverse.mctournamentsystem.lobby.modules.lobby.kotl.score.KingOfTheLadderScore;
import net.novauniverse.mctournamentsystem.lobby.modules.lobby.kotl.score.KingOfTheLadderScoreComparator;
import net.novauniverse.mctournamentsystem.lobby.versionspecific.Pre_1_13_Utils;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.NovaCoreGameVersion;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMaterial;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseWorld;
import net.zeeraa.novacore.spigot.module.modules.multiverse.WorldUnloadOption;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.LocationData;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.PlayerUtils;
import net.zeeraa.novacore.spigot.utils.VectorArea;
import net.zeeraa.novacore.spigot.utils.XYZLocation;

@NovaAutoLoad(shouldEnable = true)
public class Lobby extends NovaModule implements Listener {

	private static Lobby instance;

	private Location lobbyLocation;

	private boolean kotlEnabled;
	private KingOfTheLadderScoreComparator kingOfTheLadderScoreComparator;
	private Location kotlLocation;
	private double kotlRadius;
	private int kotlScoreHeightMin;
	private int kotlScoreHeightMax;
	private int kotlHologramLines;
	private Task kotlScoreTask;
	private List<KingOfTheLadderScore> kotlScore;
	private Hologram kotlHologram;

	private SimpleTask calmDownCageResetTimer;

	private MultiverseWorld multiverseWorld;

	private boolean gameRunningMessageSent;
	private Task gameRunningCheckTask;

	private Task loadScoreTask;
	private Task lobbyTask;

	private boolean spleefEnabled;
	private VectorArea spleefArena;
	private VectorArea spleefFloor;
	private VectorArea spleefDeathArea;
	private Location spleefRespawnLocation;

	private Task spleefTask;
	private Task spleefResetTask;

	private boolean pvpBypassEnabled;
	private boolean mapProtectionBypassEnabled;

	public static Lobby getInstance() {
		return instance;
	}

	public Lobby() {
		super("TournamentSystem.Lobby.Lobby");
	}

	@Override
	public void onLoad() {
		Lobby.instance = this;
		this.addDependency(NovaScoreboardManager.class);
		this.addDependency(MultiverseManager.class);
		this.lobbyLocation = null;
		this.multiverseWorld = null;
		this.gameRunningMessageSent = false;
		this.gameRunningCheckTask = null;
		this.loadScoreTask = null;
		this.lobbyTask = null;
		this.kotlEnabled = false;

		this.spleefEnabled = false;
		this.spleefArena = null;
		this.spleefFloor = null;
		this.spleefDeathArea = null;
		this.spleefRespawnLocation = null;

		this.kingOfTheLadderScoreComparator = new KingOfTheLadderScoreComparator();
		this.kotlScoreHeightMin = Integer.MAX_VALUE;
		this.kotlScoreHeightMax = Integer.MAX_VALUE;
		this.kotlHologramLines = 10;
		this.kotlHologram = null;
		this.kotlScore = new ArrayList<KingOfTheLadderScore>();

		this.pvpBypassEnabled = false;
		this.mapProtectionBypassEnabled = false;
	}

	@Override
	public void onEnable() throws Exception {
		File worldFolder = TournamentSystem.getInstance().getMapDataFolder();
		Log.debug(getName(), "World folder is: " + worldFolder.getAbsolutePath());
		multiverseWorld = MultiverseManager.getInstance().createFromFile(new File(worldFolder.getAbsolutePath() + File.separator + "Worlds" + File.separator + "lobby_world"), WorldUnloadOption.DELETE);

		multiverseWorld.getWorld().setThundering(false);
		multiverseWorld.getWorld().setWeatherDuration(0);

		multiverseWorld.setLockWeather(true);

		this.spleefTask = new SimpleTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				getWorld().getPlayers().forEach(player -> {
					if (spleefDeathArea.isInsideBlock(player.getLocation().toVector())) {
						player.teleport(spleefRespawnLocation, TeleportCause.PLUGIN);
						player.setFireTicks(0);
						VersionIndependentSound.WITHER_HURT.play(player);
						VersionIndependentUtils.getInstance().sendTitle(player, "", ChatColor.RED + "You died", 10, 20, 10);
					}

					if (spleefArena.isInsideBlock(player.getLocation().toVector())) {
						if (player.getGameMode() == GameMode.ADVENTURE) {
							player.setGameMode(GameMode.SURVIVAL);
						}

						if (!player.getInventory().contains(VersionIndependentMaterial.DIAMOND_SHOVEL.toBukkitVersion())) {
							ItemBuilder builder = new ItemBuilder(VersionIndependentMaterial.DIAMOND_SHOVEL.toBukkitVersion());
							builder.setUnbreakable(true);
							player.getInventory().addItem(builder.build());
						}
					}
				});
			}
		}, 5L);

		this.spleefResetTask = new SimpleTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				for (int x = spleefFloor.getPosition1().getBlockX(); x <= spleefFloor.getPosition2().getBlockX(); x++) {
					for (int y = spleefFloor.getPosition1().getBlockY(); y <= spleefFloor.getPosition2().getBlockY(); y++) {
						for (int z = spleefFloor.getPosition1().getBlockZ(); z <= spleefFloor.getPosition2().getBlockZ(); z++) {
							getWorld().getBlockAt(x, y, z).setType(Material.SNOW_BLOCK);
							getWorld().getPlayers().forEach(player -> {
								if (spleefArena.isInsideBlock(player.getLocation().toVector())) {
									VersionIndependentUtils.getInstance().sendActionBarMessage(player, ChatColor.GOLD + "" + ChatColor.BOLD + "Arena reset");
								}
							});
						}
					}
				}
			}
		}, 20 * 20);

		this.kotlScoreTask = new SimpleTask(getPlugin(), new Runnable() {
			@Override
			public void run() {
				if (!kotlEnabled) {
					return;
				}

				Bukkit.getServer().getOnlinePlayers().stream().filter(player -> player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE).filter(player -> isInKOTLArena(player)).forEach(player -> {
					int playerY = player.getLocation().getBlockY();
					if (playerY >= kotlScoreHeightMin && playerY <= kotlScoreHeightMax) {
						KingOfTheLadderScore pScore = kotlScore.stream().filter(s -> s.getUuid().equals(player.getUniqueId())).findFirst().orElse(null);
						if (pScore != null) {
							pScore.incrementScore();
						} else {
							Log.debug("KOTL", "Adding player " + player.getName() + " to the score list");
							KingOfTheLadderScore pScoreNew = new KingOfTheLadderScore(player);
							pScoreNew.incrementScore();
							kotlScore.add(pScoreNew);
						}
					}
				});

				if (kotlHologram != null) {
					int line = 1;

					for (KingOfTheLadderScore score : kotlScore.stream().sorted(kingOfTheLadderScoreComparator).limit(kotlHologramLines).collect(Collectors.toList())) {
						net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.AQUA;
						if (TeamManager.hasTeamManager()) {
							Team team = TeamManager.getTeamManager().getPlayerTeam(score.getUuid());
							if (team != null) {
								color = team.getTeamColor();
							}
						}
						((TextLine) kotlHologram.getLine(line)).setText(color + score.getName() + ChatColor.WHITE + " : " + ChatColor.AQUA + score.getScore());
						line++;
					}

					for (; line <= kotlHologramLines; line++) {
						((TextLine) kotlHologram.getLine(line)).setText(ChatColor.GRAY + "Empty");
					}
					/*
					 * while (kotlHologram.size() - 1 < kotlHologramLines) {
					 * kotlHologram.getLine(kotlHologramLines)
					 * kotlHologram.appendTextLine(ChatColor.GRAY + "Empty"); }
					 */
				}
			}
		}, 20L);
		kotlScoreTask.start();

		File spleefFile = new File(multiverseWorld.getWorld().getWorldFolder().getAbsolutePath() + File.separator + "spleefconfig.json");
		if (spleefFile.exists()) {
			Log.info("Lobby", "Found spleefconfig.json");
			JSONObject json = JSONFileUtils.readJSONObjectFromFile(spleefFile);
			spleefEnabled = true;
			spleefArena = VectorArea.fromJSON(json.getJSONObject("arena"));
			spleefFloor = VectorArea.fromJSON(json.getJSONObject("floor"));
			spleefDeathArea = VectorArea.fromJSON(json.getJSONObject("death_area"));

			spleefRespawnLocation = LocationUtils.fromJSONObject(json.getJSONObject("respawn_location"), multiverseWorld.getWorld());

			Task.tryStartTask(spleefTask);
			Task.tryStartTask(spleefResetTask);
		}

		NovaScoreboardManager.getInstance().setGlobalLine(0, new StaticTextLine(ChatColor.YELLOW + "Lobby"));

		lobbyTask = new SimpleTask(TournamentSystemLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				lobbyLocation.getWorld().getPlayers().forEach(player -> {
					player.setFoodLevel(20);
					if (player.getLocation().getY() < -3) {
						if (player.getGameMode() == GameMode.SURVIVAL) {
							player.setGameMode(GameMode.ADVENTURE);
						}
						player.teleport(lobbyLocation);
						player.setFallDistance(0);
					}
				});
			}
		}, 5L);
		lobbyTask.start();

		loadScoreTask = new SimpleTask(TournamentSystemLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (NovaCore.getInstance().hasTeamManager()) {
					// Load all players into score cache so that they get displayed in the leader
					// board
					NovaCore.getInstance().getTeamManager().getTeams().forEach(team -> team.getMembers().forEach(uuid -> ScoreManager.getInstance().getPlayerScore(uuid)));
				}
			}
		}, 200L);
		loadScoreTask.start();

		gameRunningCheckTask = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				String activeServer = TournamentSystemCommons.getActiveServer();
				if (activeServer == null) {
					if (gameRunningMessageSent) {
						gameRunningMessageSent = false;
					}
				} else {
					if (!gameRunningMessageSent) {
						gameRunningMessageSent = true;
						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							sendReconnectMessage(player);
						}
					}
				}
			}
		}, 20L);
		gameRunningCheckTask.start();

		multiverseWorld.getWorld().setGameRuleValue("announceAdvancements", "false");
		// CommandRegistry.registerCommand(new ReconnectCommand());
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(gameRunningCheckTask);
		Task.tryStopTask(calmDownCageResetTimer);
		Task.tryStopTask(loadScoreTask);
		Task.tryStopTask(lobbyTask);

		Task.tryStopTask(spleefTask);
		Task.tryStopTask(spleefResetTask);

		MultiverseManager.getInstance().unload(multiverseWorld);
		multiverseWorld = null;
	}

	public boolean isPvpBypassEnabled() {
		return pvpBypassEnabled;
	}

	public void setPvpBypassEnabled(boolean pvpBypassEnabled) {
		this.pvpBypassEnabled = pvpBypassEnabled;
	}

	public boolean isMapProtectionBypassEnabled() {
		return mapProtectionBypassEnabled;
	}

	public void setMapProtectionBypassEnabled(boolean mapProtectionBypassEnabled) {
		this.mapProtectionBypassEnabled = mapProtectionBypassEnabled;
	}

	public Location getLobbyLocation() {
		return lobbyLocation;
	}

	public void setLobbyLocation(LocationData locationData) {
		this.setLobbyLocation(locationData.toLocation(getWorld()));
	}

	public void setLobbyLocation(Location lobbyLocation) {
		this.lobbyLocation = lobbyLocation;
		lobbyLocation.setWorld(multiverseWorld.getWorld());
	}

	public World getWorld() {
		return multiverseWorld.getWorld();
	}

	public void enableKOTL(double x, double z, double radius, int scoreHeightMin, int scoreHeightMax) {
		kotlEnabled = true;
		kotlRadius = radius;
		kotlLocation = new Location(multiverseWorld.getWorld(), x, 0, z);

		kotlScoreHeightMin = scoreHeightMin;
		kotlScoreHeightMax = scoreHeightMax;
	}

	public void setKOTLHologramLines(int kotlHologramLines) {
		this.kotlHologramLines = kotlHologramLines;
	}

	public void setupKOTLHologram(XYZLocation location, int lines) {
		Location hologramLocation = location.toBukkitLocation(getWorld());
		this.kotlHologram = HologramsAPI.createHologram(getPlugin(), hologramLocation);

		kotlHologram.appendTextLine(ChatColor.GREEN + "" + ChatColor.BOLD + "Top KOTL Players");
		for (int i = 0; i < lines; i++) {
			kotlHologram.appendTextLine(ChatColor.DARK_GRAY + "Empty");
		}
	}

	private boolean isInKOTLArena(Entity entity) {
		if (kotlLocation != null) {
			if (entity.getWorld() == kotlLocation.getWorld()) {
				Location kotlCheck = kotlLocation.clone();

				kotlCheck.setY(entity.getLocation().getY());

				return kotlCheck.distance(entity.getLocation()) <= kotlRadius;
			}
		}

		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().getType() == Material.FISHING_ROD) {
			e.setCancelled(true);
		}

		if (spleefEnabled == true) {
			if (e.getItemDrop().getItemStack().getType() == VersionIndependentMaterial.DIAMOND_SHOVEL.toBukkitVersion()) {
				if (e.getPlayer().getGameMode() == GameMode.SURVIVAL || e.getPlayer().getGameMode() == GameMode.ADVENTURE) {
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		// Prevent the press e to open inventory message from showing all the time
		NovaCoreGameVersion version = VersionIndependentUtils.get().getNovaCoreGameVersion();
		if (version == NovaCoreGameVersion.V_1_8 || version == NovaCoreGameVersion.V_1_12) {
			Pre_1_13_Utils.giveOpenInventoryAchivement(player);
		}

		PlayerUtils.clearPlayerInventory(player);
		PlayerUtils.clearPotionEffects(player);
		PlayerUtils.resetPlayerXP(player);
		if (lobbyLocation != null) {
			player.teleport(lobbyLocation);
		}
		player.setFallDistance(0);
		player.setGameMode(GameMode.ADVENTURE);

		new BukkitRunnable() {
			@Override
			public void run() {
				// Fix but where gamemode does not get set
				player.setGameMode(GameMode.CREATIVE);
				player.setGameMode(GameMode.ADVENTURE);
			}
		}.runTaskLater(TournamentSystem.getInstance(), 5L);
	}

	public void sendReconnectMessage(Player player) {
		TextComponent text1 = new TextComponent("A game is in progress!");
		TextComponent text2 = new TextComponent("Use /reconnect or click this message to reconnect");

		text1.setColor(net.md_5.bungee.api.ChatColor.GOLD);
		text2.setColor(net.md_5.bungee.api.ChatColor.GOLD);

		text1.setBold(true);
		text2.setBold(true);

		text1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reconnect"));
		text2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/reconnect"));

		player.spigot().sendMessage(text1);
		player.spigot().sendMessage(text2);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinMonitor(PlayerJoinEvent e) {
		if (TournamentSystemCommons.getActiveServer() != null) {
			sendReconnectMessage(e.getPlayer());
		}
	}

	// Removed since it makes the press e to open your inventory show
	/*
	 * @EventHandler(priority = EventPriority.NORMAL) public void
	 * onVersionIndependantPlayerAchievementAwarded(
	 * VersionIndependantPlayerAchievementAwardedEvent e) { e.setCancelled(true); }
	 */

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntitySpawn(EntitySpawnEvent e) {
		if (e.getEntityType() == EntityType.DROPPED_ITEM) {
			if (e.getLocation().getWorld() == getWorld()) {
				if (spleefEnabled) {
					if (spleefArena.isInsideBlock(e.getLocation().toVector())) {
						Item item = (Item) e.getEntity();
						if (item.getItemStack().getType() == VersionIndependentMaterial.SNOWBALL.toBukkitVersion()) {
							e.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamage(EntityDamageEvent e) {
		if (pvpBypassEnabled) {
			return;
		}

		if (e.getEntity() instanceof Player) {
			if (lobbyLocation != null) {
				if (e.getEntity().getWorld() == lobbyLocation.getWorld()) {
					if (isInKOTLArena(e.getEntity()) && kotlEnabled) {
						e.setDamage(0);
						e.setCancelled(false);
						Log.trace("KOTL", "Allow damage event for player " + e.getEntity().getName() + " due to being inside the KTOL arena");
					} else {
						e.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e) {
		if (mapProtectionBypassEnabled) {
			return;
		}

		Player player = e.getPlayer();
		if (player.getWorld() == lobbyLocation.getWorld()) {
			if (spleefEnabled) {
				if (spleefFloor.isInsideBlock(e.getBlock().getLocation().toVector())) {
					e.setCancelled(false);
					return;
				}
			}

			if (player.getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (mapProtectionBypassEnabled) {
			return;
		}

		Player player = e.getPlayer();
		if (player.getWorld() == lobbyLocation.getWorld()) {
			if (player.getGameMode() != GameMode.CREATIVE) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			// if (e.getClickedBlock().getType() == Material.SIGN_POST ||
			// e.getClickedBlock().getType() == Material.WALL_SIGN) {
			if (VersionIndependentUtils.get().isSign(e.getClickedBlock())) {
				if (e.getPlayer().getGameMode() != GameMode.SPECTATOR) {
					if (e.getClickedBlock().getState() instanceof Sign) {
						Sign sign = (Sign) e.getClickedBlock().getState();
						if (ChatColor.stripColor(sign.getLine(0)).equalsIgnoreCase("[Free]") && ChatColor.stripColor(sign.getLine(1)).equalsIgnoreCase("Fishing rod")) {
							Player p = e.getPlayer();

							if (!p.getInventory().contains(Material.FISHING_ROD)) {
								p.getInventory().addItem(new ItemBuilder(Material.FISHING_ROD).setUnbreakable(true).build());
							}
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent e) {
		if (e.getLine(0).equalsIgnoreCase("[free rod]")) {
			e.setLine(0, ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "[Free]");
			e.setLine(1, ChatColor.BLUE + "Fishing rod");
			e.setLine(2, "");
			e.setLine(3, "");
		}
	}

	public void clearKOTLScore() {
		kotlScore.clear();
	}
}