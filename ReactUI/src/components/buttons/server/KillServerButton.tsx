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

export default function KillServerButton({ server, className }: Props) {
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
		const req = await tournamentSystem.api.killServer(server.name);
		if (req.success) {
			toast.success("Server killed");
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button className={className} variant='danger' onClick={showModal} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_SERVERS) || !server.is_running}>Kill</Button>
			<ConfirmModal onConfirm={onConfirm} onCancel={hideModal} title='Kill server' visible={modalVisible} cancelButtonVariant='secondary' confirmButtonVariant='danger' confirmText='Kill server'>Confirm killing server process for {server.name}</ConfirmModal>
		</>
	)
}
