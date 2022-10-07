package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.modules.yborder.YBorder;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class YBorderReset extends NovaSubCommand {
	public YBorderReset() {
		super("pause");

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