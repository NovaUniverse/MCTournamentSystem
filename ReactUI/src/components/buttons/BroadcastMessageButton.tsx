import React, { useState } from 'react'
import { Button } from 'react-bootstrap';
import { Permission } from '../../scripts/enum/Permission';
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import TextPromptModal from '../modals/TextPromptModal';
import toast from 'react-hot-toast';

interface Props {
	className?: string;
}

export default function BroadcastMessageButton({ className }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [promptVisible, setPromptVisible] = useState<boolean>(false);

	async function handleSubmit(text: string) {
		if (text.length == 0) {
			toast.error("Cant send an empty message");
			return;
		}

		const req = await tournamentSystem.api.broadcastMessage(text);
		if (req.success) {
			toast.success("Message sent");
			setPromptVisible(false);
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Button variant='secondary' className={className} onClick={() => { setPromptVisible(true) }} disabled={!tournamentSystem.authManager.hasPermission(Permission.BROADCAST_MESSAGE)}>Broadcast Message</Button>
			<TextPromptModal onClose={() => { setPromptVisible(false) }} onSubmit={handleSubmit} title='Broadcast message' visible={promptVisible} confirmText='Send' placeholder='Enter text to send' />
		</>
	)
}
