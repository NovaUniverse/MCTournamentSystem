import React, { useState } from 'react'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import toast from 'react-hot-toast';
import { Button } from 'react-bootstrap';
import ServerSelector from '../modals/ServerSelector';
import { Permission } from '../../scripts/enum/Permission';

interface Props {
	username: string;
	uuid: string;
	className?: string;
}

export default function SendPlayerButton({ username, uuid, className }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [sendPlayerModalVisible, setSendPlayerModalVisible] = useState<boolean>(false);

	function showSendModal() {
		setSendPlayerModalVisible(true);
	}

	function hideSendModal() {
		setSendPlayerModalVisible(false);
	}

	async function sendPlayer(server: string) {
		const req = await tournamentSystem.api.sendPlayerToServer(uuid, server);
		if (req.success) {
			toast.success("Player sent");
			setSendPlayerModalVisible(false);
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button className={className} variant='info' onClick={showSendModal} disabled={!tournamentSystem.authManager.hasPermission(Permission.SEND_PLAYERS)}>Send to</Button>
			<ServerSelector visible={sendPlayerModalVisible} onClose={hideSendModal} onSubmit={sendPlayer} confirmText='Send' title='Send player' text={"Select server to send " + username + " to"} />
		</>
	)
}
