package net.novauniverse.mctournamentsystem.missilewars.lobby.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import net.novauniverse.mctournamentsystem.missilewars.lobby.utils.TextUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.ModifiableTextLine;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class MissilewarsScoreboard extends NovaModule {
	private static MissilewarsScoreboard instance;

	public static MissilewarsScoreboard getInstance() {
		return instance;
	}

	private Task task;

	private ModifiableTextLine tpsLine;

	public MissilewarsScoreboard() {
		super("TournamentSystem.MissileWars.MissilewarsScoreboard");
	}

	@Override
	public void onLoad() {
		MissilewarsScoreboard.instance = this;
		tpsLine = new ModifiableTextLine("");
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
					tpsLine.setText(ChatColor.GOLD + "TPS: " + TextUtils.formatTps(tps));
				} else {
					tpsLine.setText(ChatColor.GOLD + "TPS: " + ChatColor.AQUA + "--");
				}

				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);
					NovaScoreboardManager.getInstance().setPlayerLine(player, 6, new StaticTextLine(ChatColor.GOLD + "Ping: " + TextUtils.formatPing(ping)));
				});
			}
		}, 20L);
	}

	@Override
	public void onEnable() throws Exception {
		NovaScoreboardManager.getInstance().setGlobalLine(5, tpsLine);
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}