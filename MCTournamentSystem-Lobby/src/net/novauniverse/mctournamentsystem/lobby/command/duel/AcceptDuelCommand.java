package net.novauniverse.mctournamentsystem.lobby.command.duel;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.lobby.modules.duels.DuelManager;
import net.novauniverse.mctournamentsystem.lobby.modules.duels.InviteResult;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;

public class AcceptDuelCommand extends NovaCommand {
	public AcceptDuelCommand() {
		super("acceptduel", TournamentSystem.getInstance());
		this.setAllowedSenders(AllowedSenders.PLAYERS);
		this.setPermission("tournamentcore.command.acceptduel");
		this.setPermissionDefaultValue(PermissionDefault.TRUE);
		this.setDescription("Accept a duel request");
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length > 0) {
			Player player = (Player) sender;
			String code = args[0];

			if (DuelManager.getInstance().isInDuel(player)) {
				player.sendMessage(ChatColor.RED + "You are already in a duel");
			} else {
				InviteResult result = DuelManager.getInstance().acceptInvite(player, code);

				Log.trace("Duel request result: " + result);

				// Not all results needs a message
				switch (result) {
				case INVALID:
					player.sendMessage(ChatColor.RED + "This invite code has expired");
					break;

				case PLAYER_BUSY:
					player.sendMessage(ChatColor.RED + "That player is already in another duel.\nPlease wait for them to finish before trying again");
					break;

				case START_FAILURE:
					player.sendMessage(ChatColor.DARK_RED + "An error occured");
					break;

				default:
					break;
				}

				return true;
			}
		} else {
			sender.sendMessage(ChatColor.DARK_RED + "ERR:MISSING_REQUEST_ID");
		}
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		return new ArrayList<String>();
	}
}