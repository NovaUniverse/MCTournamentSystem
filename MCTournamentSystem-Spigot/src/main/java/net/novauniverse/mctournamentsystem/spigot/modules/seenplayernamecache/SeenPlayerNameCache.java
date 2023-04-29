package net.novauniverse.mctournamentsystem.spigot.modules.seenplayernamecache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class SeenPlayerNameCache extends NovaModule implements Listener {
	private final Map<UUID, String> seenPlayerNames;

	public SeenPlayerNameCache() {
		super("TournamentSystem.SeenPlayerCache");
		seenPlayerNames = new HashMap<>();
	}

	@Override
	public void onEnable() throws Exception {
		Bukkit.getOnlinePlayers().forEach(p -> seenPlayerNames.put(p.getUniqueId(), p.getName()));
	}

	@Override
	public void onDisable() throws Exception {
		seenPlayerNames.clear();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		seenPlayerNames.put(player.getUniqueId(), player.getName());
	}

	@Nullable
	public String getPlayerName(@Nonnull UUID uuid) {
		return seenPlayerNames.get(uuid);
	}
	
	public Map<UUID, String> getSeenPlayerNames() {
		return seenPlayerNames;
	}
}