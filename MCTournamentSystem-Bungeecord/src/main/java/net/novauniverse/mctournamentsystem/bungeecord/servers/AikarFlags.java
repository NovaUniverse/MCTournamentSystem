package net.novauniverse.mctournamentsystem.bungeecord.servers;

import java.util.ArrayList;
import java.util.List;

public class AikarFlags {
	public static final List<String> AIKAR_FLAGS = new ArrayList<>();
	
	static {
		AIKAR_FLAGS.add("--add-modules=jdk.incubator.vector");
		AIKAR_FLAGS.add("-XX:+UseG1GC");
		AIKAR_FLAGS.add("-XX:+ParallelRefProcEnabled");
		AIKAR_FLAGS.add("-XX:MaxGCPauseMillis=200");
		AIKAR_FLAGS.add("-XX:+UnlockExperimentalVMOptions");
		AIKAR_FLAGS.add("-XX:+DisableExplicitGC");
		AIKAR_FLAGS.add("-XX:+AlwaysPreTouch");
		AIKAR_FLAGS.add("-XX:G1HeapWastePercent=5");
		AIKAR_FLAGS.add("-XX:G1MixedGCCountTarget=4");
		AIKAR_FLAGS.add("-XX:InitiatingHeapOccupancyPercent=15");
		AIKAR_FLAGS.add("-XX:G1MixedGCLiveThresholdPercent=90");
		AIKAR_FLAGS.add("-XX:G1RSetUpdatingPauseTimePercent=5");
		AIKAR_FLAGS.add("-XX:SurvivorRatio=32");
		AIKAR_FLAGS.add("-XX:+PerfDisableSharedMem");
		AIKAR_FLAGS.add("-XX:MaxTenuringThreshold=1");
		AIKAR_FLAGS.add("-Dusing.aikars.flags=https://mcflags.emc.gs");
		AIKAR_FLAGS.add("-Daikars.new.flags=true");
		AIKAR_FLAGS.add("-XX:G1NewSizePercent=30");
		AIKAR_FLAGS.add("-XX:G1MaxNewSizePercent=40");
		AIKAR_FLAGS.add("-XX:G1HeapRegionSize=8M");
		AIKAR_FLAGS.add("-XX:G1ReservePercent=20");
	}
}