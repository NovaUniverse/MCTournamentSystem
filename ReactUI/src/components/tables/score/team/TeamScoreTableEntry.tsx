import React from 'react'
import { TeamScoreEntry } from '../../../../scripts/dto/ScoreDTO'
import { Button } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../../context/TournamentSystemContext';
import { Permission } from '../../../../scripts/enum/Permission';
import { ScoreEntryType } from '../../../../scripts/enum/ScoreEntryType';
import toast from 'react-hot-toast';
import { Events } from '../../../../scripts/enum/Events';

interface Props {
	score: TeamScoreEntry;
}

export default function TeamScoreTableEntry({ score }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	async function deleteEntry() {
		const req = await tournamentSystem.api.deleteScoreEntry(ScoreEntryType.TEAM, score.id);
		if (req.success) {
			toast.success("Team score removed");
			tournamentSystem.events.emit(Events.FORCE_SCORE_UPDATE);
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<tr>
			<td className='t-fit'>{score.id}</td>
			<td>{score.team.team_number}</td>
			<td>{score.server}</td>
			<td>{score.reason}</td>
			<td>{score.amount}</td>
			<td>{score.gained_at}</td>
			<td>
				<Button variant='danger' onClick={deleteEntry} disabled={!tournamentSystem.authManager.hasPermission(Permission.ALTER_SCORE)}>Remove</Button>
			</td>
		</tr>
	)
}
