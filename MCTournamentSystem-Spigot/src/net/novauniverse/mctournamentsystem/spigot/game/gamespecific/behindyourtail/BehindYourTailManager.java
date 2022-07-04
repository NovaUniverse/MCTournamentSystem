package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.behindyourtail;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.behindyourtail.NovaBehindYourTail;
import net.novauniverse.behindyourtail.game.event.BehindYourTailPlayerDamageFoxEvent;
import net.novauniverse.behindyourtail.game.role.Role;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.CustomDefaultPlayerEliminationMessaageProvider;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class BehindYourTailManager extends NovaModule {
	private static final int PLAYER_ATTACK_FOX_SCORE = 10;

	public BehindYourTailManager() {
		super("TournamentSystem.GameSpecific.BehindYourTailManager");
	}

	@Override
	public void onLoad() {
		ModuleManager.disable(PlayerHeadDrop.class);

		TournamentSystem.getInstance().getDefaultPlayerEliminationMessage().addCustomProvider(PlayerEliminationReason.OTHER, new CustomDefaultPlayerEliminationMessaageProvider() {
			@Override
			public String getEliminationMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
				Role role = NovaBehindYourTail.getInstance().getGame().getPlayerRole(player.getUniqueId());

				ChatColor color = ChatColor.AQUA;
				if (TeamManager.hasTeamManager()) {
					Team team = TeamManager.getTeamManager().getPlayerTeam(player.getUniqueId());
					if (team != null) {
						color = team.getTeamColor();
					}
				}

				if (role == Role.HUNTER) {
					return ChatColor.RED + "" + ChatColor.BOLD + "Player Eliminated> " + color + ChatColor.BOLD + player.getName() + ChatColor.RED + ChatColor.BOLD + " failed to protect their fox";
				}

				return "";
			}
		});
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBehindYourTailPlayerDamageFox(BehindYourTailPlayerDamageFoxEvent e) {
		Player player = e.getAttacker();
		player.sendMessage(ChatColor.GRAY + "Damaged enemy fox. +" + PLAYER_ATTACK_FOX_SCORE + " points");
		ScoreManager.getInstance().addPlayerScore(player, PLAYER_ATTACK_FOX_SCORE, true);
	}
}