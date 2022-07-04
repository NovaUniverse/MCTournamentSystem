package net.novauniverse.mctournamentsystem.commons.team;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;

public class TeamOverrides {
	public static final Map<Integer, String> nameOverrides = new HashMap<>();
	public static final Map<Integer, ChatColor> colorOverrides = new HashMap<>();

	public static final void readOverrides(File dataFolder) {
		String globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(dataFolder)))).getAbsolutePath();

		File teamNameOverrideFile = new File(globalConfigPath + File.separator + "team_names.json");
		if (teamNameOverrideFile.exists()) {
			try {
				JSONObject overrides = JSONFileUtils.readJSONObjectFromFile(teamNameOverrideFile);
				overrides.keySet().forEach(key -> {
					try {
						Integer teamId = Integer.parseInt(key);
						nameOverrides.put(teamId, overrides.getString(key));
					} catch (NumberFormatException e) {
						Log.error("TournamentSystem", "Invalid team number " + key + " in team_names.json");
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("TournamentSystem", "Failed to read team_names.json");
			}
		}
		Log.info("TournamentSystem", nameOverrides.size() + " custom team names loaded");

		File teamColorOverrideFile = new File(globalConfigPath + File.separator + "team_colors.json");
		if (teamColorOverrideFile.exists()) {
			try {
				JSONObject overrides = JSONFileUtils.readJSONObjectFromFile(teamColorOverrideFile);
				overrides.keySet().forEach(key -> {
					try {
						Integer teamId = Integer.parseInt(key);
						colorOverrides.put(teamId, ChatColor.of(overrides.getString(key)));
					} catch (NumberFormatException e) {
						Log.error("TournamentSystem", "Invalid team number " + key + " in team_colors.json");
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("TournamentSystem", "Failed to read team_colors.json");
			}
		}
		Log.info("TournamentSystem", colorOverrides.size() + " custom team colors loaded");
	}
}