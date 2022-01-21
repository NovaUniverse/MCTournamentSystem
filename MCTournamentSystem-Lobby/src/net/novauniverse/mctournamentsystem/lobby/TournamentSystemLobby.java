package net.novauniverse.mctournamentsystem.lobby;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.lobby.command.duel.AcceptDuelCommand;
import net.novauniverse.mctournamentsystem.lobby.command.duel.DuelCommand;
import net.novauniverse.mctournamentsystem.lobby.command.missilewars.MissileWars;
import net.novauniverse.mctournamentsystem.lobby.modules.lobby.TSLobby;
import net.novauniverse.mctournamentsystem.lobby.modules.scoreboard.TSLeaderboard;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.module.ModuleManager;

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
	public void onLoad() {
		TournamentSystemLobby.instance = this;
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();

		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.lobby.modules");

		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		if (getConfig().getBoolean("prevent_damage_mobs")) {
			preventDamageMobs = true;
		}

		lobbyLocation = new Location(TSLobby.getInstance().getWorld(), getConfig().getDouble("spawn_x"), getConfig().getDouble("spawn_y"), getConfig().getDouble("spawn_z"), (float) getConfig().getDouble("spawn_yaw"), (float) getConfig().getDouble("spawn_pitch"));
		TSLobby.getInstance().setLobbyLocation(lobbyLocation);

		TSLobby.getInstance().setKOTLLocation(getConfig().getDouble("kotl_x"), getConfig().getDouble("kotl_z"), getConfig().getDouble("kotl_radius"));

		ConfigurationSection playerLeaderboard = getConfig().getConfigurationSection("lobby_player_leaderboard");
		ConfigurationSection teamLeaderboard = getConfig().getConfigurationSection("lobby_team_leaderboard");

		TSLeaderboard.getInstance().setLines(8);

		TSLeaderboard.getInstance().setPlayerHologramLocation(new Location(TSLobby.getInstance().getWorld(), playerLeaderboard.getDouble("x"), playerLeaderboard.getDouble("y"), playerLeaderboard.getDouble("z")));
		TSLeaderboard.getInstance().setTeamHologramLocation(new Location(TSLobby.getInstance().getWorld(), teamLeaderboard.getDouble("x"), teamLeaderboard.getDouble("y"), teamLeaderboard.getDouble("z")));

		CommandRegistry.registerCommand(new AcceptDuelCommand());
		CommandRegistry.registerCommand(new DuelCommand());
		CommandRegistry.registerCommand(new MissileWars(this));

		/* ----- Misc ----- */
		new BukkitRunnable() {
			@Override
			public void run() {
				/* ----- Run after load ----- */
				System.out.println(lobbyLocation);
			}
		}.runTask(this);
	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
	}
}