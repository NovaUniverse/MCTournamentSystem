import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, useLocation, useNavigate } from 'react-router-dom'

import './index.scss'

import App from './App'
import { TournamentSystemContext } from './context/TournamentSystemContext'
import TournamentSystem from './scripts/TournamentSystem'
import RandomDivs from './components/RandomDivs'

const root = document.getElementById('root')

const tournamentSystem = new TournamentSystem();

if (root) {
	ReactDOM
		.createRoot(root)
		.render(
			<>
				<TournamentSystemContext.Provider value={tournamentSystem}>
					<BrowserRouter>
						<App />
						<RandomDivs />
					</BrowserRouter>
				</TournamentSystemContext.Provider>
			</>
		)
}