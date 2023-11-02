import React, { useEffect, useState } from 'react'
import { Routes, Route } from 'react-router-dom'
import Overview from './pages/Overview'
import { useTournamentSystemContext } from './context/TournamentSystemContext';
import { Events } from './scripts/enum/Events';
import toast, { Toaster } from 'react-hot-toast';
import LoginModal from './components/modals/LoginModal';
import GlobalNavbar from './components/navbar/GlobalNavbar';

export default function App() {
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
            <GlobalNavbar loggedIn={loggedIn} />
            {loggedIn ?
                <>
                    <Routes>
                        <Route path="/" element={<Overview />} />
                    </Routes>
                </>
                :
                <>
                    <LoginModal onSubmit={login} visible showCloseButtons={false} />
                </>
            }

            <Toaster />
        </>
    )
}