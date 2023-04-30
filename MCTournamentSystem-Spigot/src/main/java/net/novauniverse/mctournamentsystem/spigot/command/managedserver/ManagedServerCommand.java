package net.novauniverse.mctournamentsystem.spigot.command.managedserver;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;

public class ManagedServerCommand extends NovaCommand {
	public ManagedServerCommand() {
		super("managedserver", TournamentSystem.getInstance());

		setDescription("This command is used to start and stop servers related to the tournament system");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.command.managedserver");
		setPermissionDefaultValue(PermissionDefault.OP);
		setPermissionDescription("Allow the user to use the managedserver command");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		setUsage("/managedserver");

		addSubCommand(new ManagedServerKill());
		addSubCommand(new ManagedServerStart());

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GOLD + "Use: " + ChatColor.AQUA + "/managedserver help" + ChatColor.GOLD + " for info on how to use this command");
		return true;
	}
}

class ManagedServerStart extends NovaSubCommand {
	public ManagedServerStart() {
		super("start");

		setDescription("Starts a server");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.commands.managedserver.start");
		setPermissionDefaultValue(PermissionDefault.OP);
		setPermissionDescription("Allow the user to use the managedserver start command");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		setUsage("/managedserver start <Server Name>");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!TournamentSystem.getInstance().hasApi()) {
			sender.sendMessage(ChatColor.RED + "Internal API has not been set up");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You need to provide the name of the server to start");
			return true;
		}

		AsyncManager.runAsync(() -> {
			try {
				TournamentSystem.getInstance().getApi().startServer(args[0]);
				sender.sendMessage(ChatColor.GREEN + "OK");
			} catch (Exception e) {
				Log.error("API", "Failed to start server " + args[0] + ". " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Failed to start server " + args[0] + ". " + e.getMessage());
			}
		});
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> names = new ArrayList<>();
		TournamentSystem.getInstance().getConfiguredManagedServers().forEach(names::add);
		return names;
	}
}

class ManagedServerKill extends NovaSubCommand {
	public ManagedServerKill() {
		super("kill");

		setDescription("Kills a server");

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentsystem.commands.managedserver.kill");
		setPermissionDefaultValue(PermissionDefault.OP);
		setPermissionDescription("Allow the user to use the managedserver kill command");

		setEmptyTabMode(true);
		setFilterAutocomplete(true);

		setUsage("/managedserver kill <Server Name>");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (!TournamentSystem.getInstance().hasApi()) {
			sender.sendMessage(ChatColor.RED + "Internal API has not been set up");
			return true;
		}

		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "You need to provide the name of the server to kill");
			return true;
		}

		AsyncManager.runAsync(() -> {
			try {
				TournamentSystem.getInstance().getApi().killServer(args[0]);
				sender.sendMessage(ChatColor.GREEN + "OK");
			} catch (Exception e) {
				Log.error("API", "Failed to start kill " + args[0] + ". " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Failed to start kill " + args[0] + ". " + e.getMessage());
			}
		});
		return true;
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> names = new ArrayList<>();
		TournamentSystem.getInstance().getConfiguredManagedServers().forEach(names::add);
		return names;
	}
}