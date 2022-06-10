package net.novauniverse.mctournamentsystem.commons.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LinuxUtils {
	public static String getLinuxDistroPrettyName() {
		String[] cmd = { "/bin/sh", "-c", "cat /etc/*-release" };
		String prettyName = null;
		try {
			Process p = Runtime.getRuntime().exec(cmd);
			BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = bri.readLine()) != null) {
				if(line.startsWith("PRETTY_NAME=\"")) {
					prettyName = line.split("PRETTY_NAME=\"")[1];
					prettyName = prettyName.substring(0, prettyName.length() - 1);
				}
			}
			bri.close();
		} catch (Exception e) {
		}
		return prettyName;
	}
}