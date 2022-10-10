package net.novauniverse.mctournamentsystem.spigot.command.database.socials.implementation;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import net.novauniverse.mctournamentsystem.spigot.command.database.socials.AbstractSocialsCommand;

public class PatreonCommand extends AbstractSocialsCommand {
	public PatreonCommand(Plugin plugin, String url) {
		super("patreon", plugin, url);
	}

	@Override
	public String getMessage() {
		return ChatColor.GREEN + "Here is the link to our patreon: " + ChatColor.AQUA + url;
	}
}