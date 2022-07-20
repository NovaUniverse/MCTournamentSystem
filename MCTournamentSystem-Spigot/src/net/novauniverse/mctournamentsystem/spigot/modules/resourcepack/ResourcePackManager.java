package net.novauniverse.mctournamentsystem.spigot.modules.resourcepack;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = false)
public class ResourcePackManager extends NovaModule implements Listener {
	public ResourcePackManager() {
		super("TournamentSystem.ResourcePackManager");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		String url = TournamentSystem.getInstance().getResourcePackUrl();
		if (url != null) {
			e.getPlayer().setResourcePack(url);
		}
	}
}