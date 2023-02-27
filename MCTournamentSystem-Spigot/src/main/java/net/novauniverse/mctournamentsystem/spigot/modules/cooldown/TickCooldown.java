package net.novauniverse.mctournamentsystem.spigot.modules.cooldown;

public class TickCooldown {
	private long ticksLeft;
	private long initialTicks;

	public TickCooldown(long ticks) {
		this.ticksLeft = ticks;
		this.initialTicks = ticks;

		CooldownManager.getInstance().addCooldown(this);
	}

	/**
	 * Reset the cool down to its initial time
	 */
	public void resetTicks() {
		this.setTicksLeft(this.getInitialTicks());
	}

	public void setTicksLeft(long ticksLeft) {
		this.ticksLeft = ticksLeft;
	}

	public long getTicksLeft() {
		return ticksLeft;
	}

	public long getInitialTicks() {
		return initialTicks;
	}

	/**
	 * Decrements the cool down. Do not call this externally since this is handled
	 * by {@link CooldownManager}
	 */
	public void decrement() {
		if (ticksLeft > 0) {
			ticksLeft--;
		}
	}

	/**
	 * Check if the cool down is finished
	 * 
	 * @return <code>true</code> if the cool down is finished
	 */
	public boolean isCompleted() {
		return ticksLeft <= 0;
	}
}