import React from 'react'
import { Button, Table } from 'react-bootstrap'
import { WhitelistEntry } from '../../../scripts/dto/StateDTO';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Permission } from '../../../scripts/enum/Permission';
import WhitelistTableEntry from './WhitelistTableEntry';

interface Props {
	entries: WhitelistEntry[];
}

export default function WhitelistTable({ entries }: Props) {
	const tournamentSystem = useTournamentSystemContext();


	function showAddUserModal() {

	}

	function showClearModal() {

	}

	return (
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
						<Button variant="danger" disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_WHITELIST)} onClick={showClearModal}>Clear</Button>
					</td>
					<td>
						<Button variant="success" disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_WHITELIST)} onClick={showAddUserModal}>Add</Button>
					</td>
				</tr>
			</tbody>
		</Table>
	)
}
