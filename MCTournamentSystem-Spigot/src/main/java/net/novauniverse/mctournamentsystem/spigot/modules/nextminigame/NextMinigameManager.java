package net.novauniverse.mctournamentsystem.spigot.modules.nextminigame;

import javax.annotation.Nullable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.tasks.TaskExecutionMode;

@NovaAutoLoad(shouldEnable = true)
public class NextMinigameManager extends NovaModule {
	private static NextMinigameManager instance;

	private SimpleTask task;
	private String nextMinigame;

	public static NextMinigameManager getInstance() {
		return instance;
	}

	public @Nullable String getNextMinigame() {
		return nextMinigame;
	}

	public NextMinigameManager() {
		super("TournamentSystem.NextMinigameManager");
	}

	@Override
	public void onLoad() {
		NextMinigameManager.instance = this;
		task = new SimpleTask(getPlugin(), () -> {
			nextMinigame = TournamentSystemCommons.getNextMinigame();
		}, 100L);
		task.setTaskExecutionMode(TaskExecutionMode.ASYNCHRONOUS);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}