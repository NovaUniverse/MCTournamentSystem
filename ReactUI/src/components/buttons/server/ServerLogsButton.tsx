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

export default function ShowLogsButton({ server, className }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	async function showLogs() {
		const response = await tournamentSystem.api.getServerLogs(server.name);

		if (response.success) {
			let content = "";

			response.data.log_data.forEach((line: string) => {
				content += line + "\n";
			});

			const win = window.open("", "Server log for " + server.name, "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes");
			if (win != null) {
				win.document.body.innerText = content;
			}

			toast.success("Showing logs in external window");
		} else {
			toast.error("" + response.message);
		}
	}

	return (
		<>
			<Button className={className} variant='dark' onClick={showLogs}>Logs</Button>
		</>
	)
}
