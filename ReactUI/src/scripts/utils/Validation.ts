export default class Validation {
	static validateMinecraftUsername(username: string): boolean {
		// Check the length of the username
		if (username.length < 3 || username.length > 16) {
			return false;
		}

		// Check if the username contains only valid characters
		const validCharacters = /^[a-zA-Z0-9_\-]+$/;
		if (!validCharacters.test(username)) {
			return false;
		}
		return true;
	}
}