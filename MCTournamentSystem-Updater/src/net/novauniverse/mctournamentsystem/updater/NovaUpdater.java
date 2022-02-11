package net.novauniverse.mctournamentsystem.updater;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.lingala.zip4j.ZipFile;
import net.novauniverse.mctournamentsystem.updater.api.NovaApi;
import net.novauniverse.mctournamentsystem.updater.license.LicenseData;
import net.novauniverse.mctournamentsystem.updater.license.LicenseUtils;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;

public class NovaUpdater implements Runnable {
	public static void main(String[] args) {
		new NovaUpdater().run();
	}

	private File tempDir;
	private File contentDir;

	private static void fatalError(String message) {
		System.err.println(message);
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}

	@Override
	public void run() {
		System.out.println("Checking internet connection...");
		if (NovaApi.connectivityCheck()) {
			System.out.println("Sucessfully connected to the novauniverse api");
		} else {
			fatalError("Error: Could not connect to the novauniverse api. check that you have a working internet connection and that novauniverse.net is not blocked");
			return;
		}

		System.out.println("Checking license key");
		File licenseFile = new File("license_key.txt");
		if (!licenseFile.exists()) {
			fatalError("Error: Could not find license_key.txt. Check that this program is loacated in the same directory as license_key.txt");
			return;
		}

		String licenseKey;
		try {
			licenseKey = FileUtils.readFileToString(licenseFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
			fatalError("Error: Could not find read license_key.txt");
			return;
		}

		try {
			System.out.println("Checking if license is valid");
			LicenseData data = LicenseUtils.CheckLicense(licenseKey);

			if (data.isValid()) {
				fatalError("Error: A valid license key is required to update the system");
				return;
			}

			if (data.isDemo()) {
				fatalError("Error: Demo keys cant be used to update the system");
				return;
			}

			if (!data.isActive()) {
				fatalError("Error: Your license key has expires. Please use an active key or extend the time of your key");
				return;
			}

			System.out.println("Valid license found. Nice >:]");
			System.out.println("Licensed to: " + data.getOwner());
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to validate the license key");
			return;
		}

		System.out.println("Setting up temporary directory");
		try {
			tempDir = new File("updater_temp_data");
			if (tempDir.exists()) {
				tempDir.delete();
			}
			FileUtils.forceMkdir(tempDir);
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to set up temporary directory");
			return;
		}

		System.out.println("Downloading update...");
		File contentZipFile = new File(tempDir.getAbsolutePath() + File.separator + "content.zip");
		try {
			String url = "https://novauniverse.net/cdn/tournament_system/dist/index.php?key=" + licenseKey;
			this.downloadFile(url, contentZipFile);
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to download update");
			return;
		}
		System.out.println("Content downloaded. Extracting...");

		try {
			ZipFile zipFile = new ZipFile(contentZipFile);
			zipFile.extractAll(tempDir.getAbsolutePath());
			zipFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to extract zip file");
			return;
		}

		contentDir = new File(tempDir.getAbsolutePath() + File.separator + "content");

		JSONArray updateManifest;
		try {
			File manifest = new File(contentDir.getAbsolutePath() + File.separator + "update_manifest.json");
			updateManifest = JSONFileUtils.readJSONArrayFromFile(manifest);
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to read update manifest file");
			return;
		}

		for (int i = 0; i < updateManifest.length(); i++) {
			JSONObject fileData = updateManifest.getJSONObject(i);

			File from = new File(contentDir.getAbsolutePath() + File.separator + fileData.getString("from"));
			File to = new File(fileData.getString("to"));

			if (!to.getParentFile().exists()) {
				System.err.println("Cant extract " + from.getAbsolutePath() + " since the target directory " + to.getParentFile().getAbsolutePath() + " does not exits");
				continue;
			}

			try {
				if (to.exists()) {
					System.out.println("Deleting " + to.getAbsolutePath());
					if (to.isDirectory()) {
						FileUtils.deleteDirectory(to);
					} else {
						FileUtils.delete(to);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed  delete " + from.getAbsolutePath());
				continue;
			}

			try {
				System.out.println("Copying " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
				if (from.isDirectory()) {
					FileUtils.copyDirectory(from, to);
				} else {
					FileUtils.copyFile(from, to);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println("Failed to copy " + from.getAbsolutePath() + " to " + to.getAbsolutePath());
			}
		}
	}

	public void downloadFile(String url, File target) throws MalformedURLException, IOException {
		URL dlUrl = new URL(url);
		URLConnection conn = dlUrl.openConnection();
		// Fake UA so cloudflare lets us pass
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:31.0) Gecko/20100101 Firefox/31.0");
		conn.connect();
		FileUtils.copyInputStreamToFile(conn.getInputStream(), target);
	}
}