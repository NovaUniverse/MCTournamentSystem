package net.novauniverse.mctournamentsystem.bungeecord.commands.timeout;

import java.sql.PreparedStatement;
import java.time.Instant;

import me.leoko.advancedban.manager.PunishmentManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.TextComponentBuilder;

public class TimeoutCommand extends Command {
	public static final int TIMEOUT_MINUTES = 10;
	public static final String REASON = "You received a chat timeout";
	public static final String TYPE = "TEMP_MUTE";

	public TimeoutCommand() {
		super("timeout");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender.hasPermission("tournamentsystem.command.timeout")) {
			if (args.length == 0) {
				sender.sendMessage(new ComponentBuilder("Please provide a player").color(ChatColor.RED).create());
			} else {
				ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
				if (target == null) {
					sender.sendMessage(new ComponentBuilder("Cant find player " + args[0]).color(ChatColor.RED).create());
				} else {
					String operator = "CONSOLE";
					if (sender instanceof ProxiedPlayer) {
						operator = ((ProxiedPlayer) sender).getName();
					}

					long start = Instant.now().getEpochSecond() * 1000;
					long end = (Instant.now().getEpochSecond() + (60 * TIMEOUT_MINUTES)) * 1000;

					// Punishment.create(target.getName(), target.getUniqueId().toString(), "Chat
					// timeout", operator, PunishmentType.TEMP_MUTE, end, "10m", true);

					// Using APIs is cringe. Lets interact directly with the database instead

					String shortUUID = target.getUniqueId().toString().replaceAll("-", "");

					try {
						PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement("INSERT INTO Punishments (name, uuid, reason, operator, punishmentType, start, end, calculation) VALUES (?, ?, ?, ?, ?, ?, ?, \"\")");

						ps.setString(1, target.getName());
						ps.setString(2, shortUUID);
						ps.setString(3, REASON);
						ps.setString(4, operator);
						ps.setString(5, TYPE);
						ps.setLong(6, start);
						ps.setLong(7, end);

						ps.executeUpdate();
						ps.close();

						PunishmentManager.get().discard(target.getName());

						target.sendMessage(new TextComponentBuilder("You received a temporary chat timeout").setColor(ChatColor.RED).build());
						sender.sendMessage(new TextComponentBuilder("Punishment applied").setColor(ChatColor.GREEN).build());
					} catch (Exception e) {
						Log.error("Failed to insert punishment into database. " + e.getClass().getName() + " " + e.getMessage());
						sender.sendMessage(new TextComponentBuilder("DB Communication failure. " + e.getClass().getName() + " " + e.getMessage()).setColor(ChatColor.DARK_RED).build());
						e.printStackTrace();
					}
				}
			}
		} else {
			sender.sendMessage(new ComponentBuilder("You are not allowed to use this command").color(ChatColor.RED).create());
		}
	}
}