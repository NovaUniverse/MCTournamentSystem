package net.novauniverse.mctournamentsystem.spigot.command.database.status;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class DatabaseCommandSubCommandStatus extends NovaSubCommand {

	public DatabaseCommandSubCommandStatus() {
		super("status");
		setDescription("Show database status");
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.database.status");
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