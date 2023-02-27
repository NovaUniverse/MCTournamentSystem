package net.novauniverse.mctournamentsystem.bungeecord.listener.playertelementry;

import java.util.HashMap;
import java.util.UUID;

import org.json.JSONObject;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class PlayerTelementryManager implements Listener {
	private HashMap<UUID, PlayerTelementryData> data;

	public PlayerTelementryManager() {
		data = new HashMap<>();
	}

	public HashMap<UUID, PlayerTelementryData> getData() {
		return data;
	}

	@EventHandler
	public void onPlayerJoin(PostLoginEvent e) {
		data.put(e.getPlayer().getUniqueId(), new PlayerTelementryData(e.getPlayer().getUniqueId(), e.getPlayer().getName(), ""));
	}

	@EventHandler
	public void onPlayerJoin(PlayerDisconnectEvent e) {
		data.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPluginMessage(PluginMessageEvent e) {
		if (e.getTag().equalsIgnoreCase(TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL)) {
			e.setCancelled(true);
			try {
				if (e.getReceiver() instanceof ProxiedPlayer) {
					ProxiedPlayer player = (ProxiedPlayer) e.getReceiver();
					if (e.getSender() instanceof Server) {
						if (data.containsKey(player.getUniqueId())) {
							PlayerTelementryData pData = data.get(player.getUniqueId());

							ByteArrayDataInput in = ByteStreams.newDataInput(e.getData());

							pData.setHealth(in.readDouble());
							pData.setMaxHealth(in.readDouble());

							pData.setFood(in.readInt());
							pData.setClosestEnemyDistance(in.readInt());

							pData.setGamemode(in.readUTF());

							pData.setGameEnabled(in.readBoolean());
							pData.setInGame(in.readBoolean());

							pData.setServer(player.getServer().getInfo().getName());

							JSONObject metadata = new JSONObject(in.readUTF());

							pData.setMetadata(metadata);
						}

					} else {
						Log.warn("Received player telementry data from non ProxyServer source: " + e.getSender().toString());
					}
				} else {
					Log.warn("Received player telementry data with non ProxiedPlayer receiver: " + e.getReceiver().toString());
				}
			} catch (Exception ex) {
				Log.error("Failed to read player telementry data from " + e.getSender() + " send to " + e.getReceiver());
				ex.printStackTrace();
			}
		}
	}
}