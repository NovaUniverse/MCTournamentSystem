import React from 'react'
import { Button, Table } from 'react-bootstrap'
import { WhitelistEntry } from '../../../scripts/dto/StateDTO';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Permission } from '../../../scripts/enum/Permission';
import WhitelistTableEntry from './WhitelistTableEntry';
import ScrollOnXOverflow from '../../ScrollOnXOverflow';

interface Props {
	entries: WhitelistEntry[];
	onAddButtonClicked: () => void;
	onClearButtonClicked: () => void;
}

export default function WhitelistTable({ entries, onAddButtonClicked, onClearButtonClicked }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	return (
		<ScrollOnXOverflow>
			<Table bordered striped hover>
				<thead>
					<tr>
						<td className='t-fit'></td>
						<td>UUID</td>
						<td>Username</td>
						<td>Offline mode</td>
						<td className='t-fit'></td>
					</tr>
				</thead>

				<tbody>
					{entries.map(e => <WhitelistTableEntry entry={e} key={e.uuid} />)}
				</tbody>

				<tbody>
					<tr>
						<td colSpan={4}>
							<Button variant="danger" disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_WHITELIST)} onClick={onClearButtonClicked}>Clear</Button>
						</td>
						<td>
							<Button variant="success" disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_WHITELIST)} onClick={() => { onAddButtonClicked() }}>Add</Button>
						</td>
					</tr>
				</tbody>
			</Table>
		</ScrollOnXOverflow>
	)
}
