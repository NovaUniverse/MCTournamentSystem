import React, { useEffect, useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Overview from './pages/Overview'
import { useTournamentSystemContext } from './context/TournamentSystemContext';
import { Events } from './scripts/enum/Events';
import toast, { Toaster } from 'react-hot-toast';
import LoginModal from './components/modals/LoginModal';
import GlobalNavbar from './components/navbar/GlobalNavbar';
import Servers from './pages/Servers';
import { Alert, Button } from 'react-bootstrap';
import PageSelection from './components/nav/PageSelection';

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
            {criticalError == null ?
                <>
                    <GlobalNavbar loggedIn={loggedIn} />
                    {loggedIn ?
                        <>
                            <PageSelection />
                            <Routes>
                                <Route path="/" element={<Overview />} />
                                <Route path="/servers" element={<Servers />} />
                            </Routes>
                        </>
                        :
                        <>
                            <LoginModal onSubmit={login} visible showCloseButtons={false} />
                        </>
                    }
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