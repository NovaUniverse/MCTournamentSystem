import React, { useState } from 'react'
import NextMinigameText from './NextMinigameText'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext'
import TextPromptModal from '../modals/TextPromptModal';
import { Permission } from '../../scripts/enum/Permission';

import "./ClickableNextMinigameText.scss";
import toast from 'react-hot-toast';

export default function ClickableNextMinigameText() {
	const tournamentSystem = useTournamentSystemContext();

	const [promptVisible, setPromptVisible] = useState<boolean>(false);

	async function submitNext(name: string) {
		const req = await tournamentSystem.api.setNextMinigame(name);
		if (req.success) {
			toast.success("Next game updated");
			setPromptVisible(false);
			await tournamentSystem.updateState();
		} else {
			toast.error("" + req.message);
		}
	}

	async function clearNext() {
		await submitNext("");
	}

	function open() {
		if (tournamentSystem.authManager.hasPermission(Permission.SET_NEXT_MINIGAME)) {
			setPromptVisible(true);
		}
	}

	return (
		<>
			<span className={tournamentSystem.authManager.hasPermission(Permission.SET_NEXT_MINIGAME) ? "set-next-game-cursor" : ""} title='Click to change' onClick={open}>Next minigame: <NextMinigameText /></span>
			<TextPromptModal onSubmit={submitNext} onClose={() => { setPromptVisible(false) }} title='Set next minigame' visible={promptVisible} cancelText='Cancel' confirmText='Set next game' confirmType='primary' extraButtonVisible extraButtonText='Clear next game' extraButtonType='warning' placeholder='Next minigame name' onExtraButtonClick={clearNext} />
		</>
	)
}
