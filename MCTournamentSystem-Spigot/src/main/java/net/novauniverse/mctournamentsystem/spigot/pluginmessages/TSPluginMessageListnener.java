package net.novauniverse.mctournamentsystem.spigot.pluginmessages;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.permissions.TournamentPermissions;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.GameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.TriggerFlag;

public class TSPluginMessageListnener implements PluginMessageListener {
	public static final int DEFAULT_REQUEST_EXPIRE_TIME = 10; // 10 seconds

	private List<ExpirablePluginMessageRequest> requests;

	public TSPluginMessageListnener() {
		requests = new ArrayList<ExpirablePluginMessageRequest>();
	}

	public boolean hasRequest(UUID requestId) {
		return requests.stream().filter(r -> r.getRequestId().equals(requestId)).findAny().isPresent();
	}

	public void addRequest(UUID requestId) {
		this.requests.add(new ExpirablePluginMessageRequest(requestId, TSPluginMessageListnener.DEFAULT_REQUEST_EXPIRE_TIME));
	}

	public void tickSecond() {
		requests.forEach(ExpirablePluginMessageRequest::decrement);
		requests.removeIf(ExpirablePluginMessageRequest::hasExpired);
	}

	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		if (channel.equalsIgnoreCase(TournamentSystemCommons.DATA_CHANNEL)) {
			ByteArrayDataInput in = ByteStreams.newDataInput(message);
			String subchannel = in.readUTF();

			// Log.trace("Received message on sub channel ", subchannel);

			switch (subchannel.toLowerCase()) {

			case "trigger":
				if (NovaCore.isNovaGameEngineEnabled()) {
					if (GameManager.getInstance().isEnabled()) {
						if (GameManager.getInstance().hasGame()) {
							UUID requestUUID = UUID.fromString(in.readUTF());
							if (this.hasRequest(requestUUID)) {
								return;
							}

							this.addRequest(requestUUID);

							String name = in.readUTF();
							String triggerSessionId = in.readUTF();

							if (!triggerSessionId.equalsIgnoreCase(TournamentSystemCommons.getSessionId().toString())) {
								break;
							}

							GameTrigger trigger = GameManager.getInstance().getActiveGame().getTrigger(name);

							trigger.trigger(TriggerFlag.COMMAND_ACTIVATION);
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
						if (commentator.hasPermission(TournamentPermissions.COMMENTATOR_PERMISSION)) {
							commentator.teleport(commentatorTarget, TeleportCause.PLUGIN);
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