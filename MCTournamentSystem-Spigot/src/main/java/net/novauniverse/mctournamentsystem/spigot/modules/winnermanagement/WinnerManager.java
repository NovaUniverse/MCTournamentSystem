package net.novauniverse.mctournamentsystem.spigot.modules.winnermanagement;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import net.novauniverse.mctournamentsystem.commons.winner.LockedWinnerManagement;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class WinnerManager extends NovaModule {
	private int winner;
	private Task task;

	public WinnerManager() {
		super("TournamentSystem.WinnerManager");
	}

	@Override
	public void onLoad() {
		winner = -1;
		task = new SimpleTask(() -> {
			this.pollState();
		}, 20L * 60);
	}

	@Override
	public void onPostEnable() {
		this.pollState();
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	public void pollState() {
		if (!isEnabled()) {
			return;
		}

		AsyncManager.runAsync(() -> {
			try {
				int newState = LockedWinnerManagement.getLockedWinner();
				AsyncManager.runSync(() -> {
					if (winner != newState) {
						int old = winner;
						winner = newState;
						Log.info("WinnerManager", "Locked winner changed from " + old + " to " + winner);
						Event event = new WinnerChangeEvent(old, winner);
						Bukkit.getServer().getPluginManager().callEvent(event);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("WinnerManager", "Failed to poll state. " + e.getClass().getName() + " " + e.getMessage());
			}
		});
	}
}