import React, { useEffect, useState } from 'react'
import { WhitelistEntry } from '../../../scripts/dto/StateDTO'
import { Button } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Permission } from '../../../scripts/enum/Permission';
import PlayerHead from '../../PlayerHead';
import axios from 'axios';
import toast from 'react-hot-toast';
import { Skins } from '../../../scripts/enum/Skins';

interface Props {
	entry: WhitelistEntry;
}

export default function WhitelistTableEntry({ entry }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [skinTexture, setSkinTexture] = useState<string | undefined>(undefined);

	useEffect(() => {
		const interval = setInterval(() => {
			updateSkin();
		}, 10000);

		updateSkin();

		return () => {
			clearInterval(interval);
		}
	}, []);

	async function updateSkin() {
		try {
			const response = await axios.get(tournamentSystem.apiUrl + "/skinrestorer/get_user_skin?username=" + entry.username);
			if (response.data.has_skin) {
				const skinData = JSON.parse(atob(response.data.skin_data));
				if (skinData.textures.SKIN != null) {
					setSkinTexture(skinData.textures.SKIN.url as string);
					return;
				}
			}
			setSkinTexture(undefined);
		} catch (err) {
			console.error("An error occured while fetching skin data");
			console.error(err);
		}
	}

	const uuidToShow = tournamentSystem.state.system.offline_mode ? Skins.MHF_Steve : entry.uuid;

	async function remove() {
		const req = await tournamentSystem.api.removeWhitelistUser(entry.uuid);
		if (req.success) {
			toast.success(entry.username + " was removed from the whitelist");
			await tournamentSystem.updateState();
		} else {
			toast.error("Failed to remove user from whitelist. " + req.message);
		}
	}



	return (
		<tr>
			<td>
				<PlayerHead width={32} texture={skinTexture} uuid={skinTexture != null ? undefined : uuidToShow} />
			</td>
			<td>{entry.uuid}</td>
			<td>{entry.username}</td>
			<td>{entry.offline_mode ? "Yes" : "No"}</td>
			<td>
				<Button variant='danger' disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_WHITELIST)} onClick={remove}>Remove</Button>
			</td>
		</tr>
	)
}
