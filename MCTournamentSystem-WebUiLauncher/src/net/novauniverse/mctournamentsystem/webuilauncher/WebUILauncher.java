package net.novauniverse.mctournamentsystem.webuilauncher;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.utils.JSONFileUtils;

public class WebUILauncher {
	public static void main(String[] args) {
		System.out.println("Reading config...");
		File file = new File("tournamentconfig.json");
		if (!file.exists()) {
			fatalError("Error: Could not find tournamentconfig.json. Check that this program is loacated in the same directory as tournamentconfig.json");
			return;
		}
		
		JSONObject json;

		try {
			json = JSONFileUtils.readJSONObjectFromFile(file);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			fatalError("Failed to read tournamentconfig.json. " + e.getClass().getName() + " " + e.getMessage());
			return;
		}

		JSONObject webSettings = json.getJSONObject("web_ui");

		int port = webSettings.getInt("port");

		System.out.println("Port is " + port);
		
		URI uri;
		try {
			uri = new URI("http://localhost:" + port + "/app/");
		} catch (URISyntaxException e) {
			fatalError("Failed to parse url. " + e.getClass().getName() + " " + e.getMessage());
			return;
		}
		
		System.out.println("URL is " + uri);
		
		try {
			Desktop.getDesktop().browse(uri);
		} catch (Exception e) {
			fatalError("Failed to open browser. " + e.getClass().getName() + " " + e.getMessage());
			return;
		}
		
		System.out.println("Web ui opened in default browser");
	}

	private static void fatalError(String message) {
		System.err.println(message);
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}
}