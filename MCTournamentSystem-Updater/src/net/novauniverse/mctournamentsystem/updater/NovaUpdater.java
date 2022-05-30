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
import net.novauniverse.mctournamentsystem.updater.utils.ConsoleColor;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;

public class NovaUpdater implements Runnable {
	public static void main(String[] args) {
		NovaUpdater.args = args;
		new NovaUpdater().run();
	}

	private File tempDir;
	private File contentDir;

	public static String[] args = {};

	private static void fatalError(String message) {
		System.err.println(ConsoleColor.RED + message + ConsoleColor.RESET);
		JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.INFORMATION_MESSAGE);
		System.exit(0);
	}

	@Override
	public void run() {
		System.out.println(ConsoleColor.BLUE + "Checking internet connection..." + ConsoleColor.RESET);
		if (NovaApi.connectivityCheck()) {
			System.out.println(ConsoleColor.GREEN + "Sucessfully connected to the novauniverse api" + ConsoleColor.RESET);
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
			fatalError("Error: Could not read license_key.txt");
			return;
		}

		try {
			System.out.println(ConsoleColor.BLUE + "Checking if license is valid" + ConsoleColor.RESET);
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

			System.out.println(ConsoleColor.GREEN + "Valid license found. Nice >:]" + ConsoleColor.RESET);
			System.out.println(ConsoleColor.BLUE + "Licensed to: " + data.getOwner() + ConsoleColor.RESET);
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to validate the license key");
			return;
		}

		System.out.println(ConsoleColor.BLUE + "Setting up temporary directory" + ConsoleColor.RESET);
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

		System.out.println(ConsoleColor.BLUE + "Downloading update..." + ConsoleColor.RESET);
		File contentZipFile = new File(tempDir.getAbsolutePath() + File.separator + "content.zip");
		try {
			String url = "https://novauniverse.net/cdn/tournament_system/dist/index.php?key=" + licenseKey;
			this.downloadFile(url, contentZipFile);
		} catch (Exception e) {
			e.printStackTrace();
			fatalError("Error: Failed to download update");
			return;
		}
		System.out.println(ConsoleColor.GREEN + "Content downloaded. Extracting..." + ConsoleColor.RESET);

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
				System.err.println(ConsoleColor.RED + "Cant extract " + from.getAbsolutePath() + " since the target directory " + to.getParentFile().getAbsolutePath() + " does not exits" + ConsoleColor.RESET);
				continue;
			}

			try {
				if (to.exists()) {
					System.out.println(ConsoleColor.BLUE + "Deleting " + to.getAbsolutePath() + ConsoleColor.RESET);
					if (to.isDirectory()) {
						FileUtils.deleteDirectory(to);
					} else {
						FileUtils.delete(to);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(ConsoleColor.RED + "Failed to delete " + from.getAbsolutePath() + ConsoleColor.RESET);
				continue;
			}

			try {
				System.out.println(ConsoleColor.CYAN + "Copying " + from.getAbsolutePath() + " to " + to.getAbsolutePath() + ConsoleColor.RESET);
				if (from.isDirectory()) {
					FileUtils.copyDirectory(from, to);
				} else {
					FileUtils.copyFile(from, to);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(ConsoleColor.RED + "Failed to copy " + from.getAbsolutePath() + " to " + to.getAbsolutePath() + ConsoleColor.RESET);
			}
		}

		for (int i = 0; i < args.length; i++) {
			File from;
			File to;

			String[] split = args[i].split(":");

			try {
				from = new File(contentDir.getAbsolutePath() + File.separator + split[0]);
				to = new File(contentDir.getAbsolutePath() + File.separator + split[1]);
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println(ConsoleColor.RED + "Failed to parse extra argument " + i + ConsoleColor.RESET);
				continue;
			}

			if (!to.getParentFile().exists()) {
				System.err.println(ConsoleColor.RED + "Cant extract " + from.getAbsolutePath() + " since the target directory " + to.getParentFile().getAbsolutePath() + " does not exits" + ConsoleColor.RESET);
				continue;
			}

			try {
				if (to.exists()) {
					System.out.println(ConsoleColor.BLUE + "Deleting " + to.getAbsolutePath() + ConsoleColor.RESET);
					if (to.isDirectory()) {
						FileUtils.deleteDirectory(to);
					} else {
						FileUtils.delete(to);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(ConsoleColor.RED + "Failed to delete " + from.getAbsolutePath() + ConsoleColor.RESET);
				continue;
			}

			try {
				System.out.println(ConsoleColor.CYAN + "Copying " + from.getAbsolutePath() + " to " + to.getAbsolutePath() + ConsoleColor.RESET);
				if (from.isDirectory()) {
					FileUtils.copyDirectory(from, to);
				} else {
					FileUtils.copyFile(from, to);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.err.println(ConsoleColor.RED + "Failed to copy " + from.getAbsolutePath() + " to " + to.getAbsolutePath() + ConsoleColor.RESET);
			}
		}
	}

	public void downloadFile(String url, File target) throws MalformedURLException, IOException {
		URL dlUrl = new URL(url);
		URLConnection conn = dlUrl.openConnection();
		conn.setRequestProperty("User-Agent", "NovaUpdater 1.0");
		conn.connect();
		FileUtils.copyInputStreamToFile(conn.getInputStream(), target);
	}
}