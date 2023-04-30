package net.novauniverse.mctournamentsystem.spigot.command.commentator.ctp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;

public class CTPCommand extends NovaCommand {
	public CTPCommand() {
		super("ctp", TournamentSystem.getInstance());
		setDescription("Teleport to a players thats in game");
		setPermission("tournamentsystem.command.ctp");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);

		setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (GameManager.getInstance().hasGame()) {
			if (args.length > 0) {
				Player player = Bukkit.getServer().getPlayer(args[0]);
				if (player != null) {
					if (player.isOnline()) {
						((Player) sender).teleport(player, TeleportCause.PLUGIN);
						player.sendMessage(ChatColor.GREEN + "Teleported to " + player.getName());
					} else {
						sender.sendMessage(ChatColor.RED + "That player is not online");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "Cant find player " + args[0]);
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Please provide a player. Use tab to auto complete players that are still alive");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "It seems like there is no game running on this server");
		}

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> players = new ArrayList<>();

		if (NovaCore.isNovaGameEngineEnabled()) {
			Bukkit.getServer().getOnlinePlayers().forEach(player -> {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().getPlayers().contains(player.getUniqueId())) {
						players.add(player.getName());
					}
				}
			});
		}

		return players;
	}
}