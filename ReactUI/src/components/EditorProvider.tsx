import React from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import TeamEditor from '../scripts/TeamEditor';
import { TeamEditorContext } from '../context/TeamEditorContext';
import Editor from '../pages/Editor';

export default function EditorProvider() {
	const tournamentSystem = useTournamentSystemContext();
	const teamEditor = new TeamEditor(tournamentSystem);

	return (
		<TeamEditorContext.Provider value={teamEditor}>
			<Editor />
		</TeamEditorContext.Provider>
	)
}
