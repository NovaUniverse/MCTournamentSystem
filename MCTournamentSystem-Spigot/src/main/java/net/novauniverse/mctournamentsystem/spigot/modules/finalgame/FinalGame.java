package net.novauniverse.mctournamentsystem.spigot.modules.finalgame;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.winner.LockedWinnerManagement;
import net.novauniverse.mctournamentsystem.spigot.team.DefaultTopTeamProvider;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.Pair;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.bossbar.NovaBossBar;
import net.zeeraa.novacore.spigot.abstraction.bossbar.NovaBossBar.NovaBarColor;
import net.zeeraa.novacore.spigot.abstraction.bossbar.NovaBossBar.NovaBarStyle;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.Game;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.TeamWinEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.GameLobby;
import net.zeeraa.novacore.spigot.gameengine.module.modules.gamelobby.events.GameLobbyStartingEvent;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = false)
public class FinalGame extends NovaModule implements Listener {
	private NovaBossBar bossBar;

	public FinalGame() {
		super("TournamentSystem.FinalGame");
		addDependency(GameLobby.class);
		addDependency(GameManager.class);
	}

	@Override
	public void onEnable() throws Exception {
		ModuleManager.getModule(GameLobby.class).setDisableAutoAddPlayers(true);
		if (!TeamManager.hasTeamManager()) {
			throw new RuntimeException("Tried to enable final game without a active team manager");
		}
		this.bossBar = VersionIndependentUtils.get().createBossBar(ChatColor.RED + ChatColor.BOLD.toString() + "Final 1v1");
		this.bossBar.setColor(NovaBarColor.RED);
		this.bossBar.setStyle(NovaBarStyle.SOLID);
		this.bossBar.setProgress(1);
		
		Bukkit.getServer().getOnlinePlayers().forEach(bossBar::addPlayer);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onGameLobbyStarting(GameLobbyStartingEvent e) {
		if (!DefaultTopTeamProvider.getTopParticipants().toList().stream().allMatch(Team::hasOnlineMembersInThisServer)) {
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "Could not start since one or more of the top teams are not online");
			e.setCancelled(true);
			return;
		}

		Game game = ModuleManager.getModule(GameManager.class).getActiveGame();

		Pair<Team> teams = DefaultTopTeamProvider.getTopParticipants();

		teams.toList().forEach(team -> {
			team.getOnlinePlayers().forEach(game::addPlayer);
		});

		bossBar.setText(teams.getObject1().getTeamColor() + teams.getObject1().getDisplayName() + ChatColor.WHITE + " vs " + teams.getObject2().getTeamColor() + teams.getObject2().getDisplayName());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onTeamWin(TeamWinEvent e) {
		TournamentSystemTeam team = (TournamentSystemTeam) e.getTeam();
		try {
			LockedWinnerManagement.lockWinner(team.getTeamNumber());
			bossBar.setText(ChatColor.GREEN + "Winner: " + team.getTeamColor() + team.getDisplayName());
		} catch (SQLException e1) {
			e1.printStackTrace();
			Log.error("FinalGame", "Failed to set winner. Please manually set winner team to " + team.getDisplayName() + ". (" + team.getTeamNumber() + "). " + e1.getClass().getName() + " " + e1.getMessage());
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		bossBar.addPlayer(e.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		bossBar.removePlayer(e.getPlayer());
	}
}
