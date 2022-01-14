package net.novauniverse.mctournamentsystem.spigot.command.yborder;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class YBorderCommand extends NovaCommand {
	public YBorderCommand() {
		super("yborder", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.yborder");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Command to manage y border");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);
		
		addSubCommand(new YBorderEnable());
		addSubCommand(new YBorderDisable());
		
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Bukkit.getServer().dispatchCommand(sender, "yborder help");
		return true;
	}
}