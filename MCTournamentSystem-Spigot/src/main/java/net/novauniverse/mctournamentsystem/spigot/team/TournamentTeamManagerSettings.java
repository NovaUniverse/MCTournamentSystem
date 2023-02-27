package net.novauniverse.mctournamentsystem.spigot.team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TournamentTeamManagerSettings {
	private boolean updateDisplayName;
	private boolean updateListName;
	private boolean updateNameColor;

	private List<UUID> displayNameUpdateExemptList;
	private List<UUID> listNameUpdateExemptList;
	private List<UUID> nameColorUpdateExemptList;

	private TournamentTeamManagerSettings() {
		this.updateDisplayName = true;
		this.updateListName = true;
		this.updateNameColor = true;

		this.displayNameUpdateExemptList = new ArrayList<>();
		this.listNameUpdateExemptList = new ArrayList<>();
		this.nameColorUpdateExemptList = new ArrayList<>();
	}

	public void setUpdateDisplayName(boolean updateDisplayName) {
		this.updateDisplayName = updateDisplayName;
	}

	public void setUpdateListName(boolean updateListName) {
		this.updateListName = updateListName;
	}

	public void setUpdateNameColor(boolean updateNameColor) {
		this.updateNameColor = updateNameColor;
	}

	public boolean shouldUpdateDisplayName() {
		return updateDisplayName;
	}

	public boolean shouldUpdateListName() {
		return updateListName;
	}

	public boolean shouldUpdateNameColor() {
		return updateNameColor;
	}

	public List<UUID> getDisplayNameUpdateExemptList() {
		return displayNameUpdateExemptList;
	}

	public List<UUID> getListNameUpdateExemptList() {
		return listNameUpdateExemptList;
	}

	public List<UUID> getNameColorUpdateExemptList() {
		return nameColorUpdateExemptList;
	}

	public static final TournamentTeamManagerSettings defaultSettings() {
		return new TournamentTeamManagerSettings();
	}
}