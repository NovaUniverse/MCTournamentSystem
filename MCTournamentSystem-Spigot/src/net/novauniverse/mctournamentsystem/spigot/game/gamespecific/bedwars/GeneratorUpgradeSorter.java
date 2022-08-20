package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bedwars;

import java.util.Comparator;

import net.novauniverse.bedwars.game.config.GeneratorUpgrade;

public class GeneratorUpgradeSorter implements Comparator<GeneratorUpgrade> {
	@Override
	public int compare(GeneratorUpgrade o1, GeneratorUpgrade o2) {
		return o1.getTimeLeft() - o2.getTimeLeft();
	}
}