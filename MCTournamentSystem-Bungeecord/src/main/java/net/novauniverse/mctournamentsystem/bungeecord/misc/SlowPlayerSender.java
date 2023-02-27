package net.novauniverse.mctournamentsystem.bungeecord.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class SlowPlayerSender implements Listener {
	public static final long SEND_DELAY = 500;

	private ServerInfo targetServer;
	private List<ProxiedPlayer> players;

	private ScheduledTask task;

	public SlowPlayerSender(Plugin plugin) {
		players = new ArrayList<>();
		targetServer = null;
		task = ProxyServer.getInstance().getScheduler().schedule(plugin, new Runnable() {
			@Override
			public void run() {
				if (targetServer != null) {
					if (players.size() > 0) {
						ProxiedPlayer player = players.remove(0);
						if (player.getServer().getInfo().getName().equals(targetServer.getName())) {
							return;
						}

						player.connect(targetServer);
					}
				}
			}
		}, SEND_DELAY, SEND_DELAY, TimeUnit.MILLISECONDS);
	}

	public void sendAll(ServerInfo targetServer) {
		this.cancel();
		this.targetServer = targetServer;

		players.addAll(ProxyServer.getInstance().getPlayers().stream().filter(p -> !p.getServer().getInfo().getName().equals(targetServer.getName())).collect(Collectors.toList()));
	}

	public void cancel() {
		players.clear();
	}

	public void destroy() {
		this.cancel();
		task.cancel();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDisconnect(PlayerDisconnectEvent e) {
		players.removeIf(p -> p.getUniqueId().equals(e.getPlayer().getUniqueId()));
	}
}