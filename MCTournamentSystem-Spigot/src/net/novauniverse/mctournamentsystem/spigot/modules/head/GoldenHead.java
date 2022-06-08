package net.novauniverse.mctournamentsystem.spigot.modules.head;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.bananapuncher714.nbteditor.NBTEditor;
import net.novauniverse.mctournamentsystem.spigot.textures.Textures;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

@NovaAutoLoad(shouldEnable = true)
public class GoldenHead extends NovaModule implements Listener {
	public GoldenHead() {
		super("TournamentSystem.GoldenHead");
	}

	@Override
	public void onEnable() throws Exception {
		ShapedRecipe recipe = new ShapedRecipe(getItem());

		recipe.shape("AAA", "ABA", "AAA");
		recipe.setIngredient('A', Material.GOLD_INGOT);

		VersionIndependentUtils.get().setShapedRecipeIngredientAsPlayerSkull(recipe, 'B');

		Bukkit.getServer().addRecipe(recipe);
	}

	public ItemStack getItem() {
		ItemStack stack = ItemBuilder.getPlayerSkullWithBase64Texture(Textures.GOLDEN_HEAD);

		ItemMeta meta = stack.getItemMeta();

		meta.setDisplayName(ChatColor.GOLD + "Golden head");

		stack.setItemMeta(meta);

		stack = NBTEditor.set(stack, 1, "tournamentsystem", "goldenhead");

		return stack;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockPlace(BlockPlaceEvent e) {
		if (NBTEditor.contains(e.getItemInHand(), "tournamentsystem", "goldenhead")) {
			e.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (NBTEditor.contains(e.getItem(), "tournamentsystem", "goldenhead")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					e.setCancelled(true);
					Player p = e.getPlayer();
					if (e.getItem().getAmount() > 1) {
						e.getItem().setAmount(e.getItem().getAmount() - 1);
					} else {
						if (p.getItemInHand().getAmount() == 1) {
							p.setItemInHand(null);
						} else {
							boolean removed = false;
							for (int i = 0; i < p.getInventory().getSize(); i++) {
								ItemStack item = p.getInventory().getItem(i);
								if (item != null) {
									if (item.getType() != Material.AIR) {
										if (NBTEditor.contains(item, "tournamentsystem", "goldenhead")) {
											if (item.getAmount() > 1) {
												item.setAmount(item.getAmount() - 1);
												removed = true;
												break;
											} else {
												p.getInventory().setItem(i, null);
												removed = true;
												break;
											}
										}
									}
								}
							}

							if (!removed) {
								return;
							}
						}
					}
					p.getWorld().playSound(p.getLocation(), Sound.EAT, 1F, 1F);

					p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, 1));
					p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 1));
				}
			}
		}
	}
}