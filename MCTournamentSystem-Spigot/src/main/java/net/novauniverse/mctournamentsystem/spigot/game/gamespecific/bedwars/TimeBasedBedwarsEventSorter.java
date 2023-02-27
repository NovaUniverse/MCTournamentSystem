package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bedwars;

import java.util.Comparator;

import net.novauniverse.bedwars.game.config.event.BedwarsEvent;

public class TimeBasedBedwarsEventSorter implements Comparator<BedwarsEvent> {
	@Override
	public int compare(BedwarsEvent o1, BedwarsEvent o2) {
		return o1.getTimeLeft() - o2.getTimeLeft();
	}
}