package net.novauniverse.mctournamentsystem.spigot.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.BingoManager;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.SpleefManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameLoadedEvent;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class GameListeners extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "ts.GameListeners";
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameLoaded(GameLoadedEvent e) {
		NetherBoardScoreboard.getInstance().setGlobalLine(0, ChatColor.YELLOW + "" + ChatColor.BOLD + GameManager.getInstance().getDisplayName());

		if (e.getGame().getName().equalsIgnoreCase("bingo")) {
			if (ModuleManager.loadModule(BingoManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: BingoManager (" + BingoManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: BingoManager (" + BingoManager.class.getName() + ")");
			}
		}

		if (e.getGame().getName().equalsIgnoreCase("spleef")) {
			if (ModuleManager.loadModule(SpleefManager.class, true)) {
				Log.success(getName(), "Enabled game specific module: SpleefManager (" + SpleefManager.class.getName() + ")");
			} else {
				Log.error(getName(), "Failed to enable game specific module: SpleefManager (" + SpleefManager.class.getName() + ")");
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
}