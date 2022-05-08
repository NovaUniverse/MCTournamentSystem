package net.novauniverse.mctournamentsystem.lobby.modules.security;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependantUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.NovaCoreGameVersion;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class LobbyCrashPrevention extends NovaModule implements Listener {
	public LobbyCrashPrevention() {
		super("TournamentSystem.Lobby.CrashPrevention");
	}

	// Prevent crash exploit with using too many tnt minecarts
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onEntitySpawn(PlayerInteractEvent e) {
		Material material = VersionIndependantUtils.get().getItemInMainHand(e.getPlayer()).getType();
		if (VersionIndependantUtils.get().getNovaCoreGameVersion() == NovaCoreGameVersion.V_1_8) {
			if (material == Material.EXPLOSIVE_MINECART) {
				e.setCancelled(true);
			}
		}
	}
}