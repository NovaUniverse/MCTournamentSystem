package net.novauniverse.mctournamentsystem.spigot.command.chatfilter;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ChatfilterCommand extends NovaCommand {
	public ChatfilterCommand() {
		super("chatfilter", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.chatfilter");
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