import React, { useEffect, useState } from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Col, Container, Row } from 'react-bootstrap'
import { useTournamentSystemContext } from '../context/TournamentSystemContext'
import MapDataDTO from '../scripts/dto/MapDataDTO';
import { Events } from '../scripts/enum/Events';
import toast from 'react-hot-toast';
import MapsTable from '../components/tables/maps/MapsTable';

export default function Maps() {
	const tournamentSystem = useTournamentSystemContext();

	const [maps, setMaps] = useState<MapDataDTO[]>([]);

	useEffect(() => {
		const interval = setInterval(() => {
			fetchMaps();
		}, 3000);

		const handleMapsChange = () => {
			fetchMaps();
		}

		fetchMaps();

		tournamentSystem.events.on(Events.MAPS_CHANGED, handleMapsChange);

		return () => {
			tournamentSystem.events.off(Events.MAPS_CHANGED, handleMapsChange);
			clearInterval(interval);
		}
	}, []);

	async function fetchMaps() {
		try {
			const result = await tournamentSystem.api.getMaps();
			setMaps(result);
		} catch (err) {
			console.error("An error occured while fetching map list");
			console.error(err);
			toast.error("An error occured while fetching map list");
		}
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<h2>Maps</h2>
					</Col>
				</Row>
				<Row>
					<Col>
						<MapsTable maps={maps} />
					</Col>
				</Row>
			</Container>
		</>
	)
}