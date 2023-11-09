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
import NotFound from './pages/NotFound';
import Triggers from './pages/Triggers';
import Staff from './pages/Staff';
import DisconnectHandler from './components/DisconnectHandler';
import Maps from './pages/Maps';
import ChatLog from './pages/ChatLog';
import Accounts from './pages/Accounts';
import EditorProvider from './components/EditorProvider';

/// @ts-ignore
import catCry from "./assets/img/cat_cry.png";

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
			<GlobalNavbar loggedIn={loggedIn} />

			{criticalError == null ?
				<>
					<DisconnectHandler>
						<Routes>
							{/* Authenticated zones */}
							<Route path="/" element={<AuthenticatedZone><Overview /></AuthenticatedZone>} />
							<Route path="/servers" element={<AuthenticatedZone><Servers /></AuthenticatedZone>} />
							<Route path="/score" element={<AuthenticatedZone><Score /></AuthenticatedZone>} />
							<Route path="/whitelist" element={<AuthenticatedZone><Whitelist /></AuthenticatedZone>} />
							<Route path="/triggers" element={<AuthenticatedZone><Triggers /></AuthenticatedZone>} />
							<Route path="/staff" element={<AuthenticatedZone><Staff /></AuthenticatedZone>} />
							<Route path="/maps" element={<AuthenticatedZone><Maps /></AuthenticatedZone>} />
							<Route path="/chat" element={<AuthenticatedZone><ChatLog /></AuthenticatedZone>} />
							<Route path="/accounts" element={<AuthenticatedZone><Accounts /></AuthenticatedZone>} />
							<Route path="/editor" element={<AuthenticatedZone><EditorProvider /></AuthenticatedZone>} />

							{/* Unauthenticated zones */}
							<Route path="/live_stats" element={<LiveStats />} />

							{/* Misc */}
							<Route path="*" element={<NotFound />} />
						</Routes>
					</DisconnectHandler>
				</>
				:
				<>
					<div className='mx-2 my-2'>
						<Alert variant='danger '>
							<div className='d-flex'>
								<div className='w-100'>
									TournamentSystemUI has crashed: <span>{criticalError}</span>
								</div>
								<img className='align-self-end' src={catCry} width={32} height={32} />
							</div>
						</Alert>
						<Button onClick={() => { window.location.reload() }}>Reload</Button>
					</div>
				</>
			}



			<Toaster />
		</>
	)
}