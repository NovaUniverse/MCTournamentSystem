package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.modules.yborder.YBorder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class YBorderPause extends NovaSubCommand {
	public YBorderPause() {
		super("reset");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.yborder");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Reset y border");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (ModuleManager.isEnabled(YBorder.class)) {
			YBorder yBorder = (YBorder) ModuleManager.getModule(YBorder.class);

			if (!yBorder.isPaused()) {
				sender.sendMessage(ChatColor.GREEN + "Y Border paused");
				yBorder.setPaused(true);
			} else {
				sender.sendMessage(ChatColor.RED + "Y Border is already paused");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Y border not enabled");
		}
		return true;
	}
}