package net.novauniverse.mctournamentsystem.spigot.command.fly;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class FlyCommand extends NovaCommand {
	public FlyCommand() {
		super("fly", TournamentSystem.getInstance());
		setDescription("Enable flight for you or another player");
		setPermission("tournamentsystem.command.fly");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.ALL);

		addHelpSubCommand();

		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		Player target = null;

		boolean useTarget = false;

		if (args.length == 0) {
			if (sender instanceof Player) {
				target = (Player) sender;
			} else {
				sender.sendMessage(ChatColor.RED + "Pleace specify a target player");
				return false;
			}
		} else {
			target = Bukkit.getServer().getPlayer(args[0]);
			useTarget = true;

			if (target != null) {
				if (!target.isOnline()) {
					sender.sendMessage(ChatColor.RED + "That player is not online");
					return false;
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find a player with that name");
				return false;
			}
		}

		boolean newState = !target.getAllowFlight();

		target.setAllowFlight(newState);
		target.setFlying(newState);
		target.setAllowFlight(newState);

		sender.sendMessage(ChatColor.GOLD + "Flight " + (newState ? "enabled" : "disabled") + (useTarget ? " for " + target.getName() : ""));

		return true;
	}
}