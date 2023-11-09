import { createContext, useContext } from 'react';
import TeamEditor from '../scripts/TeamEditor';

export const TeamEditorContext = createContext<TeamEditor | undefined>(undefined);

export function useTeamEditorContext() {
	const context = useContext(TeamEditorContext);
	if (!context) {
		throw new Error('useTeamEditorContext must be used within a TeamEditorContextProvider');
	}
	return context;
}