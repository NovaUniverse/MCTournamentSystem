import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import ServerDTO from '../../../scripts/dto/ServerDTO';
import { Events } from '../../../scripts/enum/Events';
import { Col } from 'react-bootstrap';
import ServerCard from './ServerCard';

export default function ServerCardsList() {
	const tournamentSystem = useTournamentSystemContext();

	const [servers, setServers] = useState<ServerDTO[]>(tournamentSystem.servers);

	useEffect(() => {
		const handleStateUpdate = (servers: ServerDTO[]) => {
			setServers(servers);
		}
		tournamentSystem.events.on(Events.SERVER_UPDATE, handleStateUpdate);
		return () => {
			tournamentSystem.events.off(Events.SERVER_UPDATE, handleStateUpdate);
		};
	}, []);

	return (
		<>
			{servers.map((server) =>
				<Col key={server.name} lg={3} md={4} sm={6} xs={12}>
					<ServerCard server={server} className='mx-1 my-1' />
				</Col>
			)}
		</>
	)
}
