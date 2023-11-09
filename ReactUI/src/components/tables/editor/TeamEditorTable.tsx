import React, { useEffect, useState } from 'react'
import { Table } from 'react-bootstrap'
import { useTeamEditorContext } from '../../../context/TeamEditorContext';
import TeamEditorEntry from '../../../scripts/TeamEditorEntry';
import { Events } from '../../../scripts/enum/Events';
import TeamEditorPlayerEntry from './TeamEditorPlayerEntry';

interface Props {
	showMetadata: boolean;
	disableInputs: boolean;
}

export default function TeamEditorTable({ showMetadata, disableInputs }: Props) {
	const teamEditor = useTeamEditorContext();

	const [players, setPlayers] = useState<TeamEditorEntry[]>(teamEditor.players);

	useEffect(() => {
		const handleChange = (data: TeamEditorEntry[]) => {
			console.log("Change event");
			console.log(data);
			setPlayers(data);
		}

		teamEditor.tournamentSystem.events.on(Events.TEAM_EDITOR_UPDATE, handleChange);

		return () => {
			teamEditor.tournamentSystem.events.off(Events.TEAM_EDITOR_UPDATE, handleChange);
		}
	}, []);

	function sortPlayers(toSort: TeamEditorEntry[]): TeamEditorEntry[] {
		return toSort.sort((a, b) => { return a.team_number - b.team_number });
	}

	return (
		<>
			<Table striped bordered hover>
				<thead>
					<tr>
						<th className='t-fit'></th>
						<th>UUID</th>
						<th>Username</th>
						<th>Team Number</th>
						{showMetadata &&
							<th>Metadata</th>
						}
						<th className='t-fit'></th>
					</tr>
				</thead>

				<tbody>
					{sortPlayers(players).map(p => <TeamEditorPlayerEntry showMetadata={showMetadata} player={p} key={p.uuid} disableInputs={disableInputs} />)}
				</tbody>
			</Table>
		</>
	)
}
