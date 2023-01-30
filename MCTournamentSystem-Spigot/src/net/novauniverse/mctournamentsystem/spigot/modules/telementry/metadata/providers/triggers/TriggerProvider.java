package net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.providers.triggers;

import org.bukkit.entity.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.ITickingGameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.ScheduledGameTrigger;

public class TriggerProvider implements ITelementryMetadataProvider {
	private GameManager gameManager;

	public TriggerProvider() {
		gameManager = GameManager.getInstance();
	}

	@Override
	public void process(Player player, JSONObject metadata) {
		JSONArray triggers = new JSONArray();

		if (NovaCore.isNovaGameEngineEnabled()) {
			if (gameManager.hasGame()) {
				gameManager.getActiveGame().getTriggers().forEach(trigger -> {
					JSONObject triggerData = new JSONObject();
					JSONArray flags = new JSONArray();

					trigger.getFlags().forEach(f -> flags.put(f.name()));

					triggerData.put("name", trigger.getName());
					triggerData.put("type", trigger.getType());
					triggerData.put("trigger_count", trigger.getTriggerCount());
					triggerData.put("flags", flags);

					if (trigger instanceof ScheduledGameTrigger) {
						triggerData.put("running", ((ScheduledGameTrigger) trigger).isRunning());
					}

					if (trigger instanceof ITickingGameTrigger) {
						triggerData.put("ticks_left", ((ITickingGameTrigger) trigger).getTicksLeft());
					}

					triggers.put(triggerData);
				});
			}
		}

		metadata.put("triggers", triggers);
	}
}