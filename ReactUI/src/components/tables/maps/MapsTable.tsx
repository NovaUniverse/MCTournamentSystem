import React from 'react'
import MapDataDTO from '../../../scripts/dto/MapDataDTO'
import { Table } from 'react-bootstrap'
import MapsTableEntry from './MapsTableEntry'

interface Props {
	maps: MapDataDTO[]
}

export default function MapsTable({ maps }: Props) {
	return (
		<>
			<Table striped bordered hover>
				<thead>
					<tr>
						<th>Name</th>
						<th>Game</th>
						<th>UUID</th>
						<th>Status</th>
						<th className='t-fit'></th>
					</tr>
				</thead>

				<tbody>
					{maps.map(map => <MapsTableEntry key={map.uuid} map={map} />)}
				</tbody>
			</Table>
		</>
	)
}