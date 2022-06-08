package net.novauniverse.mctournamentsystem.spigot.modules.head;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.NovaCoreGameVersion;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

@NovaAutoLoad(shouldEnable = false)
public class EdibleHeads extends NovaModule implements Listener {
	public EdibleHeads() {
		super("TournamentSystem.EdibleHeads");
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			if (p.getItemInHand() != null) {

				boolean isPlayerSkull = false;
				ItemStack item = VersionIndependentUtils.get().getItemInMainHand(p);

				if (VersionIndependentUtils.get().getNovaCoreGameVersion() == NovaCoreGameVersion.V_1_12 || VersionIndependentUtils.get().getNovaCoreGameVersion() == NovaCoreGameVersion.V_1_8) {
					if (item.getType().name().equals("SKULL_ITEM")) {
						MaterialData data = e.getItem().getData();
						if (data.getData() == 3) {
							isPlayerSkull = true;
						}
					}
				} else {
					if (item.getType().name().equals("PLAYER_HEAD")) {
						isPlayerSkull = true;
					}
				}

				if (isPlayerSkull) {
					if (p.getGameMode() == GameMode.SURVIVAL || p.getGameMode() == GameMode.ADVENTURE) {

						e.setCancelled(true);

						if (item.getAmount() > 1) {
							item.setAmount(item.getAmount() - 1);
						} else {
							VersionIndependentUtils.get().setItemInMainHand(p, ItemBuilder.AIR);
						}

						p.getLocation().getWorld().playSound(p.getLocation(), Sound.EAT, 1F, 1F);
						p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 20, 0));
					}
				}
			}
		}
	}
}