package net.novauniverse.mctournamentsystem.spigot.command.chatfilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.chatfilter.ChatFilter;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleEnableFailureReason;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class ChatfilterCommand extends NovaCommand {
	public ChatfilterCommand() {
		super("chatfilter", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.command.chatfilter");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Command to manage chat filter");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addSubCommand(new Enable());
		addSubCommand(new Disable());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Bukkit.getServer().dispatchCommand(sender, "chatfilter help");
		return true;
	}
}

class Disable extends NovaSubCommand {
	public Disable() {
		super("disable");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.command.chatfilter");
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

class Enable extends NovaSubCommand {
	public Enable() {
		super("enable");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.command.chatfilter");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Enable chat filter");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (ModuleManager.isEnabled(ChatFilter.class)) {
			sender.sendMessage(ChatColor.RED + "Chat filter is already enabled");
		} else {
			if (ModuleManager.enable(ChatFilter.class)) {
				sender.sendMessage(ChatColor.GREEN + "Chat filter enabled");
			} else {
				ModuleEnableFailureReason reason = ModuleManager.getEnableFailureReason(ChatFilter.class);
				sender.sendMessage(ChatColor.DARK_RED + "Failed to enable module. Check logs for more info. Reason: " + reason.name());
			}
		}
		return true;
	}
}