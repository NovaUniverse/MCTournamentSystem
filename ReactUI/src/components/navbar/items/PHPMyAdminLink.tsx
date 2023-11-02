import axios from 'axios';
import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { NavLink } from 'react-bootstrap';

interface Props {
	useNavLink?: boolean
}

export default function PHPMyAdminLink({ useNavLink = false }: Props) {
	const tournamentSystem = useTournamentSystemContext();
	const [url, setUrl] = useState<string>("#");
	useEffect(() => {
		updateUrl();
	}, [])

	async function updateUrl() {
		const response = await axios.get(tournamentSystem.apiUrl + "/v1/system/web/phpmyadmin_url");
		setUrl(response.data.url);
	}

	return (
		<>
			{useNavLink ?
				<NavLink target='_blank' href={url}>PHPMyAdmin</NavLink>
				:
				<a target='_blank' href={url}>PHPMyAdmin</a>
			}
		</>

	)
}
