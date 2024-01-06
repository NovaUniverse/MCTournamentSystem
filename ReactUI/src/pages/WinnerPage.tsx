import React, { ChangeEvent, useEffect, useState } from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Button, Col, Container, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap'
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

	const [selectedTeam, setSelectedTeam] = useState<number>(state.teams.length > 0 ? state.teams[0].team_number : -1);

	const [autoLockModalVisible, setAutoLockModalVisible] = useState<boolean>(false);
	const [clearModalVisible, setClearModalVisible] = useState<boolean>(false);
	const [selectWinnerModalVisible, setSelectWinnerModalVisible] = useState<boolean>(false);

	async function selectWinner() {
		setAutoLockModalVisible(false);
		try {
			await tournamentSystem.api.setLockedWinner(selectedTeam);
			await tournamentSystem.updateState();
			setSelectWinnerModalVisible(false);
			toast.success("Winner locked as team " + selectedTeam);
		} catch (err) {
			console.error("Failed to lock winner winner");
			console.error(err);
			toast.error("An error occured while locking winner team");
		}
	}

	async function confirmAutoLock() {
		try {
			await tournamentSystem.api.autoLockWinner();
			await tournamentSystem.updateState();
			setAutoLockModalVisible(false);
			toast.success("Winner locked");
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
			toast.success("Winner cleared");
		} catch (err) {
			console.error("Failed to clear winner");
			console.error(err);
			toast.error("An error occured while clearing locked winner");
		}
	}

	function handleTeamChange(e: ChangeEvent<any>) {
		setSelectedTeam(e.target.value);
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
						<Button variant='primary' className='me-2' onClick={() => { setSelectWinnerModalVisible(true) }} disabled={!tournamentSystem.authManager.hasPermission(Permission.LOCK_WINNER)}>Select winner</Button>
						<Button variant='primary' className='me-2' onClick={() => { setAutoLockModalVisible(true) }} disabled={!tournamentSystem.authManager.hasPermission(Permission.LOCK_WINNER)}>Auto select winner</Button>
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

			<Modal show={selectWinnerModalVisible} onHide={() => { setSelectWinnerModalVisible(false) }}>
				<ModalHeader closeButton>
					<ModalTitle>Select winner</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<p>
									Select team to mark as winner
								</p>
							</Col>
						</Row>

						<Row>
							<Col>
								<FormSelect value={selectedTeam} onChange={handleTeamChange}>
									{state.teams.map(t => <option key={t.id} value={t.team_number}>{t.display_name}</option>)}
								</FormSelect>
							</Col>
						</Row>
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="danger" onClick={() => { setSelectWinnerModalVisible(false) }}>Cancel</Button>
					<Button variant="success" onClick={selectWinner}>Select</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}
