package net.novauniverse.mctournamentsystem.missilewars.lobby.command.hub;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class HubCommand extends NovaCommand {

	public HubCommand(Plugin plugin) {
		super("hub", plugin);

		this.setAliases(NovaCommand.generateAliasList("lobby", "leave"));
		this.setFilterAutocomplete(true);
		this.setEmptyTabMode(true);
		this.setAllowedSenders(AllowedSenders.PLAYERS);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GREEN + "Trying to connect to the lobby...");
		BungeecordUtils.sendToServer((Player) sender, "lobby");
		return true;
	}
}