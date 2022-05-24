package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;

import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.trait.SkinTrait;
import net.novauniverse.mctournamentsystem.lobby.TournamentSystemLobby;
import net.novauniverse.mctournamentsystem.lobby.modules.lobby.Lobby;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = false)
public class HallOfFame extends NovaModule {
	private boolean initialized;

	private Hologram nameHologram;

	private HallOfFameConfig config;

	private List<TournamentResult> cachedResults;

	private int index;

	private boolean debug;

	public HallOfFame() {
		super("TournamentSystem.Lobby.HallOfFame");
	}

	private Task task;

	@Override
	public void onLoad() {
		initialized = false;
		cachedResults = new ArrayList<>();
		index = -1;
		task = new SimpleTask(TournamentSystemLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				showNext();
			}
		}, 10 * 20);
		showNext();
	}

	@Override
	public void onEnable() throws Exception {
		if (initialized) {
			Task.tryStartTask(task);
			showNext();
		}
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
		
		if(initialized) {
			this.config.getNpcs().forEach(npc -> npc.getNPC().destroy());
			this.config.getNpcs().clear();
		}
	}

	public void init(HallOfFameConfig config) {
		this.config = config;
		this.initialized = true;
		this.debug = config.isDebug();

		nameHologram = HologramsAPI.createHologram(getPlugin(), config.getNameHologramLocation().toBukkitLocation(Lobby.getInstance().getWorld()));

		updateData();

		ModuleManager.enable(this.getClass());
	}

	public void showNext() {
		if (cachedResults.size() == 0) {
			return;
		}
		index++;
		if (index >= cachedResults.size()) {
			index = 0;
		}
		TournamentResult tournamentResult = cachedResults.get(index);
		TournamentTeamResult top = Collections.max(tournamentResult.getTeams(), Comparator.comparing(s -> s.getScore()));
		if (top != null) {
			if (debug) {
				Log.debug("HallOfFame", "Showing result for " + tournamentResult.getDisplayName());
				Log.debug("HallOfFame", "NPC count: " + config.getNpcs().size());
				Log.debug("HallOfFame", "Player count: " + top.getPlayers().size());
			}
			nameHologram.clearLines();
			nameHologram.appendTextLine(ChatColor.GREEN + tournamentResult.getDisplayName());

			for (int i = 0; i < config.getNpcs().size(); i++) {
				HallOfFameNPC npc = config.getNpcs().get(i);
				if (i < top.getPlayers().size()) {
					TournamentPlayer player = top.getPlayers().get(i);
					if (debug) {
						Log.debug("HallOfFame", "Showing npc " + npc.getNPC().getId() + " as " + player.getUsername());
					}
					if (!npc.getNPC().isSpawned()) {
						if (debug) {
							Log.debug("HallOfFame", "Spawning npc " + npc.getNPC().getId() + " at " + npc.getLocation());
						}
						npc.getNPC().spawn(npc.getLocation(), SpawnReason.PLUGIN);
					}
					try {
						npc.getNPC().setName(player.getUsername());
						// npc.getNPC().data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA,
						// player.getUsername());

						SkinTrait skinTrait = npc.getNPC().getOrAddTrait(SkinTrait.class);
						skinTrait.setSkinName(player.getUsername(), true);

					} catch (Exception e) {
						Log.warn("HallOfFame", "Failed to set npc name or skin");
						e.printStackTrace();
					}
				} else {
					if (npc.getNPC().isSpawned()) {
						if (debug) {
							Log.debug("HallOfFame", "Hiding npc " + npc.getNPC().getId());
						}
						npc.getNPC().despawn(DespawnReason.PLUGIN);
					}
				}
			}
		}
	}

	public void updateData() {
		if (initialized) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Log.info("HallOfFame", "Fetching data from from " + config.getUrl());
						cachedResults = ResultFetcher.fetch(config.getUrl());
						Log.info("HallOfFame", cachedResults.size() + " sessions loaded");
					} catch (Exception e) {
						Log.error("HallOfFame", "Failed to fetch hall of fame data from " + config.getUrl());
					}
				}
			}.runTaskAsynchronously(getPlugin());
		}
	}
}