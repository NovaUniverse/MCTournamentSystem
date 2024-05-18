package net.novauniverse.mctournamentsystem.spigot.command.enablefinalgame;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.finalgame.FinalGame;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class EnableFinalGameCommand extends NovaCommand {
	public EnableFinalGameCommand() {
		super("enablefinalgame", TournamentSystem.getInstance());
		setDescription("Enable final game 1v1 mode");
		setPermission("tournamentsystem.command.enablefinalgame");
		setPermissionDefaultValue(PermissionDefault.OP);
		setAllowedSenders(AllowedSenders.ALL);

		addHelpSubCommand();

		this.setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		if (ModuleManager.isEnabled(FinalGame.class)) {
			sender.sendMessage(ChatColor.RED + "Final game is already enabled");
		} else {
			if (ModuleManager.enable(FinalGame.class)) {
				sender.sendMessage(ChatColor.GREEN + "Final game enabled");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Failed to enable final game. " + ModuleManager.getEnableFailureReason(FinalGame.class));
			}
		}

		return true;
	}
}