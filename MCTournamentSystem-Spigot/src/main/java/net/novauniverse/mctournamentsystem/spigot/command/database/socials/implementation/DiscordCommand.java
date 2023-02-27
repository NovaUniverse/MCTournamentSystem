package net.novauniverse.mctournamentsystem.spigot.command.database.socials.implementation;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import net.novauniverse.mctournamentsystem.spigot.command.database.socials.AbstractSocialsCommand;

public class DiscordCommand extends AbstractSocialsCommand {

	public DiscordCommand(Plugin plugin, String url) {
		super("discord", plugin, url);
	}

	@Override
	public String getMessage() {
		return ChatColor.GREEN + "Here is the link to our discord server: " + ChatColor.AQUA + url;
	}
}