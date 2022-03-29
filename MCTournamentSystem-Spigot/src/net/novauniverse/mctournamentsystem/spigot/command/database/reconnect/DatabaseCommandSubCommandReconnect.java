package net.novauniverse.mctournamentsystem.spigot.command.database.reconnect;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class DatabaseCommandSubCommandReconnect extends NovaSubCommand {
	public DatabaseCommandSubCommandReconnect() {
		super("reconnect");

		setDescription("Reconnect the database");
		setUseage("/database reconnect");
		setAllowedSenders(AllowedSenders.ALL);

		setPermission("tournamentcore.command.database.reconnect");
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