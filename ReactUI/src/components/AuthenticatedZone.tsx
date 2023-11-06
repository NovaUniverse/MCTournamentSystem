import React, { useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import { Events } from '../scripts/enum/Events';
import toast from 'react-hot-toast';
import LoginModal from './modals/LoginModal';

interface Props {
	children: any;
}

export default function AuthenticatedZone({ children }: Props) {
	const tournamentSystem = useTournamentSystemContext();
	const [loggedIn, setLoggedIn] = useState<boolean>(false);

	useEffect(() => {
		const handleLoginStateChange = () => {
			setLoggedIn(tournamentSystem.authManager.isLoggedIn);
		}

		tournamentSystem.events.on(Events.LOGIN_STATE_CHANGED, handleLoginStateChange);
		return () => {
			tournamentSystem.events.off(Events.LOGIN_STATE_CHANGED, handleLoginStateChange);
		};
	}, []);

	async function login(username: string, password: string) {
		try {
			const success = await tournamentSystem.authManager.login(username, password);
			if (success) {
				toast.success("Logged in");
			} else {
				toast.error("Invalid username or password");
			}
		} catch (err) {
			console.error("Failed to log in");
			console.error(err);
			toast.error("An error occured while logging in");
		}
	}

	return (
		<>
			{loggedIn ? children :
				<LoginModal onSubmit={login} visible showCloseButtons={false} />
			}
		</>
	)
}
