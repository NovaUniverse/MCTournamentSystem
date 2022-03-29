package net.novauniverse.mctournamentsystem.updater.license;

import java.io.IOException;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.updater.utils.WebRequestUtil;

public class LicenseUtils {
	public static LicenseData CheckLicense(String key) throws IOException {
		String url = "https://novauniverse.net/api/license/tournament_system/" + key;
		JSONObject json = WebRequestUtil.getResource(url);

		boolean valid = false;
		boolean active = false;
		boolean demo = false;
		String owner = null;

		if (json.getBoolean("success")) {
			JSONObject data = json.getJSONObject("data");
			active = data.getBoolean("is_active");
			demo = data.getBoolean("is_demo");
			owner = data.getString("owner");
		} else {
			valid = false;
		}

		return new LicenseData(valid, active, demo, owner);
	}

}