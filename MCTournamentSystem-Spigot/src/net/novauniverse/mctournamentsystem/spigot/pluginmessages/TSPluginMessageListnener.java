package net.novauniverse.mctournamentsystem.spigot.pluginmessages;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
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

			case "commentator_tp":
				UUID commentatorUuid = UUID.fromString(in.readUTF());
				UUID commentatorTargetUuid = UUID.fromString(in.readUTF());

				Player commentator = Bukkit.getPlayer(commentatorUuid);
				Player commentatorTarget = Bukkit.getPlayer(commentatorTargetUuid);

				if (commentator != null && commentatorTarget != null) {
					if (commentator.isOnline() && commentatorTarget.isOnline()) {
						commentator.teleport(commentatorTarget, TeleportCause.PLUGIN);
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