package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.yborder.YBorder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleEnableFailureReason;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class YBorderCommand extends NovaCommand {
	public YBorderCommand() {
		super("yborder", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.command.yborder");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Command to manage y border");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addSubCommand(new YBorderEnable());
		addSubCommand(new YBorderDisable());
		addSubCommand(new YBorderPause());
		addSubCommand(new YBorderReset());
		addSubCommand(new YBorderSet());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Bukkit.getServer().dispatchCommand(sender, "yborder help");
		return true;
	}
}

class YBorderDisable extends NovaSubCommand {
	public YBorderDisable() {
		super("disable");

		setAliases(NovaCommand.generateAliasList("stop"));
		
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

class YBorderEnable extends NovaSubCommand {
	public YBorderEnable() {
		super("enable");

		setAliases(NovaCommand.generateAliasList("start"));
		
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
		if (ModuleManager.isEnabled(YBorder.class)) {
			YBorder yBorder = (YBorder) ModuleManager.getModule(YBorder.class);

			if (yBorder.isPaused()) {
				sender.sendMessage(ChatColor.GREEN + "Y Border resumed");
				yBorder.setPaused(false);
			} else {
				sender.sendMessage(ChatColor.RED + "Y Border is already enabled");
			}
		} else {
			if (ModuleManager.enable(YBorder.class)) {
				sender.sendMessage(ChatColor.GREEN + "Y Border enabled");
			} else {
				ModuleEnableFailureReason reason = ModuleManager.getEnableFailureReason(YBorder.class);
				sender.sendMessage(ChatColor.DARK_RED + "Failed to enable module. Check logs for more info. Reason: " + reason.name());
			}
		}
		return true;
	}
}

class YBorderPause extends NovaSubCommand {
	public YBorderPause() {
		super("pause");

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
				sender.sendMessage(ChatColor.RED + "Y Border is already pause. You can resume it with " + ChatColor.AQUA + "/yborder start");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Y border not enabled");
		}
		return true;
	}
}

class YBorderSet extends NovaSubCommand {
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

class YBorderReset extends NovaSubCommand {
	public YBorderReset() {
		super("reset");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.yborder");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Pause y border");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		YBorder yBorder = (YBorder) ModuleManager.getModule(YBorder.class);

		if (yBorder != null) {
			yBorder.reset();
			sender.sendMessage(ChatColor.GREEN + "Y Border reset");
		} else {
			sender.sendMessage(ChatColor.RED + "Y Border not loaded");
		}
		return true;
	}
}