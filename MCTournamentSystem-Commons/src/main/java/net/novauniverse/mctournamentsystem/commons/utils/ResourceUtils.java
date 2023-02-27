package net.novauniverse.mctournamentsystem.commons.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ResourceUtils {
	public static final String readResourceFromJARAsString(String filename, Object plugin) throws IOException {
		InputStream is = plugin.getClass().getResourceAsStream(filename);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		isr.close();
		is.close();
		return sb.toString();
	}
}