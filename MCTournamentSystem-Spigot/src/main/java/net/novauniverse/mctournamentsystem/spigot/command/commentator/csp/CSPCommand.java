package net.novauniverse.mctournamentsystem.spigot.command.commentator.csp;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class CSPCommand extends NovaCommand {
	public CSPCommand() {
		super("csp", TournamentSystem.getInstance());
		setDescription("Change to spectator mode");
		setPermission("tournamentcore.command.csp");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);

		setEmptyTabMode(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		((Player) sender).setGameMode(GameMode.SPECTATOR);
		return true;
	}
}