import React from 'react'
import MapDataDTO from '../../../scripts/dto/MapDataDTO'
import { Badge, Button } from 'react-bootstrap'
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext'
import { Permission } from '../../../scripts/enum/Permission'
import toast from 'react-hot-toast'
import { Events } from '../../../scripts/enum/Events'

interface Props {
	map: MapDataDTO
}

export default function MapsTableEntry({ map }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	async function enable() {
		await setEnabled(true);
	}

	async function disable() {
		await setEnabled(false);
	}

	async function setEnabled(enabled: boolean) {
		const req = await tournamentSystem.api.setMapEnabled(map.uuid, enabled);
		if (req.success) {
			toast.success("Map " + (enabled ? "enabled" : "disabled"));
			tournamentSystem.events.emit(Events.MAPS_CHANGED);
		} else {
			const error = "Failed to " + (enabled ? "enable" : "disable") + " map. " + req.message;
			console.error(error);
			toast.error(error);
		}
	}

	return (
		<tr>
			<td>{map.name}</td>
			<td>{map.game}</td>
			<td>{map.uuid}</td>
			<td>
				{map.enabled ?
					<Badge bg='success'>Enabled</Badge>
					:
					<Badge bg='danger'>Disabled</Badge>
				}
			</td>
			<td className='t-fit'>
				{map.enabled ?
					<Button variant='danger' onClick={disable} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_MAPS)}>Disable</Button>
					:
					<Button variant='success' onClick={enable} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_MAPS)}>Enable</Button>
				}
			</td>
		</tr>
	)
}