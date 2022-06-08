package net.novauniverse.mctournamentsystem.spigot.modules.labymod;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.UUIDUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

@NovaAutoLoad(shouldEnable = true)
public class LabymodIntegration extends NovaModule implements Listener {
	private Task task;

	private static LabymodIntegration instance;

	public static LabymodIntegration getInstance() {
		return instance;
	}

	public LabymodIntegration() {
		super("TournamentSystem.LabymodIntegration");
	}

	@Override
	public void onLoad() {
		LabymodIntegration.instance = this;

		this.task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (TeamManager.hasTeamManager()) {
					Bukkit.getServer().getOnlinePlayers().forEach(player -> sendPlayerTeamTitles(player));
				}
			}
		}, 200L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (TournamentSystem.getInstance().getLabymodBanner() != null) {
			this.sendTabImage(e.getPlayer(), TournamentSystem.getInstance().getLabymodBanner());
		}

		if (TeamManager.hasTeamManager()) {
			this.sendPlayerTeamTitles(e.getPlayer());
		}
		this.setMiddleClickActions(e.getPlayer());

	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		this.sendCurrentPlayingGamemode(e.getPlayer(), false, "");
	}

	private void sendPlayerTeamTitles(Player player) {
		Bukkit.getServer().getOnlinePlayers().forEach(target -> {
			if (!UUIDUtils.isSame(player.getUniqueId(), target.getUniqueId())) {
				String text = ChatColor.YELLOW + "No team";
				Team t = TournamentSystem.getInstance().getTeamManager().getPlayerTeam(target);
				if (t != null) {
					if (t instanceof TournamentSystemTeam) {
						TournamentSystemTeam team = (TournamentSystemTeam) t;

						text = team.getTeamColor() + "Team " + team.getTeamNumber();
					}
				}

				this.setSubtitle(player, target.getUniqueId(), text);
			}
		});
	}

	private void sendTabImage(Player player, String url) {
		JsonObject object = new JsonObject();
		object.addProperty("url", url);
		VersionIndependentUtils.get().getLabyModProtocol().sendLabyModMessage(player, "server_banner", object);

		Log.trace("Sending banner " + url + " to player " + player.getName() + "(" + player.getUniqueId().toString() + ")");
	}

	public void setSubtitle(Player receiver, UUID subtitlePlayer, String value) {
		// List of all subtitles
		JsonArray array = new JsonArray();

		// Add subtitle
		JsonObject subtitle = new JsonObject();
		subtitle.addProperty("uuid", subtitlePlayer.toString());

		// Optional: Size of the subtitle
		subtitle.addProperty("size", 1.6d); // Range is 0.8 - 1.6 (1.6 is Minecraft default)

		// no value = remove the subtitle
		if (value != null) {
			subtitle.addProperty("value", value);
		}

		// If you want to use the new text format in 1.16+
		// subtitle.add("raw_json_text", textObject );

		// You can set multiple subtitles in one packet
		array.add(subtitle);

		// Send to LabyMod using the API
		VersionIndependentUtils.get().getLabyModProtocol().sendLabyModMessage(receiver, "account_subtitle", array);
	}

	/*
	 * public void updateKDR(Player player) { PlayerData data =
	 * PlayerDataManager.getPlayerData(player.getUniqueId());
	 * 
	 * int kills = data.getKills(); int deaths = data.getDeaths();
	 * 
	 * boolean updateKills = true; boolean updateDeaths = true;
	 * 
	 * if (killsCache.containsKey(player.getUniqueId())) { if
	 * (killsCache.get(player.getUniqueId()) == kills) { updateKills = false; } }
	 * 
	 * if (deathsCache.containsKey(player.getUniqueId())) { if
	 * (deathsCache.get(player.getUniqueId()) == deaths) { updateDeaths = false; } }
	 * 
	 * if (updateKills) { updateBalanceDisplay(player, EnumBalanceType.CASH, true,
	 * kills, KILLS_ICON); killsCache.put(player.getUniqueId(), kills); }
	 * 
	 * if (updateDeaths) { updateBalanceDisplay(player, EnumBalanceType.BANK, true,
	 * deaths, DEATHS_ICON); deathsCache.put(player.getUniqueId(), deaths); } }
	 */

	public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance) {
		this.updateBalanceDisplay(player, type, visible, balance, null);
	}

	public void updateBalanceDisplay(Player player, EnumBalanceType type, boolean visible, int balance, String icon) {
		JsonObject economyObject = new JsonObject();
		JsonObject cashObject = new JsonObject();

		// Visibility
		cashObject.addProperty("visible", visible);

		// Amount
		cashObject.addProperty("balance", balance);

		if (icon != null) {
			cashObject.addProperty("icon", icon);
		}
		/*
		 * 
		 * // Decimal number (Optional) JsonObject decimalObject = new JsonObject();
		 * decimalObject.addProperty("format", "##.##"); // Decimal format
		 * decimalObject.addProperty("divisor", 100); // The value that divides the
		 * balance cashObject.add( "decimal", decimalObject );
		 */

		// The display type can be "cash" or "bank".
		economyObject.add(type.getKey(), cashObject);

		// Send to LabyMod using the API
		VersionIndependentUtils.get().getLabyModProtocol().sendLabyModMessage(player, "economy", economyObject);
	}

	public void setMiddleClickActions(Player player) {
		JsonArray array = new JsonArray();
		JsonObject entry = new JsonObject();

		entry.addProperty("displayName", "Request to duel");
		entry.addProperty("type", EnumActionType.RUN_COMMAND.name());
		entry.addProperty("value", "duel {name}");
		array.add(entry);

		VersionIndependentUtils.get().getLabyModProtocol().sendLabyModMessage(player, "user_menu_actions", array);
	}

	public void sendCurrentPlayingGamemode(Player player, boolean visible, String gamemodeName) {
		JsonObject object = new JsonObject();
		object.addProperty("show_gamemode", visible); // Gamemode visible for everyone
		object.addProperty("gamemode_name", gamemodeName); // Name of the current playing gamemode

		// Send to LabyMod using the API
		VersionIndependentUtils.get().getLabyModProtocol().sendLabyModMessage(player, "server_gamemode", object);
	}
}