package net.novauniverse.mctournamentsystem.spigot.command.halt;

import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.log.LogLevel;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.command.NovaSubCommand;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.PlayerUnloadOption;
import net.zeeraa.novacore.spigot.module.modules.multiverse.WorldUnloadOption;

public class HaltCommand extends NovaCommand {
	public HaltCommand() {
		super("halt", TournamentSystem.getInstance());

		setDescription("Cancel all server activity");
		setAliases(generateAliasList("haltactivity", "emergencystop"));
		addHelpSubCommand();
		setEmptyTabMode(true);

		addSubCommand(new HaltConfirmCommand());
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.DARK_RED + "" + ChatColor.BOLD + TextUtils.ICON_WARNING + " Warning! " + TextUtils.ICON_WARNING + "\nThis command will halt all plugin activity on the server!\nThis should only be done in emergencies or while testing.\nTo confirm halt use " + ChatColor.AQUA + "/halt confirm");
		return false;
	}
}

class HaltConfirmCommand extends NovaSubCommand {
	public HaltConfirmCommand() {
		super("confirm");
		setAllowedSenders(AllowedSenders.ALL);
		setPermissionDefaultValue(PermissionDefault.FALSE);
		setPermission("tournamentsystem.command.halt");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (sender instanceof Player) {
			Log.subscribedPlayers.put(((Player) sender).getUniqueId(), LogLevel.TRACE);
		}

		Log.fatal("Server Halt", "Server halt triggered by " + sender.getName());

		Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "Halting server activity");

		if (MultiverseManager.getInstance().isEnabled()) {
			Log.info("Server Halt", "Preventing multiverse kick");

			MultiverseManager.getInstance().getWorlds().values().forEach(world -> {
				world.setUnloadOption(WorldUnloadOption.KEEP);
				world.setPlayerUnloadOptions(PlayerUnloadOption.DO_NOTHING);
			});
		}

		Log.info("Server Halt", "Killing database");
		try {
			TournamentSystemCommons.getDBConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
			Log.warn("Server Halt", "Exception caught while closing database " + e.getClass().getName() + " " + e.getMessage());
		}

		Log.info("Server Halt", "Ending tasks");
		Bukkit.getScheduler().cancelAllTasks();

		Log.info("Server Halt", "Unregistering handlers");
		HandlerList.unregisterAll();

		Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED + "All plugin activity halted");

		return true;
	}
}