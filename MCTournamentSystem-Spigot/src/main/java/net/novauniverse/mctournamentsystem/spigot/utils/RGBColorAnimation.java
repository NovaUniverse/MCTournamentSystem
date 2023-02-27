package net.novauniverse.mctournamentsystem.spigot.utils;

import net.md_5.bungee.api.ChatColor;

public class RGBColorAnimation {
	private ChatColor[] colors = { ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.LIGHT_PURPLE, ChatColor.LIGHT_PURPLE };
	private int index;

	public RGBColorAnimation() {
		this.index = 0;
	}

	public ChatColor nextChatColor() {
		index++;
		if (index >= colors.length) {
			index = 0;
		}
		return this.getChatColor();
	}

	public ChatColor getChatColor() {
		return colors[index];
	}
}