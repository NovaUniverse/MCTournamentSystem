package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

public enum UserAuthType {
	TOKEN, API_KEY;

	public String getDescription() {
		switch (this) {
		case API_KEY:
			return "API Key authentication";

		case TOKEN:
			return "Token authentication";
			
			default:
				return "Error: Unknown UserAuthType: " + this.name();
		}
	}
}