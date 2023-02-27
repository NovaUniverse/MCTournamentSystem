package net.novauniverse.mctournamentsystem.lobby.command.missilewars;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class MissileWars extends NovaCommand {
	public MissileWars(Plugin plugin) {
		super("missilewars", plugin);

		this.setFilterAutocomplete(true);
		this.setEmptyTabMode(true);
		this.setAllowedSenders(AllowedSenders.PLAYERS);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(ChatColor.GREEN + "Trying to connect to the missilewars server...");
		BungeecordUtils.sendToServer((Player) sender, "missilewars");
		return true;
	}
}