package net.novauniverse.mctournamentsystem.lobby.modules.celebrationmode;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.RandomFireworkEffect;

@NovaAutoLoad(shouldEnable = false)
public class LobbyCelebrationMode extends NovaModule implements Listener {
	private Random random;

	@Override
	public void onLoad() {
		this.random = new Random();
	}
	
	public LobbyCelebrationMode() {
		super("TournamentSystem.Lobby.CelebrationMode");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		ItemStack fireworks = new ItemStack(Material.FIREWORK);

		fireworks.setAmount(10);

		FireworkMeta meta = (FireworkMeta) fireworks.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN + "Fireworks");
		meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "FIREWORKS"));
		meta.addEffect(RandomFireworkEffect.randomFireworkEffect(random));
		meta.setPower(2);

		fireworks.setItemMeta(meta);

		e.getPlayer().getInventory().addItem(fireworks);
	}
}