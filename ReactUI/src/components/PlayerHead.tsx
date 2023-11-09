import React from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';

interface Props {
	uuid?: string;
	texture?: string;
	width?: number;
	height?: number;
}

export default function PlayerHead({ uuid, texture, width, height }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	if (uuid != null) {
		return (
			<img width={width} height={height} src={'https://mc-heads.net/avatar/' + uuid} />
		)
	} else if (texture != null) {
		return (
			<>
				{
					tournamentSystem.serviceProviders.skin_render_api == null ?
						<>PlayerHead: Error: No skin render service provider</>
						:
						<img width={width} height={height} src={tournamentSystem.serviceProviders.skin_render_api + "/from_image/face/skin.png?url=" + texture}></img>
				}
			</>
		)
	} else {
		return (
			<>PlayerHead: Error: No UUID or Texture provided</>
		)
	}
}
