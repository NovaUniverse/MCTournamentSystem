import React, { ChangeEvent, useEffect, useState } from 'react'
import StaffDTO, { StaffMember } from '../../../scripts/dto/StaffDTO'
import PlayerHead from '../../PlayerHead';
import { Button, FormSelect } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Permission } from '../../../scripts/enum/Permission';
import toast from 'react-hot-toast';
import { Events } from '../../../scripts/enum/Events';
import { Skins } from '../../../scripts/enum/Skins';

interface Props {
	staff: StaffDTO;
	staffMember: StaffMember;
}

export default function StaffTableEntry({ staff, staffMember }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [role, setRole] = useState<string>(staffMember.role);

	useEffect(() => {
		if (staffMember.role != role) {
			setRole(staffMember.role);
		}
	}, [staffMember]);

	const skinUUID = staffMember.offline_mode ? Skins.MHF_Steve : staffMember.uuid;

	function handleRoleChange(e: ChangeEvent<any>) {
		const newRole = e.target.value;
		setRole(newRole);
		updateRole(newRole);
	}

	async function updateRole(newRole: string) {
		console.debug("Change role to " + newRole);
		const req = await tournamentSystem.api.setUserStaffRole(staffMember.uuid, staffMember.username, newRole, staffMember.offline_mode);
		if (req.success) {
			toast.success("Role updated");
			tournamentSystem.events.emit(Events.STAFF_CHANGED);
		} else {
			console.error("Failed to update staff role. " + req.message);
			toast.error("Failed to update staff role. " + req.message);
		}
	}

	async function removeEntry() {
		const req = await tournamentSystem.api.removeStaffUser(staffMember.uuid);
		if (req.success) {
			toast.success("User removed");
			tournamentSystem.events.emit(Events.STAFF_CHANGED);
		} else {
			console.error("Failed to remove staff user. " + req.message);
			toast.error("Failed to remove user. " + req.message);
		}
	}

	return (
		<>
			<tr>
				<td className='t-fit'><PlayerHead uuid={skinUUID} width={32} /></td>
				<td>{staffMember.uuid}</td>
				<td>{staffMember.username}</td>
				<td>
					<FormSelect onChange={handleRoleChange} value={role} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_STAFF)}>
						{staff.staff_roles.map(role =>
							<option key={role} value={role}>{role}</option>
						)}
					</FormSelect>
				</td>
				<td>{staffMember.offline_mode ? "Yes" : "No"}</td>
				<td>
					<Button variant='danger' onClick={removeEntry} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_STAFF)}>Remove</Button>
				</td>
			</tr>
		</>
	)
}
