package net.novauniverse.mctournamentsystem.commons.tests.utils;

import java.util.Locale;

public class OSFetcher {
	public enum OSType {
		Windows, MacOS, Linux, Other
	};
	
	public static OSType getOperatingSystemType() {
		String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
		if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
			return OSType.MacOS;
		} else if (OS.indexOf("win") >= 0) {
			return OSType.Windows;
		} else if (OS.indexOf("nux") >= 0) {
			return OSType.Linux;
		}
		return OSType.Other;
	}
}