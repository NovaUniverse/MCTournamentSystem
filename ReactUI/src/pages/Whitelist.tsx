import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import StateDTO from '../scripts/dto/StateDTO';
import { Events } from '../scripts/enum/Events';
import { Col, Container, Row } from 'react-bootstrap';
import WhitelistTable from '../components/tables/whitelist/WhitelistTable';
import PageSelection from '../components/nav/PageSelection';

export default function Whitelist() {
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
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<WhitelistTable entries={state.whitelist}/>
					</Col>
				</Row>
			</Container>
		</>
	)
}
