package net.novauniverse.mctournamentsystem.spigot.modules.cinvsee;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.novauniverse.mctournamentsystem.spigot.permissions.TournamentPermissions;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class CInvseeManager extends NovaModule implements Listener {
	public CInvseeManager() {
		super("TournamentSystem.CInvseeManager");
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.getInventory().getHolder() != null) {
			if (e.getInventory().getHolder() instanceof Player) {
				if (e.getWhoClicked() != e.getInventory().getHolder()) {
					if (e.getWhoClicked().hasPermission(TournamentPermissions.COMMENTATOR_PERMISSION)) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
}