import React, { ChangeEvent, useEffect } from 'react'
import TeamEditorEntry from '../../../scripts/TeamEditorEntry'
import { useTeamEditorContext } from '../../../context/TeamEditorContext';
import { Skins } from '../../../scripts/enum/Skins';
import PlayerHead from '../../PlayerHead';
import { Button, FormSelect } from 'react-bootstrap';
import toast from 'react-hot-toast';

interface Props {
	player: TeamEditorEntry;
	showMetadata: boolean;
	disableInputs: boolean;
}

export default function TeamEditorPlayerEntry({ player, showMetadata, disableInputs }: Props) {
	const teamEditor = useTeamEditorContext();

	const headUUID = teamEditor.offlineMode ? Skins.MHF_Steve : player.uuid;

	function remove() {
		teamEditor.players = teamEditor.players.filter(p => p.uuid != player.uuid);
		toast.success("Player removed");
	}

	function handleTeamChange(e: ChangeEvent<any>) {
		const players = teamEditor.players;
		const entry = players.find(p => p.uuid == player.uuid);
		if (entry != null) {
			entry.team_number = parseInt(e.target.value);
			teamEditor.players = players.map((x) => x);
		}
	}

	return (
		<tr>
			<th className='t-fit'>
				<PlayerHead uuid={headUUID} width={32} />
			</th>
			<th>{player.uuid}</th>
			<th>{player.username}</th>
			<th>
				<FormSelect autoFocus value={player.team_number} onChange={handleTeamChange} disabled={disableInputs}>
					{teamEditor.getTeamNumbersAsList().map(n =>
						<option key={n} value={n}>Team {n}</option>
					)}
				</FormSelect>
			</th>
			{showMetadata &&
				<th>
					{JSON.stringify(player.metadata)}
				</th>
			}

			<th>
				<Button variant='danger' onClick={remove} disabled={disableInputs}>Remove</Button>
			</th>
		</tr>
	)
}
