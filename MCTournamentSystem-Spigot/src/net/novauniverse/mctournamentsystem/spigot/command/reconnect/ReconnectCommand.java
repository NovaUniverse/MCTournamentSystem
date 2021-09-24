package net.novauniverse.mctournamentsystem.spigot.command.reconnect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class ReconnectCommand extends NovaCommand {
	public ReconnectCommand() {
		super("reconnect", TournamentSystem.getInstance());

		this.setAllowedSenders(AllowedSenders.PLAYERS);
		this.setPermission("tournamentcore.command.reconnect");
		this.setPermissionDefaultValue(PermissionDefault.TRUE);
		this.setDescription("Reconnect to a game");

		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		String activeServer = TournamentSystemCommons.getActiveServer();
		if (activeServer != null) {
			sender.sendMessage(LanguageManager.getString(sender, "tournamentcore.command.reconnect.connecting"));
			BungeecordUtils.sendToServer((Player) sender, activeServer);
			return true;
		} else {
			sender.sendMessage(LanguageManager.getString(sender, "tournamentcore.command.reconnect.no_game"));
		}
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		return new ArrayList<String>();
	}
}