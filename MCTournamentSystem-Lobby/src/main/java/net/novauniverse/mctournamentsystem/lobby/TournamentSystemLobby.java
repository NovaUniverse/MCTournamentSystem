package net.novauniverse.mctournamentsystem.lobby;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
import net.novauniverse.mctournamentsystem.lobby.command.clearkotlscore.ClearKOTLScore;
import net.novauniverse.mctournamentsystem.lobby.command.duel.AcceptDuelCommand;
import net.novauniverse.mctournamentsystem.lobby.command.duel.DuelCommand;
import net.novauniverse.mctournamentsystem.lobby.command.givemefireworks.GiveMeFireworksCommand;
import net.novauniverse.mctournamentsystem.lobby.command.missilewars.MissileWars;
import net.novauniverse.mctournamentsystem.lobby.modules.celebrationmode.LobbyCelebrationMode;
import net.novauniverse.mctournamentsystem.lobby.modules.halloffame.HallOfFame;
import net.novauniverse.mctournamentsystem.lobby.modules.halloffame.HallOfFameConfig;
import net.novauniverse.mctournamentsystem.lobby.modules.halloffame.HallOfFameNPC;
import net.novauniverse.mctournamentsystem.lobby.modules.lobby.Lobby;
import net.novauniverse.mctournamentsystem.lobby.modules.scoreboard.TSLeaderboard;
import net.novauniverse.mctournamentsystem.lobby.npc.trait.TournamentLobbyRemoveOnLoadTrait;
import net.novauniverse.mctournamentsystem.misc.ClosestEntityComparatorBlockCentered;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.tablistmessage.TabListMessage;
import net.novauniverse.mctournamentsystem.spigot.utils.AdvancedGUIUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.utils.LocationData;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.XYZLocation;

public class TournamentSystemLobby extends JavaPlugin implements Listener {
	private static TournamentSystemLobby instance;

	private LocationData lobbyLocation;
	private boolean preventDamageMobs;

	public LocationData getLobbyLocationData() {
		return lobbyLocation;
	}

	public Location getLobbyLocation() {
		return lobbyLocation.toLocation(Lobby.getInstance().getWorld());
	}

	public boolean isPreventDamageMobs() {
		return preventDamageMobs;
	}

	public static TournamentSystemLobby getInstance() {
		return instance;
	}

	public World getLobbyWorld() {
		return Lobby.getInstance().getWorld();
	}

	@Override
	public void onEnable() {
		TournamentSystemLobby.instance = this;

		saveDefaultConfig();

		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.lobby.modules");

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		File configFile = new File(TournamentSystem.getInstance().getMapDataFolder().getAbsolutePath() + File.separator + "lobby.json");
		JSONObject config;
		try {
			config = JSONFileUtils.readJSONObjectFromFile(configFile);
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal("TournamentSystemLobby", "Failed to read config file at " + configFile.getAbsolutePath() + ". " + e.getClass().getName() + " " + e.getMessage());
			Bukkit.getServer().getPluginManager().disablePlugin(this);
			return;
		}

		if (getConfig().getBoolean("prevent_damage_mobs")) {
			preventDamageMobs = true;
		}

		lobbyLocation = new LocationData(config.getJSONObject("spawn_location"));
		Lobby.getInstance().setLobbyLocation(lobbyLocation);

		JSONObject kotlConfig = config.optJSONObject("king_of_the_ladder");
		if (kotlConfig != null) {
			if (kotlConfig.optBoolean("enabled", false)) {
				JSONObject center = kotlConfig.getJSONObject("center");
				double centerX = center.getDouble("x");
				double centerZ = center.getDouble("z");

				double radius = kotlConfig.getDouble("radius");

				int minScoreHeight = kotlConfig.getInt("score_trigger_min_y");
				int maxScoreHeight = kotlConfig.getInt("score_trigger_max_y");

				Lobby.getInstance().enableKOTL(centerX, centerZ, radius, minScoreHeight, maxScoreHeight);
			}

			JSONObject hologram = kotlConfig.optJSONObject("hologram");
			if (hologram != null) {
				if (hologram.optBoolean("enabled", false)) {
					int lines = hologram.optInt("lines", 10);
					XYZLocation location = XYZLocation.fromJSON(hologram.getJSONObject("location"));
					Log.info(getName(), "Setting up KOTL hologram at " + location.toString());
					Lobby.getInstance().setupKOTLHologram(location, lines);
				}
			}
		}

		TSLeaderboard.getInstance().setLines(config.optInt("leaderboard_lines", 8));

		LocationData playerScoreLocation = LocationData.fromJSON(config.getJSONObject("player_leaderboard"));
		LocationData teamScoreLocation = LocationData.fromJSON(config.getJSONObject("team_leaderboard"));

		TSLeaderboard.getInstance().setPlayerHologramLocation(playerScoreLocation.toLocation(Lobby.getInstance().getWorld()));
		TSLeaderboard.getInstance().setTeamHologramLocation(teamScoreLocation.toLocation(Lobby.getInstance().getWorld()));

		CommandRegistry.registerCommand(new AcceptDuelCommand());
		CommandRegistry.registerCommand(new DuelCommand());
		CommandRegistry.registerCommand(new ClearKOTLScore());
		CommandRegistry.registerCommand(new MissileWars(this));

		if (TournamentSystem.getInstance().isCelebrationMode()) {
			ModuleManager.enable(LobbyCelebrationMode.class);
			CommandRegistry.registerCommand(new GiveMeFireworksCommand());
		}

		if (config.has("advancedgui")) {
			if (Bukkit.getServer().getPluginManager().getPlugin("AdvancedGUI") != null) {
				Log.info("TournamentSystemLobby", "AdvancedGUI detected. Trying to set up any configured displays");
				JSONArray layouts = config.getJSONArray("advancedgui");
				for (int i = 0; i < layouts.length(); i++) {
					JSONObject layout = layouts.getJSONObject(i);

					LocationData locationData = LocationData.fromJSON(layout);
					Location location = LocationUtils.fullyCenterLocation(locationData.toLocation(getLobbyWorld()));
					ItemFrame itemFrame = (ItemFrame) location.getWorld().getNearbyEntities(location, 3, 3, 3).stream().filter(e -> e.getType() == EntityType.ITEM_FRAME).sorted(new ClosestEntityComparatorBlockCentered(location)).findFirst().orElse(null);
					if (itemFrame == null) {
						Log.warn("TournamentSystemLobby", "No item frame found near " + locationData.toVector().toString());
					} else {
						Log.debug("TournamentSystemLobby", "Found frame near " + locationData.toVector().toString() + " at " + itemFrame.getLocation() + " uuid: " + itemFrame.getUniqueId());
						String directionName = layout.getString("direction");
						String layoutName = layout.getString("layout");
						int activationRadius = layout.getInt("activation_radius");

						try {
							AdvancedGUIUtils.placeLayout(layoutName, activationRadius, itemFrame, directionName);
						} catch (Exception e) {
							e.printStackTrace();
							Log.error("TournamentSystemLobby", "An error occured while trying to place AdvancedGUI layout. " + e.getClass().getName() + " " + e.getMessage());
						}
					}
				}
			}
		}

		CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TournamentLobbyRemoveOnLoadTrait.class));
		/* ----- Misc ----- */
		new BukkitRunnable() {
			@Override
			public void run() {
				/* ----- Run after load ----- */
				System.out.println(lobbyLocation);
				List<NPC> toRemove = new ArrayList<>();
				CitizensAPI.getNPCRegistry().iterator().forEachRemaining(npc -> {
					if (npc.hasTrait(TournamentLobbyRemoveOnLoadTrait.class)) {
						toRemove.add(npc);
					}
				});
				toRemove.forEach(npc -> {
					npc.destroy();
					CitizensAPI.getNPCRegistry().deregister(npc);
				});
			}
		}.runTask(this);

		JSONObject hallOfFame = config.optJSONObject("hall_of_fame");
		if (hallOfFame != null) {
			if (hallOfFame.optBoolean("enabled", false)) {
				String url = hallOfFame.getString("data_url");
				boolean debug = hallOfFame.optBoolean("debug", false);
				XYZLocation nameHologramLocation = new XYZLocation(hallOfFame.getJSONObject("hologram_location"));
				List<HallOfFameNPC> npcs = new ArrayList<>();

				JSONArray npcsData = hallOfFame.getJSONArray("npcs");
				for (int i = 0; i < npcsData.length(); i++) {
					JSONObject npcData = npcsData.getJSONObject(i);
					Location location = LocationUtils.fromJSONObject(npcData, Lobby.getInstance().getWorld());
					npcs.add(new HallOfFameNPC(location));
				}

				HallOfFameConfig hallOfFameConfig = new HallOfFameConfig(nameHologramLocation, npcs, url, debug);

				new BukkitRunnable() {
					@Override
					public void run() {
						HallOfFame hof = (HallOfFame) ModuleManager.getModule(HallOfFame.class);
						hof.init(hallOfFameConfig);
					}
				}.runTaskLater(this, 20L);
			}
		}

		TabListMessage.setServerType("Lobby");
	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}
}