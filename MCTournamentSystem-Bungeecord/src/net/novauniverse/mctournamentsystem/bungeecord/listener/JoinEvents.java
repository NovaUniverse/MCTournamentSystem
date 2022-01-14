package net.novauniverse.mctournamentsystem.bungeecord.listener;

import de.dombo.bungeemessages.BungeeMessages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class JoinEvents implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer player = e.getPlayer();

		if (player.hasPermission("tournamentcore.autosocialspy")) {
			if (!BungeeMessages.getPlugin().getManager().isSocialSpy(player)) {
				BungeeMessages.getPlugin().getManager().playerSocialSpy().add(player);
				player.sendMessage(new TextComponent(ChatColor.GREEN + "Social spy enabled since you have moderator permissions"));
			}
		} else if (BungeeMessages.getPlugin().getManager().isSocialSpy(player)) {
			BungeeMessages.getPlugin().getManager().playerSocialSpy().remove(player);
		}
	}
}