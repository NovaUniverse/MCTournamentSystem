import React, { useState } from 'react'
import { Button } from 'react-bootstrap';
import { useTeamEditorContext } from '../../../context/TeamEditorContext';
import ConfirmModal from '../../modals/ConfirmModal';
import toast from 'react-hot-toast';

interface Props {
	className?: string;
	disabled?: boolean;
}

export default function DiscardChangesButton({ className, disabled = false }: Props) {
	const teamEditor = useTeamEditorContext();

	const [showModal, setShowModal] = useState<boolean>(false);

	async function clear() {
		try {
			await teamEditor.loadExistingTeam();
			setShowModal(false);
		} catch (err) {
			console.error("Failed to load team data");
			console.error(err);
			toast.error("Failed to import team from tournament system");
		}
	}

	return (
		<>
			<Button className={className} onClick={() => { setShowModal(true) }} variant='danger' disabled={disabled}>Discard changes</Button>
			<ConfirmModal onCancel={() => { setShowModal(false) }} onConfirm={clear} title='Confirm' visible={showModal} cancelText='Cancel' cancelButtonVariant='secondary' confirmButtonVariant='danger' confirmText='Discard' >
				<p>
					Please confirm that you want to discard changes and import existing team from tournament system
				</p>
			</ConfirmModal>
		</>
	)
}