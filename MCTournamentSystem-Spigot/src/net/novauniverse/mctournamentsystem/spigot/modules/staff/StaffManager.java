package net.novauniverse.mctournamentsystem.spigot.modules.staff;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class StaffManager extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "ts.staffmanager";
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		String role = null;
		try {
			String sql = "SELECT role FROM staff WHERE uuid = ?";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.setString(1, e.getPlayer().getUniqueId().toString());
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				role = rs.getString("role");
			}

			rs.close();
			ps.close();
		} catch (Exception ex) {
			kickDelayed(e.getPlayer(), ChatColor.RED + "Failed to fetch user role\n\n" + ex.getClass().getName() + " " + ex.getMessage());
			return;
		}

		User user = LuckPermsProvider.get().getUserManager().getUser(e.getPlayer().getUniqueId());

		List<Node> toRemove = new ArrayList<>();

		user.getDistinctNodes().forEach(node -> {
			if (node.getKey().startsWith("group.")) {
				toRemove.add(node);
			}
		});

		toRemove.forEach(node -> user.data().remove(node));

		if (role != null) {
			if (!TournamentSystem.getInstance().getStaffGroups().containsKey(role)) {
				kickDelayed(e.getPlayer(), ChatColor.RED + "You have an invalid role. Please contact staff about this.\n\nTo resolve this issue please remove the player from the staff team and add them again");
				Log.error(getName(), e.getPlayer().getName() + " has an invalid role. Please contact staff about this. To resolve this issue please remove the player from the staff team and add them again");
				return;
			}

			Group group = TournamentSystem.getInstance().getStaffGroups().get(role);

			DataMutateResult result = user.data().add(Node.builder("group." + group.getName()).build());

			if (result == DataMutateResult.FAIL || result == DataMutateResult.FAIL_LACKS || result == DataMutateResult.GENERIC_FAILURE) {
				kickDelayed(e.getPlayer(), ChatColor.RED + "Failed to apply permission group. Please contact staff about this\n\n" + "ERR::" + result.name());
				Log.error(getName(), "Failed to apply " + group.getName() + " permission group to " + e.getPlayer().getName() + " ERR::" + result.name());
			} else {
				Log.trace(getName(), "Apply staff role result for " + e.getPlayer().getName() + " was " + result.name());
			}
		} else {
			DataMutateResult result = user.data().add(Node.builder("group." + TournamentSystem.getInstance().getDefaultGroup().getName()).build());

			if (result == DataMutateResult.FAIL || result == DataMutateResult.FAIL_LACKS || result == DataMutateResult.GENERIC_FAILURE) {
				kickDelayed(e.getPlayer(), ChatColor.RED + "Failed to apply permission group. Please contact staff about this\n\n" + "ERR::" + result.name());
				Log.error(getName(), "Failed to apply default permission group to " + e.getPlayer().getName() + " ERR::" + result.name());
			}
		}

		LuckPermsProvider.get().getUserManager().saveUser(user);
	}

	private void kickDelayed(Player player, String message) {
		new BukkitRunnable() {
			@Override
			public void run() {
				player.kickPlayer(message);
			}
		}.runTaskLater(TournamentSystem.getInstance(), 5L);
	}
}