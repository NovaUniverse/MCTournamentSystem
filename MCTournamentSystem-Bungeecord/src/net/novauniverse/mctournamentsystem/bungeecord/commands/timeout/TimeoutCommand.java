package net.novauniverse.mctournamentsystem.bungeecord.commands.timeout;

import java.time.Instant;

import me.leoko.advancedban.utils.Punishment;
import me.leoko.advancedban.utils.PunishmentType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TimeoutCommand extends Command {
	public static final int TIMEOUT_MINUTES = 10;
	
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
					if(sender instanceof ProxiedPlayer) {
						operator = ((ProxiedPlayer) sender).getName();
					}
					
					long end = (Instant.now().getEpochSecond() + (60 * TIMEOUT_MINUTES)) * 1000;
					
					Punishment.create(target.getName(), target.getUniqueId().toString(), "Chat timeout", operator, PunishmentType.TEMP_MUTE, end, "", true);
				}
			}
		} else {
			sender.sendMessage(new ComponentBuilder("You are not allowed to use this command").color(ChatColor.RED).create());
		}
	}
}