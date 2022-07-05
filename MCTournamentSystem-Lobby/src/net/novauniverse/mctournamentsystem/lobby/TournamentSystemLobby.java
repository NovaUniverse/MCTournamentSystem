package net.novauniverse.mctournamentsystem.lobby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONException;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.TraitInfo;
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
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.tablistmessage.TabListMessage;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.utils.LocationUtils;
import net.zeeraa.novacore.spigot.utils.XYZLocation;

public class TournamentSystemLobby extends JavaPlugin implements Listener {
	private static TournamentSystemLobby instance;

	private Location lobbyLocation;
	private boolean preventDamageMobs;

	public Location getLobbyLocation() {
		return lobbyLocation;
	}

	public boolean isPreventDamageMobs() {
		return preventDamageMobs;
	}

	public static TournamentSystemLobby getInstance() {
		return instance;
	}

	@Override
	public void onEnable() {
		TournamentSystemLobby.instance = this;

		saveDefaultConfig();

		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.lobby.modules");

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		if (getConfig().getBoolean("prevent_damage_mobs")) {
			preventDamageMobs = true;
		}

		lobbyLocation = new Location(Lobby.getInstance().getWorld(), getConfig().getDouble("spawn_x"), getConfig().getDouble("spawn_y"), getConfig().getDouble("spawn_z"), (float) getConfig().getDouble("spawn_yaw"), (float) getConfig().getDouble("spawn_pitch"));
		Lobby.getInstance().setLobbyLocation(lobbyLocation);

		Lobby.getInstance().setKOTLLocation(getConfig().getDouble("kotl_x"), getConfig().getDouble("kotl_z"), getConfig().getDouble("kotl_radius"), getConfig().getInt("kotl_score_height_min"), getConfig().getInt("kotl_score_height_max"));

		ConfigurationSection kotlHologram = getConfig().getConfigurationSection("ktol_hologram");
		if (kotlHologram.getBoolean("enabled")) {
			double x = kotlHologram.getDouble("x");
			double y = kotlHologram.getDouble("y");
			double z = kotlHologram.getDouble("z");

			Log.info(getName(), "Setting up KOTL hologram at X: " + x + " Y: " + y + " Z: " + z);
			Lobby.getInstance().setupKOTLHologram(x, y, z);
			Lobby.getInstance().setKOTLHologramLines(kotlHologram.getInt("lines"));
		}

		ConfigurationSection playerLeaderboard = getConfig().getConfigurationSection("lobby_player_leaderboard");
		ConfigurationSection teamLeaderboard = getConfig().getConfigurationSection("lobby_team_leaderboard");

		TSLeaderboard.getInstance().setLines(8);

		TSLeaderboard.getInstance().setPlayerHologramLocation(new Location(Lobby.getInstance().getWorld(), playerLeaderboard.getDouble("x"), playerLeaderboard.getDouble("y"), playerLeaderboard.getDouble("z")));
		TSLeaderboard.getInstance().setTeamHologramLocation(new Location(Lobby.getInstance().getWorld(), teamLeaderboard.getDouble("x"), teamLeaderboard.getDouble("y"), teamLeaderboard.getDouble("z")));

		CommandRegistry.registerCommand(new AcceptDuelCommand());
		CommandRegistry.registerCommand(new DuelCommand());
		CommandRegistry.registerCommand(new MissileWars(this));

		if (TournamentSystem.getInstance().isCelebrationMode()) {
			ModuleManager.enable(LobbyCelebrationMode.class);
			CommandRegistry.registerCommand(new GiveMeFireworksCommand());
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

		ConfigurationSection hallOfFame = getConfig().getConfigurationSection("hall_of_fame");

		if (hallOfFame.getBoolean("enabled")) {
			String url = hallOfFame.getString("stats_url");
			XYZLocation nameHologramLocation = new XYZLocation(hallOfFame.getConfigurationSection("name_hologram_location"));

			List<HallOfFameNPC> npcs = new ArrayList<>();

			File npcLocationFile = new File(getDataFolder().getAbsolutePath() + File.separator + "hall_of_fame_npcs.json");

			if (npcLocationFile.exists()) {
				JSONArray npcLocations;
				try {
					npcLocations = JSONFileUtils.readJSONArrayFromFile(npcLocationFile);

					for (int i = 0; i < npcLocations.length(); i++) {
						Location location = LocationUtils.fromJSONObject(npcLocations.getJSONObject(i), Lobby.getInstance().getWorld());

						npcs.add(new HallOfFameNPC(location));
					}
				} catch (JSONException | IOException e) {
					e.printStackTrace();
					Log.error("TournamentLobby", "Failed to read hall_of_fame_npcs.json in data directory. Hall of fame NPCs wont spawn");
				}

			} else {
				Log.error("TournamentLobby", "Missing hall_of_fame_npcs.json in data directory. Hall of fame NPCs wont spawn");
			}

			boolean debug = hallOfFame.getBoolean("debug");

			HallOfFameConfig hallOfFameConfig = new HallOfFameConfig(nameHologramLocation, npcs, url, debug);

			new BukkitRunnable() {
				@Override
				public void run() {
					HallOfFame hof = (HallOfFame) ModuleManager.getModule(HallOfFame.class);
					hof.init(hallOfFameConfig);
				}
			}.runTaskLater(this, 20L);
		}

		TabListMessage.setServerType("Lobby");
	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}
}