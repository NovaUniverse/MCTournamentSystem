import React from 'react'
import ScoreDTO from '../../../../scripts/dto/ScoreDTO'
import { Button, Table } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../../context/TournamentSystemContext';
import { Permission } from '../../../../scripts/enum/Permission';
import PlayerScoreTableEntry from './PlayerScoreTableEntry';

interface Props {
	score: ScoreDTO;
}

export default function PlayerScoreTable({ score }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	return (
		<Table striped bordered hover>
			<thead>
				<tr>
					<th className='t-fit'>ID</th>
					<th>Username</th>
					<th>Server</th>
					<th>Reason</th>
					<th>Amount</th>
					<th>Gained at</th>
					<th className='t-fit'></th>
				</tr>
			</thead>

			<tbody>
				{score.players.map(s => <PlayerScoreTableEntry score={s} key={String(s.id)} />)}
			</tbody>

			<tbody>
				<tr>
					<td colSpan={6}></td>
					<td>
						<Button variant='success' disabled={!tournamentSystem.authManager.hasPermission(Permission.ALTER_SCORE)}>Add</Button>
					</td>
				</tr>
			</tbody>
		</Table>
	)
}
