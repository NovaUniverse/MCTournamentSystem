package net.novauniverse.mctournamentsystem.spigot.modules.playermessages;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.mctournamentsystem.spigot.utils.PlayerMessages;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class PlayerMessageManager extends NovaModule implements Listener {
	public PlayerMessageManager() {
		super("TournamentSystem.PlayerMessageManager");
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();

		e.setJoinMessage(PlayerMessages.getJoinMessage(player));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player player = e.getPlayer();

		e.setQuitMessage(PlayerMessages.getLeaveMessage(player));
	}
}