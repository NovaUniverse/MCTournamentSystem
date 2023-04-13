package net.novauniverse.mctournamentsystem.spigot.modules.playerprefix;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class PlayerPrefixManager extends NovaModule implements Listener {
	private Map<Player, String> prefixes;

	public PlayerPrefixManager() {
		super("TournamentSystem.PlayerPrefixManager");
	}

	@Override
	public void onLoad() {
		prefixes = new HashMap<>();
	}

	@Override
	public void onDisable() throws Exception {
		prefixes.clear();
	}

	public boolean hasPrefix(Player player) {
		return prefixes.containsKey(player);
	}

	@Nullable
	public String getPrefix(Player player) {
		return prefixes.get(player);
	}

	public void setPrefix(Player player, @Nullable String prefix) {
		if (prefix == null) {
			removePrefix(player);
			return;
		}
		prefixes.put(player, prefix);
		updateDisplayName(player);
	}

	public void removePrefix(Player player) {
		prefixes.remove(player);
		updateDisplayName(player);
	}

	public void updateDisplayName(Player player) {
		TournamentSystemTeamManager.getInstance().updatePlayerName(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		prefixes.remove(player);
	}
}