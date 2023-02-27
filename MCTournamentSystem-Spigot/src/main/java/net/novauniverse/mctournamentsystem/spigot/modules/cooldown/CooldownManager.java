package net.novauniverse.mctournamentsystem.spigot.modules.cooldown;

import java.util.ArrayList;
import java.util.List;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class CooldownManager extends NovaModule {
	private static CooldownManager instance;

	private List<TickCooldown> cooldowns;
	private Task tickTask;

	public static CooldownManager getInstance() {
		return instance;
	}

	public CooldownManager() {
		super("TournamentSystem.CooldownManager");
	}

	public void addCooldown(TickCooldown cooldown) {
		cooldowns.add(cooldown);
	}

	@Override
	public void onLoad() {
		CooldownManager.instance = this;
		cooldowns = new ArrayList<TickCooldown>();
		tickTask = new SimpleTask(TournamentSystem.getInstance(), () -> {
			cooldowns.forEach(TickCooldown::decrement);
			cooldowns.removeIf(TickCooldown::isCompleted);
		}, 0L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(tickTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(tickTask);
	}
}