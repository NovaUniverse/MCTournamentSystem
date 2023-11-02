import React, { useState } from 'react'
import ServerDTO from '../../../scripts/dto/ServerDTO'
import { Button } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import ConfirmModal from '../../modals/ConfirmModal';
import { Permission } from '../../../scripts/enum/Permission';
import toast from 'react-hot-toast';

interface Props {
	server: ServerDTO;
	className?: string;
}

export default function StartServerButton({ server, className }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [modalVisible, setModalVisible] = useState<boolean>(false);

	function showModal() {
		setModalVisible(true);
	}

	function hideModal() {
		setModalVisible(false);
	}

	async function onConfirm() {
		hideModal();
		const req = await tournamentSystem.api.startServer(server.name);
		if (req.success) {
			toast.success("Server started");
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button className={className} variant='success' onClick={showModal} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_SERVERS) || server.is_running}>Start</Button>
			<ConfirmModal onConfirm={onConfirm} onCancel={hideModal} title='Start server' visible={modalVisible} cancelButtonVariant='secondary' confirmButtonVariant='primary' confirmText='Start server'>Confirm starting server {server.name}</ConfirmModal>
		</>
	)
}
