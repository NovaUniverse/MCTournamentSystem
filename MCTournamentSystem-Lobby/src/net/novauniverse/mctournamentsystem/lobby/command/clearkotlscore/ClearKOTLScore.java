package net.novauniverse.mctournamentsystem.lobby.command.clearkotlscore;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.lobby.modules.lobby.Lobby;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class ClearKOTLScore extends NovaCommand {
	public ClearKOTLScore() {
		super("clearkotlscore", TournamentSystem.getInstance());

		setDescription("Clears the king of the ladder score");
		setPermission("tournamentcore.command.clearkotlscore");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.ALL);

		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Lobby.getInstance().clearKOTLScore();
		sender.sendMessage(ChatColor.GREEN + "Ok");
		return true;
	}
}