package net.novauniverse.mctournamentsystem.bungeecord.listener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class OpenModeListeners implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPostLogin(PostLoginEvent e) {
		try {
			UUID uuid = e.getPlayer().getUniqueId();
			String uuidString = uuid.toString();

			boolean shouldAdd = true;

			String sql;
			PreparedStatement ps;
			ResultSet rs;

			sql = "SELECT id FROM players WHERE uuid = ?";

			ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.setString(1, uuidString);

			rs = ps.executeQuery();

			if (rs.next()) {
				shouldAdd = false;
			}

			rs.close();
			ps.close();

			if (shouldAdd) {
				Log.info("OpenModeListeners", "Adding player " + e.getPlayer().getName() + " to the player list");
				sql = "INSERT INTO players (uuid, username, team_number) VALUES (?, ?, ?)";
				ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, uuidString);
				ps.setString(2, e.getPlayer().getName());
				ps.setInt(3, 1);

				ps.executeUpdate();

				ps.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			e.getPlayer().disconnect(new TextComponent(ChatColor.RED + "An internal error occured while joining the server\n\n" + ChatColor.DARK_RED + ex.getClass().getName() + " " + ex.getMessage() + "\n\n" + ChatColor.RED + "If you keep seeing this error please contact staff"));
		}
	}
}