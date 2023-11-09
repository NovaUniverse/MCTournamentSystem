export enum Theme {
	BOOTSTRAP = "Bootstrap",
	CERULEAN="Cerulean",
	COSMO="Cosmo",
	CYBORG="Cyborg",
	DARKLY = "Darkly",
	FLATLY = "Flatly",
	JOURNAL = "Journal",
	LITERA = "Litera",
	LUMEN = "Lumen",
	LUX = "Lux",
	MATERIA = "Materia",
	MINTY = "Minty",
	MORPH = "Morph",
	PULSE = "Pulse",
	QUARTZ = "Quartz",
	SANDSTONE = "Sandstone",
	SIMPLEX = "Simplex",
	SKETCHY = "Sketchy",
	SLATE = "Slate",
	SOLAR = "Solar",
	VAPOR = "Vapor",
	YETI = "Yeti",
	ZEPHYR = "Zephyr"
}

export function getThemeFix(theme: Theme): string | null {
	if(theme == Theme.DARKLY) {
		return "darkly";
	}

	if(theme == Theme.QUARTZ) {
		return "quartz";
	}

	return null;
}