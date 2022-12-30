package net.novauniverse.mctournamentsystem.spigot.command.reloaddynamicconfig;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ReloadDynamicConfigCommand extends NovaCommand {
	public ReloadDynamicConfigCommand() {
		super("reloaddynamicconfig", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.reloaddynamicconfig");
		setPermissionDefaultValue(PermissionDefault.OP);
		setEmptyTabMode(true);
		setUsage("/reloaddynamicconfig");
		setDescription("Reload the server dynamic config");
		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (TournamentSystem.getInstance().getDynamicConfigURL() == null) {
			sender.sendMessage(ChatColor.RED + "This server is not set up to use dynamic config");
		} else {
			sender.sendMessage(ChatColor.AQUA + "Attempting to load dynamic config from " + TournamentSystem.getInstance().getDynamicConfigURL());
			try {
				TournamentSystem.getInstance().reloadDynamicConfig();
				sender.sendMessage(ChatColor.GREEN + "Dynamic config reloaded");
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Failed to update dynamic config. " + e.getClass().getName() + " " + e.getMessage());
			}
		}

		return true;
	}
}