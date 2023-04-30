package net.novauniverse.mctournamentsystem.spigot.command.database;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class DatabaseCommand extends NovaCommand {
	public DatabaseCommand() {
		super("database", TournamentSystem.getInstance());

		setAliases(generateAliasList("db", "dbc", "mysql"));

		setPermission("tournamentsystem.command.database");
		setPermissionDefaultValue(PermissionDefault.OP);

		setDescription("Database command");

		setAllowedSenders(AllowedSenders.ALL);

		addSubCommand(new DatabaseCommandSubCommandStatus());
		addSubCommand(new DatabaseCommandSubCommandReconnect());

		addHelpSubCommand();

		setFilterAutocomplete(true);
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use: " + ChatColor.AQUA + "/database help" + ChatColor.GOLD + " for more commands");
		return true;
	}
}

class DatabaseCommandSubCommandReconnect extends NovaSubCommand {
	public DatabaseCommandSubCommandReconnect() {
		super("reconnect");

		setDescription("Reconnect the database");
		setUsage("/database reconnect");
		setAllowedSenders(AllowedSenders.ALL);

		setPermission("tournamentsystem.command.database.reconnect");
		setPermissionDefaultValue(PermissionDefault.OP);
		setDescription("Access to the databse reconnect command");

		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		try {
			if (TournamentSystemCommons.getDBConnection().reconnect()) {
				sender.sendMessage(ChatColor.GREEN + "Success");
				return true;
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Failed");
			}
		} catch (Exception e) {
			sender.sendMessage(ChatColor.DARK_RED + "Reconnect failed. Cause: " + e.getClass().getName() + " : " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}
}

class DatabaseCommandSubCommandStatus extends NovaSubCommand {
	public DatabaseCommandSubCommandStatus() {
		super("status");
		setDescription("Show database status");
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.command.database.status");
		setPermissionDefaultValue(PermissionDefault.OP);

		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		try {
			boolean connected = TournamentSystemCommons.getDBConnection().isConnected();
			boolean working = TournamentSystemCommons.getDBConnection().testQuery();
			sender.sendMessage(ChatColor.GOLD + "===== Database status =====");
			sender.sendMessage(ChatColor.GOLD + "Connected: " + (connected ? ChatColor.GREEN + "Yes" : ChatColor.DARK_RED + "No"));
			sender.sendMessage(ChatColor.GOLD + "Test query: " + (working ? ChatColor.GREEN + "Ok" : ChatColor.DARK_RED + "Failure"));
			sender.sendMessage(ChatColor.GOLD + "===========================");
			return true;
		} catch (Exception e) {
			sender.sendMessage(ChatColor.RED + e.getClass().getName() + " " + e.getMessage() + "\n" + e.getStackTrace());
		}
		return false;
	}
}