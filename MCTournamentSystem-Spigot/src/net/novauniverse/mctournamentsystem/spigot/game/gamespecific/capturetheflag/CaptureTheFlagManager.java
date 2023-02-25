package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.capturetheflag;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.capturetheflag.game.event.FlagCapturedEvent;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class CaptureTheFlagManager extends NovaModule implements Listener {
	public static int FLAG_CAPTURE_SCORE = 20;
	
	public CaptureTheFlagManager() {
		super("TournamentSystem.GameSpecific.CaptureTheFlagManager");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFlagCaptured(FlagCapturedEvent e) {
		Bukkit.getServer().broadcastMessage(ChatColor.GOLD + ChatColor.BOLD.toString() + "Flag Captured> " + e.getCarrierTeam().getTeam().getTeamColor() + ChatColor.BOLD + e.getCarrier().getName() + ChatColor.GOLD + ChatColor.BOLD + " captured the flag of team " + e.getFlag().getTeam().getTeam().getTeamColor() + ChatColor.BOLD + e.getFlag().getTeam().getTeam().getDisplayName());
		e.getCarrier().sendMessage(ChatColor.GRAY + "Flag captured. +" + FLAG_CAPTURE_SCORE + " points");
		ScoreManager.getInstance().addPlayerScore(e.getCarrier(), FLAG_CAPTURE_SCORE, true);
	}
}