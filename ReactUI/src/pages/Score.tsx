import React, { useEffect, useState } from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import { useTournamentSystemContext } from '../context/TournamentSystemContext'
import ScoreDTO, { createEmptyScoreDTO } from '../scripts/dto/ScoreDTO';
import PlayerScoreTable from '../components/tables/score/player/PlayerScoreTable';
import TeamScoreTable from '../components/tables/score/team/TeamScoreTable';
import { Events } from '../scripts/enum/Events';

export default function Score() {
	const tournamentSystem = useTournamentSystemContext();

	const [score, setScore] = useState<ScoreDTO>(createEmptyScoreDTO());

	useEffect(() => {
		const onForcedUpdate = () => {
			update();
		}

		const interval = setInterval(() => {
			update();
		}, 1000);

		tournamentSystem.events.on(Events.FORCE_SCORE_UPDATE, onForcedUpdate);

		return () => {
			tournamentSystem.events.off(Events.FORCE_SCORE_UPDATE, onForcedUpdate);
			clearInterval(interval);
		}
	}, []);

	async function update() {
		const response = await tournamentSystem.api.getScore();
		if (response.success) {
			setScore(response.data as ScoreDTO);
		} else {
			console.error("Failed to fetch score. " + response.message);
		}
	}

	return (
		<Container fluid>
			<Row>
				<Col>
					<h4>Player score</h4>
					<PlayerScoreTable score={score} />
				</Col>
			</Row>

			<Row className='mt-2'>
				<Col>
					<h4>Team score</h4>
					<TeamScoreTable score={score} />
				</Col>
			</Row>
		</Container>
	)
}
