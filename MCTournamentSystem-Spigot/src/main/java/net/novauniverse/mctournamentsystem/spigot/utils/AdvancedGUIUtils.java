package net.novauniverse.mctournamentsystem.spigot.utils;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.bukkit.entity.ItemFrame;
import org.bukkit.util.Vector;

import me.leoko.advancedgui.manager.GuiWallManager;
import me.leoko.advancedgui.manager.LayoutManager;
import me.leoko.advancedgui.utils.Direction;
import me.leoko.advancedgui.utils.GuiLocation;
import me.leoko.advancedgui.utils.GuiWallInstance;
import me.leoko.advancedgui.utils.Layout;

public class AdvancedGUIUtils {
	public static Layout getLayout(String name) {
		return LayoutManager.getInstance().getLayout(name);
	}

	public static WrappedAdvancedGUI placeLayout(String layout, int activationRadius, ItemFrame frame, Direction direction) {
		return placeLayout(getLayout(layout), activationRadius, frame, direction);
	}

	public static WrappedAdvancedGUI placeLayout(Layout layout, int activationRadius, ItemFrame frame, String direction) {
		return placeLayout(layout, activationRadius, frame, Direction.valueOf(direction));
	}

	public static WrappedAdvancedGUI placeLayout(String layout, int activationRadius, ItemFrame frame, String direction) {
		return placeLayout(getLayout(layout), activationRadius, frame, Direction.valueOf(direction));
	}

	public static WrappedAdvancedGUI placeLayout(@Nonnull Layout layout, int activationRadius, ItemFrame frame, Direction direction) {
		if (layout == null) {
			throw new IllegalArgumentException("layout is null");
		}
		GuiLocation guiLocation = new GuiLocation(frame.getLocation(), direction);
		GuiWallInstance guiWallInstance = new GuiWallInstance(GuiWallManager.getInstance().getNextId(), layout, activationRadius, guiLocation);
		guiWallInstance.queryItemFrames().forEach(frameContainer -> {
			Vector vector = frameContainer.getItemFrame().getLocation().toVector();
			GuiWallManager.getInstance().getActiveInstances().stream()
					.filter(guiInstances -> guiInstances != guiWallInstance && guiInstances.getLocation().getDirection().getBlockFace() == direction.getBlockFace() && guiInstances.getBoundingBox().contains(vector))
					.findAny().ifPresent(guiInstance -> GuiWallManager.getInstance().unregisterInstance(guiInstance, true));
		});
		guiWallInstance.populateFrames();
		GuiWallManager.getInstance().registerInstance(guiWallInstance, true);
		return new WrappedAdvancedGUI(guiWallInstance);
	}

	public static void deleteAllWithLayout(String layoutName) {
		List<GuiWallInstance> guis = GuiWallManager.getInstance().getActiveInstances().stream().filter(gui -> gui.getLayout().getName().equals(layoutName)).collect(Collectors.toList());
		guis.forEach(gui -> {
			GuiWallManager.getInstance().unregisterInstance(gui, true);
		});
	}
}