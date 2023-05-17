package net.novauniverse.mctournamentsystem.lobby.modules.bossbar;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.bossbar.NovaBossBar;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class LobbyBossBar extends NovaModule implements Listener {
	private NovaBossBar bar;

	public LobbyBossBar() {
		super("TournamentSystem.Lobby.LobbyBossBar");
	}

	@Override
	public void onLoad() {
		bar = VersionIndependentUtils.get().createBossBar("");
	}

	@Override
	public void onEnable() throws Exception {
		bar.setText(TournamentSystem.getInstance().getCachedTournamentName());
		Bukkit.getOnlinePlayers().forEach(bar::addPlayer);
	}

	@Override
	public void onDisable() throws Exception {
		bar.removePlayers();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		bar.addPlayer(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		bar.removePlayer(player);
	}
}
