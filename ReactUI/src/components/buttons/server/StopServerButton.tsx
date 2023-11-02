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

export default function StopServerButton({ server, className }: Props) {
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
		const req = await tournamentSystem.api.execServerCommand(server.name, "stop");
		if (req.success) {
			toast.success("Stop command sent");
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button className={className} variant='danger' onClick={showModal} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_SERVERS) || !server.is_running}>Stop</Button>
			<ConfirmModal onConfirm={onConfirm} onCancel={hideModal} title='Stop server' visible={modalVisible} cancelButtonVariant='secondary' confirmButtonVariant='danger' confirmText='Stop server'>Confirm sending stop command to server process for {server.name}</ConfirmModal>
		</>
	)
}
