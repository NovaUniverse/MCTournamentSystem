import React, { useEffect, useState } from 'react'
import { Button, Col, Container, Row } from 'react-bootstrap'
import { useTournamentSystemContext } from '../context/TournamentSystemContext'
import ScoreDTO, { createEmptyScoreDTO } from '../scripts/dto/ScoreDTO';
import PlayerScoreTable from '../components/tables/score/player/PlayerScoreTable';
import TeamScoreTable from '../components/tables/score/team/TeamScoreTable';
import { Events } from '../scripts/enum/Events';
import { Permission } from '../scripts/enum/Permission';
import ConfirmModal from '../components/modals/ConfirmModal';
import toast from 'react-hot-toast';
import PageSelection from '../components/nav/PageSelection';

export default function Score() {
	const tournamentSystem = useTournamentSystemContext();

	const [score, setScore] = useState<ScoreDTO>(createEmptyScoreDTO());
	const [clearModalVisible, setClearModalVisible] = useState<boolean>(false);

	useEffect(() => {
		const onForcedUpdate = () => {
			update();
		}

		const interval = setInterval(() => {
			update();
		}, 1000);

		tournamentSystem.events.on(Events.FORCE_SCORE_UPDATE, onForcedUpdate);

		update();

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

	async function clearScore() {
		const response = await tournamentSystem.api.clearScore();
		if (response.success) {
			setClearModalVisible(false);
			toast.success("Score cleared");
			await update();
		} else {
			toast.error("Failed to clear score. " + response.message);
		}
	}

	return (
		<>
			<PageSelection />
			
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

				<Row className='mt-2'>
					<Col>
						<Button variant='danger' onClick={() => { setClearModalVisible(true) }} disabled={!tournamentSystem.authManager.hasPermission(Permission.ALTER_SCORE)}>Clear all</Button>
					</Col>
				</Row>
			</Container>

			<ConfirmModal onCancel={() => { setClearModalVisible(false) }} cancelButtonVariant='secondary' confirmButtonVariant='danger' confirmText='Clear' title='Clear score' visible={clearModalVisible} onConfirm={clearScore}>
				<p>
					Please confirm that you want to remove all player and team score
				</p>
			</ConfirmModal>
		</>
	)
}
