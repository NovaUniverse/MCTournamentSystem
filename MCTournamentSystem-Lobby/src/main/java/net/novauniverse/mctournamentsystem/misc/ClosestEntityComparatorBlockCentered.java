package net.novauniverse.mctournamentsystem.misc;

import java.util.Comparator;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public class ClosestEntityComparatorBlockCentered implements Comparator<Entity> {
	private Location origin;

	public ClosestEntityComparatorBlockCentered(Location origin) {
		this.origin = origin;
	}

	@Override
	public int compare(Entity entity1, Entity entity2) {
		double entity1Dist = entity1.getLocation().distance(origin);
		double entity2Dist = entity2.getLocation().distance(origin);

		return Double.compare(entity1Dist, entity2Dist);
	}
}