package net.novauniverse.mctournamentsystem.commons.tests.pid;

import org.junit.Test;

import net.novauniverse.mctournamentsystem.commons.tests.utils.OSFetcher;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;

public class PIDTest {
	@Test
	public void testGetPID() {
		int ownPid = ProcessUtils.getOwnPID();
		System.out.println("Our PID is " + ownPid);
		assert ownPid > 0;
	}

	@Test
	public void isProcessRunningTest() {
		OSFetcher.OSType os = OSFetcher.getOperatingSystemType();

		System.out.println("Detected OS: " + os.name());
		if (os == OSFetcher.OSType.Windows || os == OSFetcher.OSType.Linux) {
			int ownPid = ProcessUtils.getOwnPID();
			System.out.println("Our own is " + ownPid + ". Checking if we are running");
			assert ProcessUtils.isProcessRunning(ownPid) : "ProcessUtils.isProcessRunning is reporting that we are not alive";
		} else {
			System.out.println("Cant run isProcessRunning test since the os was not detected as a supported os for tournament system");
		}
	}
}