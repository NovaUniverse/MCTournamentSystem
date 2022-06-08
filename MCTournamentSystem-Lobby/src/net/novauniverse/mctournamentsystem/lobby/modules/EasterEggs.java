package net.novauniverse.mctournamentsystem.lobby.modules;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import net.novauniverse.mctournamentsystem.spigot.textures.Textures;
import net.zeeraa.novacore.spigot.abstraction.enums.VersionIndependentMetarial;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.utils.ItemBuilder;

@NovaAutoLoad(shouldEnable = true)
public class EasterEggs extends NovaModule implements Listener {
	public EasterEggs() {
		super("TournamentSystem.Lobby.EasterEggs");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		// Give Noahkup a boat
		if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase("c8fbbcdf-2224-4db6-bd97-fcdbfeda8647")) {
			ItemBuilder builder = new ItemBuilder(VersionIndependentMetarial.OAK_BOAT);

			builder.setAmount(1);

			builder.addLore(ChatColor.WHITE + "Thanks for the boat");
			builder.addLore(ChatColor.WHITE + "during the 1n awards");
			builder.addLore(ChatColor.WHITE + "- Zeeraa01");

			e.getPlayer().getInventory().addItem(builder.build());
		}

		// Give CloakedLive an oreo
		if (e.getPlayer().getUniqueId().toString().equalsIgnoreCase("a54cddf2-d6b0-43d3-a47b-a2525f629d00")) {
			ItemBuilder builder = ItemBuilder.getPlayerSkullWithBase64TextureAsBuilder(Textures.OREO);

			builder.setName("Oreo");

			builder.setAmount(1);

			builder.addLore(ChatColor.WHITE + "Please dont eat it with a fork");

			e.getPlayer().getInventory().setItem(8, builder.build());
		}
	}
}