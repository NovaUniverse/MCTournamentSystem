import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import { Alert, Col, Container, Row } from 'react-bootstrap';
import { Oval } from 'react-loading-icons'
import axios from 'axios';
import { Events } from '../scripts/enum/Events';

import "./DisconnectHandler.scss";

interface Props {
	children: any;
}

export default function DisconnectHandler({ children }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [disconnected, setDisconnected] = useState<boolean>(tournamentSystem.connectionLost);

	useEffect(() => {
		if (disconnected) {
			const interval = setInterval(() => {
				check();
			}, 5000);

			check();

			return () => {
				clearInterval(interval);
			}
		}
	}, [disconnected]);

	useEffect(() => {
		const handleDisconnect = () => {
			setDisconnected(true);
		}

		tournamentSystem.events.on(Events.DISCONNECTED, handleDisconnect);

		return () => {
			tournamentSystem.events.off(Events.DISCONNECTED, handleDisconnect);
		}
	}, []);

	async function check() {
		try {
			const response = await axios.get(tournamentSystem.apiUrl + "/v1/connectivity_check");
			if (response.data.success) {
				console.log("Connection restored");
				window.location.reload();
			} else {
				console.warn("Got response from server but it does not look like what we expect from tournament system");
			}
		} catch (err) {
			console.error("Could not connect to the sevrer");
			console.error(err);
		}
	}

	return (
		<>
			{disconnected ?
				<>
					<span className='disconnect-spinner'>
						<Oval />
					</span>
					<Container fluid className='mt-2'>
						<Row>
							<Col>
								<Alert variant='danger'>Connection lost</Alert>
							</Col>
						</Row>
						<Row className='mt-2'>
							<Col>
								<p>The backend server is not responding. The page will reload as soon as connection has been re-establish.</p>
							</Col>
						</Row>
					</Container>
				</>
				:
				<>
					{children}
				</>
			}
		</>
	)
}
