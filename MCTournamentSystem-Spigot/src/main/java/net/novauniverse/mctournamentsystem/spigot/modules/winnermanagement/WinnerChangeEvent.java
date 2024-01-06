package net.novauniverse.mctournamentsystem.spigot.modules.winnermanagement;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WinnerChangeEvent extends Event {
	private static final HandlerList HANDLERS_LIST = new HandlerList();

	private int oldValue;
	private int newValue;

	public WinnerChangeEvent(int oldValue, int newValue) {
		this.oldValue = oldValue;
		this.newValue = newValue;
	}

	public int getOldValue() {
		return oldValue;
	}

	public int getNewValue() {
		return newValue;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLERS_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLERS_LIST;
	}
}