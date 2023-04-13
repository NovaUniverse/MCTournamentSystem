package net.novauniverse.mctournamentsystem.spigot.command.bc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class BCCommand extends NovaCommand {
	public BCCommand() {
		super("bc", TournamentSystem.getInstance());
		setDescription("Send a message in the chat");
		setPermission("tournamentcore.command.bc");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.ALL);
		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		String message = String.join(" ", args);

		message = ChatColor.translateAlternateColorCodes('&', message);

		Bukkit.getServer().broadcastMessage(message);
		Bukkit.getServer().getOnlinePlayers().forEach(player -> VersionIndependentSound.NOTE_PLING.play(player, player.getLocation()));

		return true;
	}
}