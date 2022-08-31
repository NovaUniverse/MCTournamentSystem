package net.novauniverse.mctournamentsystem.spigot.command.chatfilter;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.modules.chatfilter.ChatFilter;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class Disable extends NovaSubCommand {
	public Disable() {
		super("disable");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.chatfilter");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Enable the chat filter");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!ModuleManager.isEnabled(ChatFilter.class)) {
			sender.sendMessage(ChatColor.RED + "Chat filter is already disabled");
		} else {
			if (ModuleManager.disable(ChatFilter.class)) {
				sender.sendMessage(ChatColor.GREEN + "Chat filter disabled");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Failed to disable module");
			}
		}
		return true;
	}
}