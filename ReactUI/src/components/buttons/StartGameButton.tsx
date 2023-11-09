import React, { useState } from 'react'
import { Button } from 'react-bootstrap'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext'
import { Permission } from '../../scripts/enum/Permission';
import ConfirmModal from '../modals/ConfirmModal';
import toast from 'react-hot-toast';

interface Props {
	className?: string;
}

export default function StartGameButton({ className }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [modalVisible, setModalVisible] = useState<boolean>(false);

	function showModal() {
		setModalVisible(true);
	}

	function hideModal() {
		setModalVisible(false);
	}

	async function start() {
		const req = await tournamentSystem.api.startGame();
		if (req.success) {
			toast.success("Game started");
			hideModal();
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button className={className} variant='success' onClick={showModal} disabled={!tournamentSystem.authManager.hasPermission(Permission.START_GAME)}>Start game</Button>
			<ConfirmModal onCancel={hideModal} onConfirm={start} title='Start game' visible={modalVisible} confirmText='Start game'>Confirm that you want to start the game</ConfirmModal>
		</>
	)
}
