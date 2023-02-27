package net.novauniverse.mctournamentsystem.spigot.modules.tablistmessage;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
public class TabListMessage extends NovaModule implements TabHeaderProvider, TabFooterProvider {
	private Task task;
	private String serverType;
	private RGBColorAnimation rgbColorAnimation;

	private TabHeaderProvider headerProvider;
	private TabFooterProvider footerProvider;

	public TabListMessage() {
		super("TournamentSystem.TabListMessage");

		this.headerProvider = this;
		this.footerProvider = this;
	}

	public void setHeaderProvider(TabHeaderProvider headerProvider) {
		this.headerProvider = headerProvider;
	}

	public void setFooterProvider(TabFooterProvider footerProvider) {
		this.footerProvider = footerProvider;
	}

	public static void setServerType(String serverType) {
		TabListMessage tlm = (TabListMessage) ModuleManager.getModule(TabListMessage.class);
		if (tlm == null) {
			Log.error("TabListMessage", "TabListMessage#setServerType(serverType) called before the module was loaded");
		} else {
			tlm.serverType = serverType;
		}
	}

	public RGBColorAnimation getRgbColorAnimation() {
		return rgbColorAnimation;
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
		Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentUtils.get().sendTabList(player, headerProvider.getTabHeader(player), footerProvider.getTabFooter(player)));
	}

	@Override
	public String getTabFooter(Player player) {
		double[] recentTps = NovaCore.getInstance().getVersionIndependentUtils().getRecentTps();
		int ping = NovaCore.getInstance().getVersionIndependentUtils().getPlayerPing(player);

		String footer = "";

		if (recentTps.length > 0) {
			double tps = recentTps[0];
			footer += formatTPS(tps) + " ";
			tps = recentTps[0];
		}

		footer += formatPing(ping) + "\n\n";
		footer += ChatColor.AQUA + "Tournament developed by NovaUniverse. Join our discord server for weekly tournaments https://discord.gg/4gZSVJ7";
		return footer;
	}

	@Override
	public String getTabHeader(Player player) {
		String title = rgbColorAnimation.getChatColor() + "" + ChatColor.BOLD + ChatColor.stripColor(TournamentSystem.getInstance().getCachedTournamentName()) + ChatColor.WHITE + " - " + rgbColorAnimation.getChatColor() + serverType + "\n";
		title += ChatColor.AQUA + "" + ChatColor.BOLD + TournamentSystem.getInstance().getCachedTournamentLink();
		return title;
	}

	public static final String formatTPS(double tps) {
		return ChatColor.GOLD + "TPS: " + TournamentUtils.formatTps(tps) + (tps < 18 ? " " + ChatColor.RED + TextUtils.ICON_WARNING : "");
	}

	public static final String formatPing(int ping) {
		return ChatColor.GOLD + "Ping: " + TournamentUtils.formatPing(ping) + "ms " + (ping > 800 ? ChatColor.YELLOW + TextUtils.ICON_WARNING : "");
	}
}