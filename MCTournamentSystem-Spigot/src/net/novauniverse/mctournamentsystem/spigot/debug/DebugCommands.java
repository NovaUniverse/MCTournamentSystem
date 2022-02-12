package net.novauniverse.mctournamentsystem.spigot.debug;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.hastebin.Hastebin;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.debug.DebugCommandRegistrator;
import net.zeeraa.novacore.spigot.debug.DebugTrigger;
import net.zeeraa.novacore.spigot.teams.Team;

public class DebugCommands {
	public DebugCommands() {
		DebugCommandRegistrator.getInstance().addDebugTrigger(new DebugTrigger() {
			@Override
			public void onExecute(CommandSender sender, String commandLabel, String[] args) {
				String data = "----- Minecraft tournament stystem team dump -----\n";

				for (Team team : TournamentSystem.getInstance().getTeamManager().getTeams()) {
					TournamentSystemTeam tsTeam = (TournamentSystemTeam) team;
					data += "Team " + tsTeam.getTeamNumber() + ":\n";
					String members = "";
					for (UUID uuid : tsTeam.getMembers()) {
						members += uuid.toString() + " ";
					}
					data += "Members: " + members + "\n";
					data += "Member string: " + tsTeam.getMemberString() + "\n";
					data += "Score: " + tsTeam.getScore() + "\n";
					data += "Team color: " + tsTeam.getTeamColor().name() + "\n";
					data += "--------------------\n";
				}

				Hastebin hastebin = new Hastebin();

				try {
					String url = hastebin.post(data, true);
					sender.sendMessage(ChatColor.GREEN + "Data dump complete. " + url);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.DARK_RED + "Failed to post data. " + e.getClass().getName() + " " + e.getMessage() + ". Check if " + Hastebin.HASTEBIN_BASE_URL + " is working");
					sender.sendMessage(ChatColor.GREEN + "Data dump sent in the console due to the server not being able to post the data on hastebin");
					Bukkit.getServer().getConsoleSender().sendMessage(data);
					e.printStackTrace();
				}
			}

			@Override
			public PermissionDefault getPermissionDefault() {
				return PermissionDefault.OP;
			}

			@Override
			public String getPermission() {
				return "tournamentsystem.debug.dumpteams";
			}

			@Override
			public String getName() {
				return "dumpteams";
			}

			@Override
			public AllowedSenders getAllowedSenders() {
				return AllowedSenders.ALL;
			}
		});
	}
}