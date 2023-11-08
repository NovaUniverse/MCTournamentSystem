import React from 'react'
import { Table } from 'react-bootstrap'
import PlayerList from './PlayerList'
import ScrollOnXOverflow from '../../ScrollOnXOverflow'

export default function PlayerTable() {
	return (
		<ScrollOnXOverflow>
			<Table bordered striped hover>
				<thead>
					<tr>
						<th className='t-fit'></th>
						<th>UUID</th>
						<th>Username</th>
						<th>Score</th>
						<th>Kills</th>
						<th>Team</th>
						<th>Team Score</th>
						<th>Status</th>
						<th>Ping</th>
						<th>Server</th>
						<th className='t-fit'></th>
					</tr>
				</thead>

				<tbody>
					<PlayerList />
				</tbody>
			</Table>
		</ScrollOnXOverflow>
	)
}
