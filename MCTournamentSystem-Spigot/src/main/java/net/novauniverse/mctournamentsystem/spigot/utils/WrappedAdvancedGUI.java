package net.novauniverse.mctournamentsystem.spigot.utils;

import me.leoko.advancedgui.manager.GuiWallManager;
import me.leoko.advancedgui.utils.GuiWallInstance;

public class WrappedAdvancedGUI {
	private final GuiWallInstance gui;

	public WrappedAdvancedGUI(GuiWallInstance gui) {
		this.gui = gui;
	}

	public GuiWallInstance getGui() {
		return gui;
	}

	public void delete() {
		try {
			GuiWallManager.getInstance().unregisterInstance(gui, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}