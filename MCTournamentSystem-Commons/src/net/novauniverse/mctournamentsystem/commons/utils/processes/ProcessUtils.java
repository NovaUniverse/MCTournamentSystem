package net.novauniverse.mctournamentsystem.commons.utils.processes;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class ProcessUtils {
	public static int getOwnPID() {
		String javaPid = System.getenv("JAVA_PID");
		
		if(javaPid != null) {
			if(javaPid.length() > 0) {
				return Integer.parseInt(javaPid);
			}
		}
		return Integer.parseInt(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
	}

	public static boolean isProcessRunning(int pid) {
		try {
			String os = System.getProperty("os.name").toLowerCase();
			String command;
			if (os.contains("windows")) {
				command = "tasklist /fi \"pid eq " + pid + "\"";
			} else {
				command = "ps -p " + pid;
			}
			Process process = Runtime.getRuntime().exec(command);
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(String.valueOf(pid))) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}