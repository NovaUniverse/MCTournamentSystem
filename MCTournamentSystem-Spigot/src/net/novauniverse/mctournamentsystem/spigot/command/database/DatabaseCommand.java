package net.novauniverse.mctournamentsystem.spigot.command.database;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.command.database.reconnect.DatabaseCommandSubCommandReconnect;
import net.novauniverse.mctournamentsystem.spigot.command.database.status.DatabaseCommandSubCommandStatus;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class DatabaseCommand extends NovaCommand {
	public DatabaseCommand() {
		super("database", TournamentSystem.getInstance());

		setAliases(generateAliasList("db", "dbc", "mysql"));

		setPermission("tournamentcore.command.database");
		setPermissionDefaultValue(PermissionDefault.OP);

		setDescription("Database command");

		setAllowedSenders(AllowedSenders.ALL);

		addSubCommand(new DatabaseCommandSubCommandStatus());
		addSubCommand(new DatabaseCommandSubCommandReconnect());

		addHelpSubCommand();

		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use: " + ChatColor.AQUA + "/database help" + ChatColor.GOLD + " for more commands");
		return true;
	}
}