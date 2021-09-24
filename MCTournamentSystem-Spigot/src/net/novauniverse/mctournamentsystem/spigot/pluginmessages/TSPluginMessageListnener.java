package net.novauniverse.mctournamentsystem.spigot.pluginmessages;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.modules.game.GameManager;

public class TSPluginMessageListnener implements PluginMessageListener {
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase(TournamentSystemCommons.DATA_CHANNEL)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();
			
			Log.trace("Received message on sub channel ", subchannel);

			switch (subchannel.toLowerCase()) {
			case "start_game":
				if (NovaCore.isNovaGameEngineEnabled()) {
					if (GameManager.getInstance().isEnabled()) {
						if (GameManager.getInstance().hasGame()) {
							if (!GameManager.getInstance().getCountdown().hasCountdownStarted() && !GameManager.getInstance().getCountdown().hasCountdownFinished()) {
								Log.info("TSPluginMessageListnener", "Starting countdown");
								GameManager.getInstance().getCountdown().startCountdown();
							}
						}
					}
				}
				break;

			default:
				Log.warn("TSPluginMessageListnener", "Reveived invalid sub channel: " + subchannel);
				break;
			}
		}
	}
}