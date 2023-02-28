package net.novauniverse.mctournamentsystem.spigot.command.killstatusreporting;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class KillStatusReportingCommand extends NovaCommand {
	public KillStatusReportingCommand() {
		super("killstatusreporting", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.killstatusreporting");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);
		setUsage("/killstatusreporting");
		setDescription("Disable status reporting to the proxy server");
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		TournamentSystem.getInstance().killStatusReporting();
		sender.sendMessage(ChatColor.GREEN + "Status reporting task ended");
		return true;
	}
}
