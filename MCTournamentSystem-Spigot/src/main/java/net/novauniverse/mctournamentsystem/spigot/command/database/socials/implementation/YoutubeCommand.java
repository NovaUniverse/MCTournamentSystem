package net.novauniverse.mctournamentsystem.spigot.command.database.socials.implementation;

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import net.novauniverse.mctournamentsystem.spigot.command.database.socials.AbstractSocialsCommand;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class YoutubeCommand extends AbstractSocialsCommand {

	public YoutubeCommand(Plugin plugin, String url) {
		super("youtube", plugin, url);

		setAliases(NovaCommand.generateAliasList("yt"));
	}

	@Override
	public String getMessage() {
		return ChatColor.GREEN + "Here is the link to our youtube channel: " + ChatColor.AQUA + url;
	}
}