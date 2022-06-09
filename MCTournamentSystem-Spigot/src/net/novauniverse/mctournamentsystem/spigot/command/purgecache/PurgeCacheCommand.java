package net.novauniverse.mctournamentsystem.spigot.command.purgecache;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerNameCache;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class PurgeCacheCommand extends NovaCommand {
	public PurgeCacheCommand() {
		super("purgecache", TournamentSystem.getInstance());

		setAliases(NovaCommand.generateAliasList("clearcache"));
		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.purgecache");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);
		setUsage("/purgecache");
		setDescription("Purge the cached database content");
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		PlayerKillCache.getInstance().clearCache();
		PlayerNameCache.getInstance().clearCache();

		sender.sendMessage(ChatColor.GREEN + "Cached data cleared");

		return true;
	}
}
