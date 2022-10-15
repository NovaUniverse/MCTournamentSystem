package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.behindyourtail;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class ToggleBehindYourTailTracers extends NovaCommand {
	public ToggleBehindYourTailTracers() {
		super("togglebehindyourtailtracers", TournamentSystem.getInstance());

		setAllowedSenders(AllowedSenders.ALL);
		setPermission("tournamentcore.command.togglebehindyourtailtracers");
		setPermissionDefaultValue(PermissionDefault.OP);
		setUsage("/togglebehindyourtailtracers");
		setDescription("Toggle tracers in behind your tail");
		setEmptyTabMode(true);
		setFilterAutocomplete(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		BehindYourTailManager behindYourTailManager = (BehindYourTailManager) ModuleManager.getModule(BehindYourTailManager.class);
		behindYourTailManager.setTracersDisabled(!behindYourTailManager.isTracersDisabled());
		sender.sendMessage(ChatColor.GREEN + "Behind your tail tracers " + (behindYourTailManager.isTracersDisabled() ? "disabled" : "enabled"));
		return true;
	}
}