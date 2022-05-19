package net.novauniverse.mctournamentsystem.spigot.game;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.BingoManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.DropperManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.SpleefManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.SurvivalGamesManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.TNTTagManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependantSound;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
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
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class GameListeners extends NovaModule implements Listener {
	public GameListeners() {
		super("TournamentSystem.GameListeners");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoaded(GameLoadedEvent e) {
		NetherBoardScoreboard.getInstance().setGlobalLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + GameManager.getInstance().getDisplayName());

		if (e.getGame().getName().equalsIgnoreCase("survivalgames")) {
			if (ModuleManager.loadModule(TournamentSystem.getInstance(), SurvivalGamesManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: SurvivalGamesManager (" + SurvivalGamesManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: SurvivalGamesManager (" + SurvivalGamesManager.class.getName() + ")");
			}
		}

		if (e.getGame().getName().equalsIgnoreCase("bingo")) {
			if (ModuleManager.loadModule(TournamentSystem.getInstance(), BingoManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: BingoManager (" + BingoManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: BingoManager (" + BingoManager.class.getName() + ")");
			}
		}

		if (e.getGame().getName().equalsIgnoreCase("spleef")) {
			if (ModuleManager.loadModule(TournamentSystem.getInstance(), SpleefManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: SpleefManager (" + SpleefManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: SpleefManager (" + SpleefManager.class.getName() + ")");
			}
		}

		if (e.getGame().getName().equalsIgnoreCase("dropper")) {
			if (ModuleManager.loadModule(TournamentSystem.getInstance(), DropperManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: DropperManager (" + DropperManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: DropperManager (" + DropperManager.class.getName() + ")");
			}
		}

		if (e.getGame().getName().equalsIgnoreCase("tnttag")) {
			if (ModuleManager.loadModule(TournamentSystem.getInstance(), TNTTagManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: TNTTagManager (" + TNTTagManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: TNTTagManager (" + TNTTagManager.class.getName() + ")");
			}
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
				VersionIndependantUtils.get().sendTitle(player, ChatColor.GREEN + "Winner", ChatColor.GREEN + "Your team won", 10, 40, 10);
				player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1F, 2F);
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
			VersionIndependantSound.WITHER_HURT.play(player);

			String subtitle = ChatColor.RED + TextUtils.ordinal(e.getPlacement() + 1) + " place";

			Entity killerEntity = null;
			if (e.getKiller() != null) {
				if (e.getKiller() instanceof Projectile) {
					Entity theBoiWhoFirered = (Entity) ((Projectile) e.getKiller()).getShooter();

					if (theBoiWhoFirered != null) {
						killerEntity = theBoiWhoFirered;
					} else {
						killerEntity = e.getKiller();
					}
				} else {
					killerEntity = e.getKiller();
				}
			}

			ChatColor killerColor = ChatColor.RED;
			Team killerTeam = null;
			if (killerEntity != null) {
				if (killerEntity instanceof Player) {
					killerTeam = TeamManager.getTeamManager().getPlayerTeam((Player) killerEntity);
				}
			}

			if (killerTeam != null) {
				killerColor = killerTeam.getTeamColor();
			}

			switch (e.getReason()) {
			case KILLED:
				subtitle = ChatColor.RED + "Killed by " + killerColor + killerEntity.getName() + ChatColor.RED + ". " + TextUtils.ordinal(e.getPlacement() + 1) + " place";
				break;

			case COMMAND:
				subtitle = ChatColor.RED + "Eliminated by an admin";
				break;

			default:
				break;
			}

			VersionIndependantUtils.get().sendTitle(player, ChatColor.RED + "Eliminated", subtitle, 10, 60, 10);
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
				for (Player p : Bukkit.getServer().getOnlinePlayers()) {
					p.sendMessage(LanguageManager.getString(p, "tournamentsystem.game.sending_you_to_lobby_10_seconds"));
				}

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
								for (Player p : Bukkit.getServer().getOnlinePlayers()) {
									p.kickPlayer(LanguageManager.getString(p, "tournamentsystem.game.server.restarting", e.getGame()));
								}
								Bukkit.getServer().shutdown();
							}
						}, 40L);
					}
				}, 200L);
			}
		}, 100L);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onGUIMapSelectorPlayerVoted(GUIMapSelectorPlayerVotedEvent e) {
		Player player = e.getPlayer();
		VersionIndependantUtils.getInstance().sendTitle(player, "", ChatColor.GOLD + "Voted for " + e.getMap().getDisplayName(), 10, 40, 10);
	}
}