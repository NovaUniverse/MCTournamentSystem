package net.novauniverse.mctournamentsystem.spigot.command.commentator.cinvsee;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.permissions.TournamentPermissions;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class CInvseeCommand extends NovaCommand {
	public CInvseeCommand() {
		super("cinvsee", TournamentSystem.getInstance());
		setDescription("Open another players inventory");
		setPermission(TournamentPermissions.COMMENTATOR_PERMISSION);
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);

		setFilterAutocomplete(true);

		addHelpSubCommand();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length > 0) {
			Player player = Bukkit.getServer().getPlayer(args[0]);
			if (player != null) {
				if (player.isOnline()) {
					((Player) sender).openInventory(player.getInventory());
				} else {
					sender.sendMessage(ChatColor.RED + "That player is not online");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Could not find player " + args[0]);
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Please provide a player");
		}

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<>();

		Bukkit.getServer().getOnlinePlayers().forEach(player -> result.add(player.getName()));

		return super.tabComplete(sender, alias, args);
	}
}
