import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext'
import StateDTO, { Trigger } from '../../../scripts/dto/StateDTO';
import { Events } from '../../../scripts/enum/Events';
import GameTriggerCard from './GameTriggerCard';
import { Col } from 'react-bootstrap';

export default function GameTriggerCardsList() {
	const tournamentSystem = useTournamentSystemContext();

	const [triggers, setTriggers] = useState<Trigger[]>([]);

	useEffect(() => {
		const handleStateChange = (state: StateDTO) => {
			updateTriggerList();
		}

		tournamentSystem.events.on(Events.STATE_UPDATE, handleStateChange);

		updateTriggerList();

		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStateChange);
		}
	}, []);

	function updateTriggerList() {
		const triggers: Trigger[] = [];

		tournamentSystem.state.player_server_data.forEach(server => {
			if (server.metadata.triggers != null) {
				server.metadata.triggers.forEach(trigger => {
					if (triggers.filter(t => t.session_id == trigger.session_id && t.name == trigger.name).length == 0) {
						triggers.push(trigger);
					}
				});
			}
		});

		setTriggers(triggers);
	}

	return (
		<>
			{triggers.map(trigger =>
				<Col key={trigger.session_id + "_" + trigger.name} lg={3} md={4} sm={6} xs={12}>
					<GameTriggerCard trigger={trigger} />
				</Col>
			)}
		</>
	)
}
