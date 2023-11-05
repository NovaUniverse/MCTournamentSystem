import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import StateDTO from '../../scripts/dto/StateDTO';
import { Events } from '../../scripts/enum/Events';

export default function NextMinigameText() {
	const tournamentSystem = useTournamentSystemContext();

	const [state, setState] = useState<StateDTO>(tournamentSystem.state);

	useEffect(() => {
		const handleStateUpdate = (state: StateDTO) => {
			setState(state);
		}
		tournamentSystem.events.on(Events.STATE_UPDATE, handleStateUpdate);
		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStateUpdate);
		};
	}, []);

	return (
		<>{state.next_minigame == null ?
			<span className='text-danger'>None</span>
			:
			<span className='text-info'>{state.next_minigame}</span>
		}</>
	)
}
