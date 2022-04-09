package net.novauniverse.mctournamentsystem.lobby.command.givemefireworks;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.permissions.PermissionDefault;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.command.AllowedSenders;
import net.zeeraa.novacore.spigot.command.NovaCommand;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;
import net.zeeraa.novacore.spigot.utils.RandomFireworkEffect;

public class GiveMeFireworksCommand extends NovaCommand {
	private Random random;

	public GiveMeFireworksCommand() {
		super("givemefireworks", TournamentSystem.getInstance());

		this.random = new Random();

		setDescription("FIREWORKS!!!!!!");
		setPermission("tournamentcore.command.givemefireworks");
		setPermissionDefaultValue(PermissionDefault.TRUE);
		setAllowedSenders(AllowedSenders.PLAYERS);

		setEmptyTabMode(true);
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		ItemStack fireworks = new ItemStack(Material.FIREWORK);

		fireworks.setAmount(10);

		FireworkMeta meta = (FireworkMeta) fireworks.getItemMeta();

		meta.setDisplayName(ChatColor.GREEN + "Fireworks");
		meta.setLore(ItemBuilder.generateLoreList(ChatColor.WHITE + "FIREWORKS"));
		meta.addEffect(RandomFireworkEffect.randomFireworkEffect(random));
		meta.setPower(2);

		fireworks.setItemMeta(meta);

		((Player) sender).getInventory().addItem(fireworks);

		return true;
	}
}