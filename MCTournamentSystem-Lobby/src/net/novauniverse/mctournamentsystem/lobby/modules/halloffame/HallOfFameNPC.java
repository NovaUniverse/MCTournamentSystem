package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import net.zeeraa.novacore.commons.log.Log;

public class HallOfFameNPC {
	private NPC npc;
	private Location location;

	public HallOfFameNPC(Location location) {
		this.location = location;
		this.npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "HALL_OF_FAME");
		this.npc.spawn(location, SpawnReason.PLUGIN);
		Log.info("HallOfFameNPC", "Initialised npc with id: " + npc.getId());
	}

	public NPC getNPC() {
		return npc;
	}

	public Location getLocation() {
		return location;
	}
}