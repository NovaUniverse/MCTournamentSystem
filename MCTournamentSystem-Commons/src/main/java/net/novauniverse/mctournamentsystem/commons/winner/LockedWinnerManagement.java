package net.novauniverse.mctournamentsystem.commons.winner;

import java.sql.SQLException;

import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.RabbitMQStrings;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.TournamentRabbitMQManager;

public class LockedWinnerManagement {
	private static final String DB_KEY = "locked_winner";

	public static void lockWinner(int teamNumber) throws SQLException {
		TournamentSystemCommons.setConfigValue(DB_KEY, "" + teamNumber);
		broadcastChange();
	}

	public static void unlockWinner() throws SQLException {
		unlockWinner(true);
	}
	
	public static void unlockWinner(boolean noRabbitMessage) throws SQLException {
		TournamentSystemCommons.setConfigValue(DB_KEY, null);
		if(!noRabbitMessage) {
			broadcastChange();
		}
	}

	public static int getLockedWinner() throws SQLException {
		String data = TournamentSystemCommons.getConfigValue("locked_winner");
		if (data != null) {
			try {
				int result = Integer.parseInt(data);
				return result;
			} catch (NumberFormatException e) {
				unlockWinner();
			}
		}
		return -1;
	}
	
	private static void broadcastChange() {
		if (TournamentSystemCommons.hasRabbitMQManager()) {
			TournamentRabbitMQManager manager = TournamentSystemCommons.getRabbitMQManager();
			if (manager.isConnected()) {
				manager.sendMessage(RabbitMQStrings.LOCKED_TEAM_CHANGED, new JSONObject());
				
			}
		}
	}
}