import React, { useState } from 'react'
import { Button } from 'react-bootstrap'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext'
import { Permission } from '../../scripts/enum/Permission';
import ServerSelector from '../modals/ServerSelector';
import toast from 'react-hot-toast';

interface Props {
	className?: string
}

export default function SendAllButton({className}: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [modalVisible, setModalVisible] = useState<boolean>(false);

	function showModal() {
		setModalVisible(true);
	}

	function hideModal() {
		setModalVisible(false);
	}

	async function onPick(server: string) {
		const req = await tournamentSystem.api.sendAllToServer(server);
		if (req.success) {
			toast.success("Players sent");
			hideModal();
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button className={className} variant='primary' disabled={!tournamentSystem.authManager.hasPermission(Permission.SEND_PLAYERS)} onClick={showModal}>Send all players</Button>
			<ServerSelector onClose={hideModal} onSubmit={onPick} visible={modalVisible} confirmText='Send' text='Select server to send all players to' title='Send all players' />
		</>
	)
}
