package net.novauniverse.mctournamentsystem.spigot.resourcepackmanager;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.resourcepackmanager.modules.resourcepack.ResourcePackManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.ModuleManager;

public class TournamentSystemResourcePackManager extends JavaPlugin {
	@Override
	public void onEnable() {
		ModuleManager.loadModule(this, ResourcePackManager.class);

		String resourcePackUrl = TournamentSystem.getInstance().getResourcePackUrl();

		File noResourcepackFile = new File(Bukkit.getServer().getWorldContainer() + File.separator + "NO_DOWNLOAD_RESOURCEPACK");
		if (noResourcepackFile.exists()) {
			Log.info("TournamentSystem", "Server resource pack disabled on this server");
		} else {
			if (resourcePackUrl != null) {
				ModuleManager.enable(ResourcePackManager.class);
				// Allow empty string to be treated as no pack
				if (resourcePackUrl.length() == 0) {
					resourcePackUrl = null;
				}
			}
		}
	}
}
