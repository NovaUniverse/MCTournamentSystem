package net.novauniverse.mctournamentsystem.spigot.misc;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItem;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

public class CustomItemTest extends CustomItem {
	public CustomItemTest() {
	}

	@Override
	protected ItemStack createItemStack(Player player) {
		ItemBuilder builder = new ItemBuilder(Material.STICK);
		
		builder.setAmount(1);
		
		builder.setName(ChatColor.GREEN + "Developer stick");
		
		builder.addLore("This is a test item left in by the devs");
		builder.addLore("Give this to zeeraa for 1 free can of monster energy drink");
		builder.addLore("when visiting sweden next time");
		
		return builder.build();
	}
}