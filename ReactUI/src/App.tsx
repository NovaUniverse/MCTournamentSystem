import React, { useEffect, useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Overview from './pages/Overview'
import { useTournamentSystemContext } from './context/TournamentSystemContext';
import { Events } from './scripts/enum/Events';
import { Toaster } from 'react-hot-toast';
import GlobalNavbar from './components/navbar/GlobalNavbar';
import Servers from './pages/Servers';
import { Alert, Button } from 'react-bootstrap';
import Score from './pages/Score';
import AuthenticatedZone from './components/AuthenticatedZone';
import Whitelist from './pages/Whitelist';
import LiveStats from './pages/LiveStats';

export default function App() {
	const tournamentSystem = useTournamentSystemContext();
	const [loggedIn, setLoggedIn] = useState<boolean>(false);
	const [criticalError, setCriticalError] = useState<string | null>(tournamentSystem.criticalError);
	useEffect(() => {
		const handleLoginStateChange = () => {
			setLoggedIn(tournamentSystem.authManager.isLoggedIn);
		}

		const handleCrash = () => {
			setCriticalError(tournamentSystem.criticalError);
		}

		tournamentSystem.events.on(Events.LOGIN_STATE_CHANGED, handleLoginStateChange);
		tournamentSystem.events.on(Events.CRASH, handleCrash);
		return () => {
			tournamentSystem.events.off(Events.LOGIN_STATE_CHANGED, handleLoginStateChange);
			tournamentSystem.events.off(Events.CRASH, handleCrash);
		};
	}, []);

	return (
		<>
			{criticalError == null ?
				<>
					<GlobalNavbar loggedIn={loggedIn} />

					<>
						<Routes>
							{/* Authenticated zones */}
							<Route path="/" element={<AuthenticatedZone><Overview /></AuthenticatedZone>} />
							<Route path="/servers" element={<AuthenticatedZone><Servers /></AuthenticatedZone>} />
							<Route path="/score" element={<AuthenticatedZone><Score /></AuthenticatedZone>} />
							<Route path="/whitelist" element={<AuthenticatedZone><Whitelist /></AuthenticatedZone>} />

							{/* Unauthenticated zones */}
							<Route path="/live_stats" element={<LiveStats />} />

							{/* Misc */}
							<Route path="*" element={<>
								<h2>Page not found</h2>
							</>} />
						</Routes>
					</>
				</>
				:
				<>
					<div className='mx-2 my-2'>
						<Alert variant='danger '>TournamentSystemUI has crashed: {criticalError}</Alert>
						<Button onClick={() => { window.location.reload() }}>Reload</Button>
					</div>
				</>
			}

			<Toaster />
		</>
	)
}