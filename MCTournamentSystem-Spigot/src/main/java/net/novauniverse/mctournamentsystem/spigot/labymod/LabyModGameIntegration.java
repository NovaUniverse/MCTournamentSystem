package net.novauniverse.mctournamentsystem.spigot.labymod;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.labymod.serverapi.api.LabyAPI;
import net.zeeraa.novacore.commons.utils.DelayedRunner;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class LabyModGameIntegration extends NovaModule implements Listener {
	public LabyModGameIntegration() {
		super("TournamentSystem.LabyMod.Game");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {
		if (e.getPlayer().isOnline()) {
			DelayedRunner.runDelayed(() -> {
				LabyAPI.getService().getCineScopesTransmitter().transmit(e.getPlayer().getUniqueId(), true, 33, 50);
				DelayedRunner.runDelayed(() -> {
					LabyAPI.getService().getCineScopesTransmitter().transmit(e.getPlayer().getUniqueId(), true, 0, 50);
				}, 60);
			}, 10);
		}
	}
}