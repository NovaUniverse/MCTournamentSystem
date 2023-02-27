package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.modules.yborder.YBorder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleEnableFailureReason;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class YBorderEnable extends NovaSubCommand {
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