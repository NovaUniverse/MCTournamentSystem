package net.novauniverse.mctournamentsystem.commons.utils;

import java.io.File;

public class TSFileUtils {
	public static File getParentSafe(File file) {
		return new File(file.getParentFile().getAbsolutePath());
	}
}