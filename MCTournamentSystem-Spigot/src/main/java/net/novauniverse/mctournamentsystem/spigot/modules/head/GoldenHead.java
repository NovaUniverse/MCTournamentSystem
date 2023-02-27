package net.novauniverse.mctournamentsystem.spigot.modules.head;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import net.novauniverse.mctournamentsystem.spigot.modules.cooldown.TickCooldown;
import net.novauniverse.mctournamentsystem.spigot.textures.Textures;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentSound;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

@NovaAutoLoad(shouldEnable = true)
public class GoldenHead extends NovaModule implements Listener {
	private Map<UUID, TickCooldown> cooldown = new HashMap<UUID, TickCooldown>();

	public GoldenHead() {
		super("TournamentSystem.GoldenHead");
	}

	@Override
	public void onEnable() throws Exception {
		ShapedRecipe recipe = VersionIndependentUtils.get().createShapedRecipeSafe(getItem(), getPlugin(), "goldenhead");

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
		ItemStack item = VersionIndependentUtils.get().getItemInMainHand(e.getPlayer());
		if (item != null) {
			if (NBTEditor.contains(item, "tournamentsystem", "goldenhead")) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getItem() != null) {
			if (NBTEditor.contains(e.getItem(), "tournamentsystem", "goldenhead")) {
				if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					e.setCancelled(true);

					Player player = e.getPlayer();

					cooldown.values().removeIf(TickCooldown::isCompleted);

					if (!cooldown.containsKey(player.getUniqueId())) {
						cooldown.put(player.getUniqueId(), new TickCooldown(100L));
						if (e.getItem().getAmount() > 1) {
							e.getItem().setAmount(e.getItem().getAmount() - 1);
						} else {
							if (player.getItemInHand().getAmount() == 1) {
								player.setItemInHand(null);
							} else {
								boolean removed = false;
								for (int i = 0; i < player.getInventory().getSize(); i++) {
									ItemStack item = player.getInventory().getItem(i);
									if (item != null) {
										if (item.getType() != Material.AIR) {
											if (NBTEditor.contains(item, "tournamentsystem", "goldenhead")) {
												if (item.getAmount() > 1) {
													item.setAmount(item.getAmount() - 1);
													removed = true;
													break;
												} else {
													player.getInventory().setItem(i, null);
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
						// p.getWorld().playSound(p.getLocation(), Sound.EAT, 1F, 1F);
						VersionIndependentSound.EAT.play(player);

						player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 30 * 20, 1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 15 * 20, 1));
						player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 60 * 20, 1));
					}
				}
			}
		}
	}
}