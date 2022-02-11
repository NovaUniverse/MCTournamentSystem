package net.novauniverse.mctournamentsystem.updater.api;

import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.updater.utils.WebRequestUtil;

public class NovaApi {
	public static boolean connectivityCheck() {
		try {
			JSONObject json = WebRequestUtil.getResource("https://novauniverse.net/api/connectivity_check/");
			if (json != null) {
				return json.getBoolean("success");
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}
}
