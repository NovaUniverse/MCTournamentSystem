package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.modules.yborder.YBorder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class Disable extends NovaSubCommand {
	public Disable() {
		super("disable");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.yborder");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Enable y border");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!ModuleManager.isEnabled(YBorder.class)) {
			sender.sendMessage(ChatColor.RED + "Y Border is already disabled");
		} else {
			if (ModuleManager.disable(YBorder.class)) {
				sender.sendMessage(ChatColor.GREEN + "Y Border disabled");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Failed to disable module");
			}
		}
		return true;
	}
}