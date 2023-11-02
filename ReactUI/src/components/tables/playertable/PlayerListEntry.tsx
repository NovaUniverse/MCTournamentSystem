import React, { useEffect, useState } from 'react'
import { Player } from '../../../scripts/dto/StateDTO'
import { Badge } from 'react-bootstrap';
import PlayerHead from '../../PlayerHead';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import axios from 'axios';
import SendPlayerButton from '../../buttons/SendPlayerButton';

interface Props {
	player: Player;
}

export default function PlayerListEntry({ player }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [skinTexture, setSkinTexture] = useState<string | undefined>(undefined);

	const DEFAULT_TEXTURE = "c06f8906-4c8a-4911-9c29-ea1dbd1aab82";

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
			const response = await axios.get(tournamentSystem.apiUrl + "/skinrestorer/get_user_skin?username=" + player.username);
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

	function getTeamScore() {
		const team = tournamentSystem.state.teams.find(t => t.team_number == player.team_number);
		return team == null ? 0 : team.score;
	}

	const uuidToShow = tournamentSystem.state.system.offline_mode ? DEFAULT_TEXTURE : player.uuid;

	return (
		<>
			<tr key={player.uuid}>
				<td>
					<PlayerHead width={32} texture={skinTexture} uuid={skinTexture != null ? undefined : uuidToShow} />
				</td>
				<td>
					{player.uuid}
				</td>
				<td>
					{player.username}
				</td>
				<td>
					{player.score}
				</td>
				<td>
					{player.kills}
				</td>
				<td>
					{player.team_number}
				</td>
				<td>
					{getTeamScore()}
				</td>
				<td>
					{player.online ? <Badge bg='success'>Online</Badge> : <Badge bg='danger'>Offline</Badge>}
				</td>
				<td>
					{player.online ? player.ping : "N/A"}
				</td>
				<td>
					{player.online ? player.server : "N/A"}
				</td>
				<td>
					<SendPlayerButton username={player.username} uuid={player.uuid} />
				</td>
			</tr>
		</>
	)
}
