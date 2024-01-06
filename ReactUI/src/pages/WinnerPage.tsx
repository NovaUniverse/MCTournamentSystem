import React, { useEffect, useState } from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Button, Col, Container, Row } from 'react-bootstrap'
import { useTournamentSystemContext } from '../context/TournamentSystemContext'
import StateDTO from '../scripts/dto/StateDTO';
import { Events } from '../scripts/enum/Events';
import { Permission } from '../scripts/enum/Permission';
import ConfirmModal from '../components/modals/ConfirmModal';
import toast from 'react-hot-toast';

export default function WinnerPage() {
	const tournamentSystem = useTournamentSystemContext();

	const [state, setState] = useState<StateDTO>(tournamentSystem.state);

	useEffect(() => {
		const onStateChange = (state: StateDTO) => {
			setState(state);
		}
		tournamentSystem.events.on(Events.STATE_UPDATE, onStateChange);

		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, onStateChange);
		}
	}, []);

	const [autoLockModalVisible, setAutoLockModalVisible] = useState<boolean>(false);
	const [clearModalVisible, setClearModalVisible] = useState<boolean>(false);

	async function selectWinner() {

	}

	async function confirmAutoLock() {
		setAutoLockModalVisible(false);
		try {
			await tournamentSystem.api.autoLockWinner();
			await tournamentSystem.updateState();
			setClearModalVisible(false);
		} catch (err) {
			console.error("Failed to auto lock winner");
			console.error(err);
			toast.error("An error occured while auto locking winner");
		}
	}

	async function clearWinner() {
		try {
			await tournamentSystem.api.clearLockedWinner();
			await tournamentSystem.updateState();
			setClearModalVisible(false);
		} catch (err) {
			console.error("Failed to clear winner");
			console.error(err);
			toast.error("An error occured while clearing locked winner");
		}
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<h4>Locked winner: {state.locked_winner > 0 ? <span className='text-info'>Team {state.locked_winner}</span> : <span className='text-danger'>None</span>}</h4>
					</Col>
				</Row>

				<Row>
					<Col>
						<Button variant='primary' className='me-2' onClick={selectWinner} disabled={!tournamentSystem.authManager.hasPermission(Permission.LOCK_WINNER)}>Select winner</Button>
						<Button variant='primary' className='me-2' onClick={() => { setAutoLockModalVisible(true) }} disabled={!tournamentSystem.authManager.hasPermission(Permission.LOCK_WINNER)}>Lock winner (Score based)</Button>
						<Button variant='danger' className='me-2' onClick={() => { setClearModalVisible(true) }} disabled={!tournamentSystem.authManager.hasPermission(Permission.LOCK_WINNER) || state.locked_winner == -1}>Clear winner</Button>
					</Col>
				</Row>

				<ConfirmModal onCancel={() => { setAutoLockModalVisible(false) }} onConfirm={confirmAutoLock} title='Confirm winner' visible={autoLockModalVisible} confirmText='Lock' confirmButtonVariant='success' cancelButtonVariant='danger'>
					Please confirm that you want to lock the winner as the player with the most score
				</ConfirmModal>

				<ConfirmModal onCancel={() => { setClearModalVisible(false) }} onConfirm={clearWinner} title='Clear winner' visible={clearModalVisible} confirmText='Clear' confirmButtonVariant='danger'>
					Please confirm that you want to clear the current winner
				</ConfirmModal>
			</Container>
		</>
	)
}
