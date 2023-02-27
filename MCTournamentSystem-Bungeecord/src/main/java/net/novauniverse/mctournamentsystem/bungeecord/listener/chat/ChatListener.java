package net.novauniverse.mctournamentsystem.bungeecord.listener.chat;

import java.sql.PreparedStatement;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class ChatListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatFinal(ChatEvent e) {
		if (e.isCancelled()) {
			return;
		}
		if (e.getSender() instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) e.getSender();
			try {
				String sql = "INSERT INTO chat_log (id, session_id, uuid, username, content) VALUES (null, ?, ?, ?, ?)";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.setString(1, TournamentSystemCommons.getSessionId().toString());
				ps.setString(2, player.getUniqueId().toString());
				ps.setString(3, player.getName());
				ps.setString(4, e.getMessage());
				ps.executeUpdate();
			} catch (Exception ex) {
				ex.printStackTrace();
				Log.error("Chatlogger", "Failed to store chat message from " + player.getName() + ". " + ex.getClass().getName() + " " + ex.getMessage());
			}
		}
	}
}