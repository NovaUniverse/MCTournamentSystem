import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import PlayerHead from './PlayerHead';
import axios from 'axios';

interface Props {
	uuid: string;
	username: string;
	width?: number;
	height?: number;
}

export default function PlayerHeadWithSkinrestorer({ uuid, username, width, height }: Props) {
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
			const response = await axios.get(tournamentSystem.apiUrl + "/skinrestorer/get_user_skin?username=" + username);
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

	return (
		<PlayerHead height={height} width={width} uuid={skinTexture != null ? undefined : uuid} texture={skinTexture} />
	)
}
