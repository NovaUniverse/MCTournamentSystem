import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import { StateDTO } from '../scripts/dto/StateDTO';
import { Events } from '../scripts/enum/Events';

export default function SystemStats() {
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

	function getAveragePing(): string {
		const count = state.online_players.length;

		if (count == 0) {
			return "N/A";
		}

		const total = state.online_players.reduce((accumulator, player) => {
			return accumulator + player.ping;
		}, 0);

		return String(total / count);
	}
	
	return (
		<p>
			<span>Player count: {state.online_players.length}</span><br />
			<span>Average player ping: {getAveragePing()}</span><br />
			<span>Server software: {state.system.proxy_software} {state.system.proxy_software_version}</span><br />
			<span>Host cores: {state.system.cores}</span><br />
			<span>Host OS: {state.system.os_name}</span><br />
			{state.system.linux_distro != null && <><span>Linux distro: {state.system.linux_distro}</span><br /></>}
			<span>Tournament name: {state.system.tournament_name}</span><br />
			<span>Scoreboard link: {state.system.scoreboard_url}</span><br />
			<span>Public IP: {state.system.public_ip}</span><br />
			<span>Dynamic config URL:
				{state.system.dynamic_config_url == null ?
					<span className='text-danger'>[Disabled]</span>
					:
					<span>{state.system.dynamic_config_url}</span>
				}
			</span><br />
		</p>
	)
}
