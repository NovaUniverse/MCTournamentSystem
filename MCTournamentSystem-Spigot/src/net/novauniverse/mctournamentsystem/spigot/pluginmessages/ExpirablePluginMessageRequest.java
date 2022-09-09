package net.novauniverse.mctournamentsystem.spigot.pluginmessages;

import java.util.UUID;

public class ExpirablePluginMessageRequest {
	private UUID requestId;
	private int timeLeft;

	public ExpirablePluginMessageRequest(UUID requestId, int time) {
		this.requestId = requestId;
		this.timeLeft = time;
	}

	public UUID getRequestId() {
		return requestId;
	}

	public int getTimeLeft() {
		return timeLeft;
	}

	public void decrement() {
		timeLeft--;
	}

	public boolean hasExpired() {
		return timeLeft <= 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UUID) {
			return ((UUID) obj).equals(this.getRequestId());
		}

		if (obj instanceof ExpirablePluginMessageRequest) {
			return ((ExpirablePluginMessageRequest) obj).getRequestId().equals(this.getRequestId());
		}

		return false;
	}
}