import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

import './index.scss'

import App from './App'
import { TournamentSystemContext } from './context/TournamentSystemContext'
import TournamentSystem from './scripts/TournamentSystem'

const root = document.getElementById('root')

const tournamentSystem = new TournamentSystem();

import 'bootstrap/dist/css/bootstrap.min.css';

if (root) {
    ReactDOM
        .createRoot(root)
        .render(
            <React.StrictMode>
                <TournamentSystemContext.Provider value={tournamentSystem}>
                    <BrowserRouter>
                        <App />
                    </BrowserRouter>
                </TournamentSystemContext.Provider>
            </React.StrictMode>
        )
}