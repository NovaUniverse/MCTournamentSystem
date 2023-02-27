package net.novauniverse.mctournamentsystem.spigot.utils;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;

public class TSItemsAdderUtils {
	public static String addFontImages(String original) {
		return FontImageWrapper.replaceFontImages(original);
	}
}