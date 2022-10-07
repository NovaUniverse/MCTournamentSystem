package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.modules.yborder.YBorder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class YBorderSet extends NovaSubCommand {
	public YBorderSet() {
		super("set");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.yborder");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Set y border limit");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide a number for the new y limit");
			return true;
		}

		int y = 0;
		try {
			y = Integer.parseInt(args[0]);
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + "Please provide a valid number");
			return true;
		}

		YBorder yBorder = (YBorder) ModuleManager.getModule(YBorder.class);

		if (yBorder != null) {
			yBorder.setyLimit(y);
			sender.sendMessage(ChatColor.GREEN + "Y Border limit changed to " + y);
		} else {
			sender.sendMessage(ChatColor.RED + "Y Border not loaded");
		}
		return true;
	}
}