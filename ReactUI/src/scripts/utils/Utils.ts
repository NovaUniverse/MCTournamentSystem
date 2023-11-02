import { BukkitVersion, JavaVersion } from "../dto/ServerDTO";

export default class Utils {
	static stringifyBukkitVersion(bukkitVersion: BukkitVersion) {
		return bukkitVersion.bukkit_version + " " + bukkitVersion.version;
	}

	static stringifyJavaVersion(javaVersion: JavaVersion) {
		return javaVersion.version + " " + javaVersion.vm_name;
	}
}