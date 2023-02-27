package net.novauniverse.mctournamentsystem.commons.tests.linuxutils;

import org.junit.Test;

import net.novauniverse.mctournamentsystem.commons.tests.utils.OSFetcherForTesting;
import net.novauniverse.mctournamentsystem.commons.tests.utils.OSFetcherForTesting.OSType;
import net.novauniverse.mctournamentsystem.commons.utils.LinuxUtils;

public class TestLinuxUtils {
	@Test
	public void testGetDistroName() throws Exception {
		if(OSFetcherForTesting.getOperatingSystemType() != OSType.Linux) {
			System.out.println("Cant run TestLinuxUtils since build is not running on linux");
			return;
		}
		
		String name = LinuxUtils.getLinuxDistroPrettyName();

		assert name != null : "LinuxUtils.getLinuxDistroPrettyName() returned null";
		assert name.length() > 0 : "LinuxUtils.getLinuxDistroPrettyName() returned en empty string";
	}
}