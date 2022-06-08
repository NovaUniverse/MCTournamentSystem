package net.novauniverse.mctournamentsystem.spigot.modules.tablistmessage;

import org.bukkit.Bukkit;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.utils.RGBColorAnimation;
import net.novauniverse.mctournamentsystem.spigot.utils.TournamentUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class TabListMessage extends NovaModule {
	private Task task;
	private String serverType;
	private RGBColorAnimation rgbColorAnimation;

	public TabListMessage() {
		super("TournamentSystem.TabListMessage");
	}

	public static void setServerType(String serverType) {
		TabListMessage tlm = (TabListMessage) ModuleManager.getModule(TabListMessage.class);
		if (tlm == null) {
			Log.error("TabListMessage", "TabListMessage#setServerType(serverType) called before the module was loaded");
		} else {
			tlm.serverType = serverType;
		}
	}

	@Override
	public void onLoad() {
		serverType = "Unknown server type. Set it with TabListMessage#setServerType(serverType)";
		rgbColorAnimation = new RGBColorAnimation();
		this.task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				rgbColorAnimation.nextChatColor();
				update();
			}
		}, 10L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	public void update() {
		final double[] recentTps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps();

		String title = rgbColorAnimation.getChatColor() + "" + ChatColor.BOLD + ChatColor.stripColor(TournamentSystem.getInstance().getCachedTournamentName()) + ChatColor.WHITE + " - " + rgbColorAnimation.getChatColor() + serverType + "\n";
		title += ChatColor.AQUA + "" + ChatColor.BOLD + TournamentSystem.getInstance().getCachedTournamentLink();

		final String finalTitle = title;
		Bukkit.getServer().getOnlinePlayers().forEach(player -> {
			int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);

			String footer = "";

			if (recentTps.length > 0) {
				double tps = recentTps[0];
				footer += ChatColor.GOLD + "TPS: " + TournamentUtils.formatTps(tps) + (tps < 18 ? " " + ChatColor.RED + TextUtils.ICON_WARNING : "") + " ";
				tps = recentTps[0];
			}

			footer += ChatColor.GOLD + "Ping: " + TournamentUtils.formatPing(ping) + "ms " + (ping > 800 ? ChatColor.YELLOW + TextUtils.ICON_WARNING : "") + "\n\n";
			footer += ChatColor.AQUA + "Tournament developed by NovaUniverse. Join our discord server for weekly tournaments https://discord.gg/4gZSVJ7";

			VersionIndependentUtils.get().sendTabList(player, finalTitle, footer);
		});
	}
}