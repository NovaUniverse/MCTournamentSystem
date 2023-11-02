import React, { useEffect, useState } from 'react'
import { Player, StateDTO } from '../../../scripts/dto/StateDTO'
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Events } from '../../../scripts/enum/Events';
import { Badge } from 'react-bootstrap';
import PlayerListEntry from './PlayerListEntry';


export default function PlayerList() {
	const tournamentSystem = useTournamentSystemContext();
	const [players, setPlayers] = useState<Player[]>(tournamentSystem.state.players);
	useEffect(() => {
		const handleStateUpdate = (state: StateDTO) => {
			setPlayers(state.players);
		}
		tournamentSystem.events.on(Events.STATE_UPDATE, handleStateUpdate);
		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStateUpdate);
		};
	}, []);

	return (
		<>
			{players.map((player) =>
				<PlayerListEntry key={player.uuid} player={player} />
			)}
		</>
	)
}
