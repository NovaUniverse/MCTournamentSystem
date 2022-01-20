package net.novauniverse.mctournamentsystem.spigot.modules.head;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

@NovaAutoLoad(shouldEnable = false)
public class PlayerHeadDrop extends NovaModule implements Listener {
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		Player p = e.getEntity();
		ItemStack playerHead = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

		meta.setOwner(p.getName());

		// Some of the OG players heads have custom text
		if (p.getUniqueId().toString().equalsIgnoreCase("3442be05-4211-4a15-a10c-4bdb2b6060fa")) {
			// Special head for THEGOLDENPRO
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Not to be confused", ChatColor.WHITE + "with " + ChatColor.GOLD + ChatColor.BOLD + "Golden Head"));
		}
		
		if (p.getUniqueId().toString().equalsIgnoreCase("980dbf7d-0904-426f-9c02-d9af3c099fb2")) {
			// Special head for Istromus
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Visual glitch, Istromus never dies"));
		}
		
		if (p.getUniqueId().toString().equalsIgnoreCase("5203face-89ca-49b7-a5a0-f2cf0fe230e7")) {
			// Special head for Woltry
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Roses are red, Violets are blue", ChatColor.WHITE + "allschrimjägergewehr42"));
		}
		
		if (p.getUniqueId().toString().equalsIgnoreCase("ca2e347b-025a-4e7b-8019-752b83661f7f")) {
			// Special head for Cirbyz
			meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "Might be toxic", ChatColor.WHITE + "eat at your own risk"));
		}

		playerHead.setItemMeta(meta);

		p.getWorld().dropItem(p.getLocation(), playerHead);
	}

	@Override
	public String getName() {
		return "ts.playerheaddrop";
	}
}