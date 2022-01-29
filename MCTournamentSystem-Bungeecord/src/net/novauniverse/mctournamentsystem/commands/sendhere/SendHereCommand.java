package net.novauniverse.mctournamentsystem.commands.sendhere;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

public class SendHereCommand extends Command {
	public SendHereCommand() {
		super("sendhere");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			if (sender.hasPermission("tournamentsystem.command.sendhere")) {
				if (args.length == 0) {
					sender.sendMessage(new ComponentBuilder("Please provide a player").color(ChatColor.RED).create());
				} else {
					ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(args[0]);
					if (pp == null) {
						sender.sendMessage(new ComponentBuilder("Cant find player " + args[0]).color(ChatColor.RED).create());
					} else {
						Server server = ((ProxiedPlayer) sender).getServer();
						pp.connect(server.getInfo());
						sender.sendMessage(new ComponentBuilder("Sending " + pp.getName() + " to your server").color(ChatColor.GREEN).create());
					}
				}
			} else {
				sender.sendMessage(new ComponentBuilder("You are not allowed to use this command").color(ChatColor.RED).create());
			}
		} else {
			sender.sendMessage(new ComponentBuilder("Only players can use this command").color(ChatColor.RED).create());
		}
	}
}