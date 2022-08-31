package net.novauniverse.mctournamentsystem.spigot.modules.chatfilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = false)
public class ChatFilter extends NovaModule implements Listener {
	public static final String NOTIFY_PERMISSION = "tournamentcore.notify.swear";
	public static final String DEFAULT_CHAT_FILTER_URL = "https://chatfilter.novauniverse.net/";
	public static final RequestConfig REQUEST_CONFIG;
	public static int API_TIMEOUT = 3000;

	private List<String> filteredCommands;
	private String filterUrl;

	static {
		REQUEST_CONFIG = RequestConfig.custom().setConnectTimeout(API_TIMEOUT).setConnectionRequestTimeout(API_TIMEOUT).setSocketTimeout(API_TIMEOUT).build();
	}

	public ChatFilter() {
		super("TournamentSystem.ChatFilter");

		filteredCommands = new ArrayList<>();
		filterUrl = ChatFilter.DEFAULT_CHAT_FILTER_URL;
	}

	public List<String> getFilteredCommands() {
		return filteredCommands;
	}

	public void setFilterUrl(String filterUrl) {
		this.filterUrl = filterUrl;
	}

	public String getFilterUrl() {
		return filterUrl;
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		Player player = e.getPlayer();

		CloseableHttpClient client = HttpClients.createDefault();

		try {
			HttpPost httpPost = new HttpPost(filterUrl);
			httpPost.setConfig(REQUEST_CONFIG);
			httpPost.setHeader("User-Agent", "MCTournamentSystem Chat Filter");

			StringEntity myEntity = new StringEntity(e.getMessage(), ContentType.create("text/plain", "UTF-8"));
			httpPost.setEntity(myEntity);

			CloseableHttpResponse response = client.execute(httpPost);

			int status = response.getStatusLine().getStatusCode();

			if (status != 200) {
				e.setCancelled(true);
				player.sendMessage(ChatColor.RED + "Chat message could not be sent since the chat filter service could not be reaced. Try again and if this still occurs contact an admin");
				Log.error("ChatFilter", "Failed to filter chat message. Server responded with code: " + status + ". Message: " + e.getMessage());
			} else {
				HttpEntity entity = response.getEntity();
				String responseString = EntityUtils.toString(entity, "UTF-8");
				JSONObject json = new JSONObject(responseString);

				if (!json.getBoolean("is_clean")) {
					Bukkit.getServer().broadcast(ChatColor.YELLOW + player.getName() + " failed swear check. Original message: " + ChatColor.AQUA + json.getString("original"), NOTIFY_PERMISSION);
					e.setMessage(json.getString("filtered"));
				}
			}

			response.close();
		} catch (IOException e1) {
			e.setCancelled(true);
			player.sendMessage(ChatColor.RED + "Chat message could not be sent since the chat filter service could not be reaced. Try again and if this still occurs contact an admin");
			Log.error("ChatFilter", "Failed to filter chat message. Exception thrown: " + e1.getClass().getName() + " " + e1.getMessage() + ". Message: " + e.getMessage());
		}

		try {
			client.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}