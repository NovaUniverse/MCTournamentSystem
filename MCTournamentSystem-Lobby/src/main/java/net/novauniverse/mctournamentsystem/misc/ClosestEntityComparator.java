package net.novauniverse.mctournamentsystem.misc;

import java.util.Comparator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

import net.zeeraa.novacore.spigot.utils.LocationUtils;

public class ClosestEntityComparator implements Comparator<Entity> {
	private Location origin;

	public ClosestEntityComparator(Location origin) {
		this.origin = origin;
	}

	@Override
	public int compare(Entity entity1, Entity entity2) {
		double entity1Dist = LocationUtils.fullyCenterLocation(entity1.getLocation().clone()).distance(origin);
		double entity2Dist = LocationUtils.fullyCenterLocation(entity2.getLocation().clone()).distance(origin);

		return Double.compare(entity1Dist, entity2Dist);
	}
}