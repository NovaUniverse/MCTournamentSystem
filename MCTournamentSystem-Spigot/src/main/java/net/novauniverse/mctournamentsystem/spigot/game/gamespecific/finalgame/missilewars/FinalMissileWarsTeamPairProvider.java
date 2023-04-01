package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.finalgame.missilewars;

import net.novauniverse.games.finalgame.missilewars.team.MissileWarsFinalGameTeamProvider;
import net.novauniverse.mctournamentsystem.spigot.team.DefaultTopTeamProvider;
import net.zeeraa.novacore.commons.utils.Pair;
import net.zeeraa.novacore.spigot.teams.Team;

public class FinalMissileWarsTeamPairProvider implements MissileWarsFinalGameTeamProvider {
	@Override
	public Pair<Team> getParticipants() {
		return DefaultTopTeamProvider.getTopParticipants();
	}
}