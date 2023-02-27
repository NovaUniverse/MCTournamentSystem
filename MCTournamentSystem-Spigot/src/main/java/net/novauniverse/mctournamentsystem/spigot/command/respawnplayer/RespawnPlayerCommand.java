package net.novauniverse.mctournamentsystem.spigot.command.respawnplayer;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class RespawnPlayerCommand extends NovaCommand {
	public RespawnPlayerCommand() {
		super("respawnplayer", TournamentSystem.getInstance());
		setDescription("Respawn a player");
		setPermission("tournamentcore.command.respawnplayer");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.PLAYERS);

		addHelpSubCommand();

		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "Please provide a player");
		}

		Player player = Bukkit.getServer().getPlayer(args[0]);

		if (player != null) {
			if (NovaCore.isNovaGameEngineEnabled()) {
				if (GameManager.getInstance().isEnabled()) {
					if (GameManager.getInstance().hasGame()) {
						if (GameManager.getInstance().getActiveGame().hasStarted()) {
							if (!GameManager.getInstance().getActiveGame().hasEnded()) {
								if (!GameManager.getInstance().getActiveGame().getPlayers().contains(player.getUniqueId())) {
									Team team = TeamManager.getTeamManager().getPlayerTeam(player);
									if (team != null) {
										GameManager.getInstance().getActiveGame().addPlayer(player);
										player.teleport(((Player) sender).getLocation());
										player.setGameMode(GameMode.SURVIVAL);
										player.sendMessage(ChatColor.GREEN + "Respawned by a staff member");
										VersionIndependentSound.NOTE_PLING.play(player, player.getLocation());
										VersionIndependentUtils.get().sendTitle(player, ChatColor.GREEN + "Respawned", ChatColor.GREEN + "A staff member respawned you", 10, 60, 10);
										TournamentSystem.getInstance().onRespawnPlayerCommand(player);
										sender.sendMessage(ChatColor.GREEN + "Respawn successful");
									} else {
										sender.sendMessage(ChatColor.RED + "Player not in a team");
									}
								} else {
									sender.sendMessage(ChatColor.RED + "Player already in game");
								}
							} else {
								sender.sendMessage(ChatColor.RED + "Game has ended");
							}
						} else {
							sender.sendMessage(ChatColor.RED + "Game has not started");
						}
					} else {
						sender.sendMessage(ChatColor.RED + "No game loaded");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "GameManager not enabled");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "GameEngine plugin not installed");
			}
		} else {
			sender.sendMessage(ChatColor.RED + "That player is not online");
		}

		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		List<String> result = new ArrayList<>();

		if (NovaCore.isNovaGameEngineEnabled()) {
			if (GameManager.getInstance().isEnabled()) {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						if (!GameManager.getInstance().getActiveGame().hasEnded()) {
							Bukkit.getServer().getOnlinePlayers().forEach(player -> {
								if (!GameManager.getInstance().getActiveGame().getPlayers().contains(player.getUniqueId())) {
									Team team = TeamManager.getTeamManager().getPlayerTeam(player);
									if (team != null) {
										result.add(player.getName());
									}
								}
							});
						}
					}
				}
			}
		}

		return result;
	}
}