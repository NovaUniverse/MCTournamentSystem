package net.novauniverse.mctournamentsystem.spigot.cosmetics;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.costmeticsystem.cosmeticmanager.event.PrefixEquipEvent;
import net.novauniverse.costmeticsystem.cosmeticmanager.event.PrefixUnequipEvent;
import net.novauniverse.mctournamentsystem.spigot.modules.playerprefix.PlayerPrefixManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class CosmeticsIntegrations extends NovaModule implements Listener {
	public CosmeticsIntegrations() {
		super("TournamentSystem.CosmeticIntegration");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPrefixEquip(PrefixEquipEvent e) {
		ModuleManager.getModule(PlayerPrefixManager.class).setPrefix(e.getPlayer(), e.getPrefix().getPrefix());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPrefixUnequip(PrefixUnequipEvent e) {
		ModuleManager.getModule(PlayerPrefixManager.class).removePrefix(e.getPlayer());
	}
}