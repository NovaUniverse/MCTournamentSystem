import { createContext, useContext } from 'react';
import TournamentSystem from '../scripts/TournamentSystem';

export const TournamentSystemContext = createContext<TournamentSystem | undefined>(undefined);

export function useTournamentSystemContext() {
    const context = useContext(TournamentSystemContext);
    if (!context) {
        throw new Error('useTournamentSystemContext must be used within a TournamentSystemContextProvider');
    }
    return context;
}