import React from 'react'
import { Table } from 'react-bootstrap'
import StaffDTO from '../../../scripts/dto/StaffDTO';
import StaffTableEntry from './StaffTableEntry';
import ScrollOnXOverflow from '../../ScrollOnXOverflow';

interface Props {
	staff: StaffDTO;
}

export default function StaffTable({ staff }: Props) {
	return (
		<>
			<ScrollOnXOverflow>
				<Table striped bordered hover>
					<thead>
						<tr>
							<th className='t-fit'></th>
							<th>UUID</th>
							<th>Username</th>
							<th>Role</th>
							<th>Offline mode</th>
							<th className='t-fit'></th>
						</tr>
					</thead>

					<tbody>
						{staff.staff.map(s => <StaffTableEntry key={s.uuid} staff={staff} staffMember={s} />)}
					</tbody>
				</Table>
			</ScrollOnXOverflow>
		</>
	)
}
