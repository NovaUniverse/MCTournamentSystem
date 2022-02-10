package net.novauniverse.mctournamentsystem.bungeecord.listener;

import de.dombo.bungeemessages.BungeeMessages;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.commons.LCS;
import net.zeeraa.novacore.commons.utils.UUIDUtils;

public class JoinEvents implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(PostLoginEvent e) {
		ProxiedPlayer player = e.getPlayer();

		if(LCS.isDemo()) {
			player.sendMessage(new TextComponent(ChatColor.GREEN + "This is a demo version"));
		}
		
		if (player.hasPermission("tournamentcore.autosocialspy")) {
			if (!BungeeMessages.getPlugin().getManager().isSocialSpy(player)) {
				BungeeMessages.getPlugin().getManager().playerSocialSpy().add(player);
				player.sendMessage(new TextComponent(ChatColor.GREEN + "Social spy enabled since you have moderator permissions"));
			}
		} else if (BungeeMessages.getPlugin().getManager().isSocialSpy(player)) {
			BungeeMessages.getPlugin().getManager().playerSocialSpy().remove(player);
		}

		ProxyServer.getInstance().getPlayers().forEach(p -> {
			if (UUIDUtils.isSame(p.getUniqueId(), e.getPlayer().getUniqueId())) {
				return;
			}

			if (p.hasPermission("tournamentsystem.joinnotifications")) {
				ClickEvent event = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sendhere " + e.getPlayer().getName());

				p.sendMessage(new ComponentBuilder(e.getPlayer().getName() + " connected. Click this message to send them to your server").color(ChatColor.GOLD).event(event).create());
			}
		});
	}
}