import React from 'react'
import AccountDTO from '../../../scripts/dto/AccountDTO';
import { Table } from 'react-bootstrap';
import AccountsTableEntry from './AccountsTableEntry';
import ScrollOnXOverflow from '../../ScrollOnXOverflow';

interface Props {
	accounts: AccountDTO[];
}

export default function AccountsTable({ accounts }: Props) {
	return (
		<>
			<ScrollOnXOverflow>
				<Table bordered striped hover >
					<thead>
						<tr>
							<th>Username</th>
							<th>Permissions</th>
							<th className='text-nowrap'>Hide IPs</th>
							<th className='text-nowrap'>Allow manage users</th>
							<th className='t-fit' colSpan={3}></th>
						</tr>
					</thead>

					<tbody>
						{accounts.map(a => <AccountsTableEntry account={a} key={a.username} />)}
					</tbody>
				</Table>
			</ScrollOnXOverflow>
		</>
	)
}
