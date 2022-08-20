package net.novauniverse.mctournamentsystem.spigot.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bedwars.BedwarsManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.behindyourtail.BehindYourTailManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bingo.BingoManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.chickenout.ChickenOutManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.dropper.DropperManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.hive.HiveManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.parkourrace.ParkourRaceManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.spleef.SpleefManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.survivalgames.SurvivalGamesManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.tnttag.TNTTagManager;
import net.novauniverse.mctournamentsystem.spigot.game.util.PlayerEliminatedTitleProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.tablistmessage.TabListMessage;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.PlayerTelementryManager;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.behindyourtail.BehindYourTailMetadataProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.common.PlayerYMetadataProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.tnttag.TNTTagMetadataProvider;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.DefaultGameCountdownStartEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameBeginEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.mapselector.selectors.guivoteselector.GUIMapSelectorPlayerVotedEvent;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class GameListeners extends NovaModule implements Listener {
	public static final Map<String, Class<? extends NovaModule>> GAME_SPECIFIC_MODULES = new HashMap<>();
	public static final List<GameSpecificTelementryModule> TELEMENTRY_METADATA_PROVIDERS = new ArrayList<>();

	static {
		// Original MCF games
		GAME_SPECIFIC_MODULES.put("survivalgames", SurvivalGamesManager.class);
		GAME_SPECIFIC_MODULES.put("bingo", BingoManager.class);
		GAME_SPECIFIC_MODULES.put("spleef", SpleefManager.class);
		GAME_SPECIFIC_MODULES.put("dropper", DropperManager.class);
		GAME_SPECIFIC_MODULES.put("tnttag", TNTTagManager.class);
		GAME_SPECIFIC_MODULES.put("nova_bedwars", BedwarsManager.class);

		// NovaGames games
		GAME_SPECIFIC_MODULES.put("chickenout", ChickenOutManager.class);
		GAME_SPECIFIC_MODULES.put("behindyourtail", BehindYourTailManager.class);
		GAME_SPECIFIC_MODULES.put("ng_hive", HiveManager.class);
		GAME_SPECIFIC_MODULES.put("parkour_race", ParkourRaceManager.class);
	}

	static {
		// TNT tag state
		TELEMENTRY_METADATA_PROVIDERS.add(new GameSpecificTelementryModule("tnttag", TNTTagMetadataProvider.class));

		// Y location
		TELEMENTRY_METADATA_PROVIDERS.add(new GameSpecificTelementryModule("tntrun", PlayerYMetadataProvider.class));
		TELEMENTRY_METADATA_PROVIDERS.add(new GameSpecificTelementryModule("spleef", PlayerYMetadataProvider.class));

		// Hunter or fox
		TELEMENTRY_METADATA_PROVIDERS.add(new GameSpecificTelementryModule("behindyourtail", BehindYourTailMetadataProvider.class));
	}

	public GameListeners() {
		super("TournamentSystem.GameListeners");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoaded(GameLoadedEvent e) {
		NetherBoardScoreboard.getInstance().setGlobalLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + GameManager.getInstance().getDisplayName());
		TabListMessage.setServerType(GameManager.getInstance().getDisplayName());

		String name = e.getGame().getName().toLowerCase();
		if (GAME_SPECIFIC_MODULES.containsKey(name)) {
			Class<? extends NovaModule> clazz = GAME_SPECIFIC_MODULES.get(name);
			if (ModuleManager.loadModule(TournamentSystem.getInstance(), clazz, true)) {
				Log.success(getName(), "Enabled game specific module: " + clazz.getName());
			} else {
				Log.error(getName(), "Failed to enable game specific module: " + clazz.getName());
			}
		}

		List<GameSpecificTelementryModule> providersToLoad = TELEMENTRY_METADATA_PROVIDERS.stream().filter(p -> p.getGameName().equals(name)).collect(Collectors.toList());
		providersToLoad.forEach(p -> {
			Class<? extends ITelementryMetadataProvider> clazz = p.getProviderClass();

			if (p.isSensitive()) {
				if (!TournamentSystem.getInstance().isShowSensitiveTelementryData()) {
					Log.info(getName(), "Ignoring metadata provider " + clazz.getName() + " since sentitive metadata is disabled");
					return;
				}
			}

			try {
				ITelementryMetadataProvider provider = clazz.getConstructor().newInstance();

				Log.info(getName(), "Adding telementry metadata provider " + provider.getClass().getName());

				PlayerTelementryManager.getInstance().addMetadataProvider(provider);
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.error(getName(), "Failed to enable telementry metadata provider: " + clazz.getName());
			}
		});

		Bukkit.getServer().getWorlds().forEach(world -> world.setAutoSave(false));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameBegin(GameBeginEvent e) {
		Bukkit.getServer().getWorlds().forEach(world -> world.setAutoSave(false));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onDefaultGameCountdownStart(DefaultGameCountdownStartEvent e) {
		try {
			TournamentSystemCommons.setActiveServer(TournamentSystem.getInstance().getServerName());
		} catch (Exception ex) {
			Log.error("Failed to set active server name");
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		try {
			TournamentSystemCommons.setActiveServer(TournamentSystem.getInstance().getServerName());
		} catch (Exception ex) {
			Log.error("Failed to set active server name");
			ex.printStackTrace();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		e.getTeam().getMembers().forEach(uuid -> {
			Player player = Bukkit.getServer().getPlayer(uuid);
			if (player != null) {
				VersionIndependentUtils.get().sendTitle(player, ChatColor.GREEN + "Winner", ChatColor.GREEN + "Your team won", 10, 40, 10);
				// player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 2F);
				VersionIndependentSound.ORB_PICKUP.play(player, 1F, 2F);
			}
		});
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (e.getPlayer().isOnline()) {
			Player player = e.getPlayer().getPlayer();

			if (e.getReason() == PlayerEliminationReason.COMMAND) {
				player.setGameMode(GameMode.SPECTATOR);
			}

			// player.playSound(player.getLocation(), Sound.WITHER_HURT, 1F, 1F);
			VersionIndependentSound.WITHER_HURT.play(player);

			if (TournamentSystem.getInstance().isEliminationTitleMessageEnabled()) {
				PlayerEliminatedTitleProvider provider = TournamentSystem.getInstance().getPlayerEliminatedTitleProvider();
				if (provider != null) {
					provider.show(e);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		try {
			TournamentSystemCommons.setActiveServer(null);
		} catch (Exception ex) {
			Log.error("Failed to reset active server name");
			ex.printStackTrace();
		}

		Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> player.sendMessage(LanguageManager.getString(player, "tournamentsystem.game.sending_you_to_lobby_20_seconds")));

				Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentSystem.getInstance(), new Runnable() {
					@Override
					public void run() {
						for (Player player : Bukkit.getServer().getOnlinePlayers()) {
							Bukkit.getScheduler().runTaskLater(TournamentSystem.getInstance(), new Runnable() {
								@Override
								public void run() {
									BungeecordUtils.sendToServer(player, TournamentSystem.getInstance().getLobbyServer());
								}
							}, 4L);
						}

						Bukkit.getScheduler().scheduleSyncDelayedTask(TournamentSystem.getInstance(), new Runnable() {
							@Override
							public void run() {
								Bukkit.getServer().getOnlinePlayers().forEach(player -> player.kickPlayer(LanguageManager.getString(player, "tournamentsystem.game.server.restarting", e.getGame())));
								Bukkit.getServer().shutdown();
							}
						}, 200L);
					}
				}, 400L);
			}

		}, 100L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGUIMapSelectorPlayerVoted(GUIMapSelectorPlayerVotedEvent e) {
		Player player = e.getPlayer();
		VersionIndependentUtils.getInstance().sendTitle(player, "", ChatColor.GOLD + "Voted for " + e.getMap().getDisplayName(), 10, 40, 10);
	}
}