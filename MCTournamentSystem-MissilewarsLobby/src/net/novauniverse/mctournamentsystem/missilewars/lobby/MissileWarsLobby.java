package net.novauniverse.mctournamentsystem.missilewars.lobby;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.missilewars.lobby.command.hub.HubCommand;
import net.novauniverse.mctournamentsystem.missilewars.lobby.gamestarter.DefaultCountdownGameStarter;
import net.novauniverse.mctournamentsystem.missilewars.lobby.gamestarter.GameStarter;
import net.novauniverse.mctournamentsystem.missilewars.lobby.modules.GameStartScoreboardCountdown;
import net.novauniverse.mctournamentsystem.missilewars.lobby.modules.MissileWarsHandler;
import net.novauniverse.mctournamentsystem.missilewars.lobby.modules.NovaScoreboard;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.novaplugin.NovaPlugin;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class MissileWarsLobby extends NovaPlugin implements Listener {
	private static MissileWarsLobby instance;
	private GameStarter starter;

	public static MissileWarsLobby getInstance() {
		return instance;
	}

	public GameStarter getStarter() {
		return starter;
	}

	@Override
	public void onEnable() {
		MissileWarsLobby.instance = this;

		this.starter = new DefaultCountdownGameStarter();

		this.registerEvents(this);

		CommandRegistry.registerCommand(new HubCommand(this));

		requireModule(NetherBoardScoreboard.class);
		NetherBoardScoreboard.getInstance().setLineCount(5);
		NetherBoardScoreboard.getInstance().setDefaultTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "MissileWars");
		NetherBoardScoreboard.getInstance().setGlobalLine(3, ChatColor.GOLD + "Use " + ChatColor.AQUA + "/hub " + ChatColor.GOLD + "to get back");
		NetherBoardScoreboard.getInstance().setGlobalLine(4, ChatColor.GOLD + "to the tournament");

		ModuleManager.loadModule(NovaScoreboard.class, true);
		ModuleManager.loadModule(GameStartScoreboardCountdown.class, true);
		ModuleManager.loadModule(MissileWarsHandler.class, true);

		new BukkitRunnable() {
			@Override
			public void run() {
				starter.onEnable();
			}
		}.runTaskLater(this, 1L);
	}

	@Override
	public void onDisable() {
		this.cancelTasks(this);
		Bukkit.getScheduler().cancelTasks(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome to missilewars. In this minigame you will have to use the items you get to destroy your opponents shield. Use " + ChatColor.AQUA + ChatColor.BOLD + "/hub" + ChatColor.GOLD + ChatColor.BOLD + " to get back to the tournament lobby");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage(ChatColor.AQUA + "Sending you to the lobby in 15 seconds");
				new BukkitRunnable() {
					@Override
					public void run() {
						Bukkit.getServer().getOnlinePlayers().forEach(p -> {
							BungeecordUtils.sendToServer(p, "lobby");
						});
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getServer().getOnlinePlayers().forEach(p -> {
									p.kickPlayer(ChatColor.RED + "Failed to send you to the lobby. Please reconnect");
								});

								Bukkit.getServer().shutdown();
							}
						}.runTaskLater(getInstance(), 60L);
					}
				}.runTaskLater(getInstance(), 15 * 20);
			}
		}.runTaskLater(getInstance(), 20L);

	}
}