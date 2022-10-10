package net.novauniverse.mctournamentsystem.spigot.command.database.socials;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;

import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public abstract class AbstractSocialsCommand extends NovaCommand {
	protected String url;

	public AbstractSocialsCommand(String name, Plugin plugin, String url) {
		super(name, plugin);
		this.url = url;

		setPermission("tournamentsystem.command.social");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);
		setRequireOp(false);
		setEmptyTabMode(true);
		setFilterAutocomplete(false);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		sender.sendMessage(getMessage());
		return true;
	}

	public abstract String getMessage();
}