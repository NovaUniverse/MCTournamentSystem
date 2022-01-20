package net.novauniverse.mctournamentsystem.missilewars.lobby.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.novauniverse.mctournamentsystem.missilewars.lobby.utils.TextUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class NovaScoreboard extends NovaModule {
	private static NovaScoreboard instance;
	
	public static NovaScoreboard getInstance() {
		return instance;
	}
	
	private Task task;

	@Override
	public String getName() {
		return "MissilewarsScoreboard";
	}

	@Override
	public void onLoad() {
		NovaScoreboard.instance = this;
		task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				
				double tps = -1;
				try {
					tps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps()[0];
				} catch (Exception e) {
					Log.trace("TabList", "Failed to fetch server ping " + e.getClass().getName() + " " + e.getMessage());
				}

				if (tps != -1) {
					NetherBoardScoreboard.getInstance().setGlobalLine(13, ChatColor.GOLD + "TPS: " + TextUtils.formatTps(tps));
				} else {
					NetherBoardScoreboard.getInstance().setGlobalLine(13, ChatColor.GOLD + "TPS: " + ChatColor.AQUA + "--");
				}

				for (Player player : Bukkit.getServer().getOnlinePlayers()) {
					int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);
					NetherBoardScoreboard.getInstance().setPlayerLine(12, player, ChatColor.GOLD + "Ping: " + TextUtils.formatPing(ping));
				}
			}
		}, 20L);
	}

	@Override
	public void onEnable() throws Exception {
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}