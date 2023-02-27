package net.novauniverse.mctournamentsystem.bungeecord.listener.whitelist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class WhitelistListener implements Listener {
	@EventHandler(priority = EventPriority.NORMAL)
	public void onLogin(LoginEvent e) {
		if (TournamentSystem.getInstance().isOpenMode()) {
			return;
		}

		try {
			UUID uuid = e.getConnection().getUniqueId();
			String uuidString = uuid.toString();

			boolean allow = false;

			String sql;
			PreparedStatement ps;
			ResultSet rs;

			sql = "SELECT id FROM whitelist WHERE uuid = ?";

			ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.setString(1, uuidString);

			rs = ps.executeQuery();

			if (rs.next()) {
				allow = true;
			}

			rs.close();
			ps.close();

			if (!allow) {
				sql = "SELECT id FROM players WHERE uuid = ?";

				ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.setString(1, uuidString);

				rs = ps.executeQuery();

				if (rs.next()) {
					allow = true;
				}

				rs.close();
				ps.close();
			}

			if (!allow) {
				sql = "SELECT id FROM staff WHERE uuid = ?";

				ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.setString(1, uuidString);

				rs = ps.executeQuery();

				if (rs.next()) {
					allow = true;
				}

				rs.close();
				ps.close();
			}

			if (!allow) {
				e.setCancelled(true);
				e.setCancelReason(new TextComponent(ChatColor.RED + "You are not in the list of allowed players to join this tournament.\n\nIf you are supposed to play in the tournament please contact a staff member"));
			}
		} catch (Exception ex) {
			e.setCancelled(true);
			e.setCancelReason(new TextComponent(ChatColor.RED + "An internal error occured while verifying your connection\n\n" + ChatColor.DARK_RED + ex.getClass().getName() + " " + ex.getMessage() + "\n\n" + ChatColor.RED + "If you keep seeing this error please contact staff"));
		}
	}
}