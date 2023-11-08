import React, { ChangeEvent, useEffect, useState } from 'react'
import { Col, FormCheck, Row } from 'react-bootstrap'
import AccountDTO from '../../../scripts/dto/AccountDTO';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';

interface Props {
	account?: AccountDTO;
	handlePermissions: (permissions: Map<string, boolean>) => void;
}

export default function AccountPermissions({ account, handlePermissions }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [permissions, setPermissions] = useState<Map<string, boolean>>(new Map<string, boolean>());

	useEffect(() => {
		handlePermissions(permissions);
	}, [permissions]);

	useEffect(() => {
		let newPermissions = new Map<string, boolean>();
		if (account != null) {
			tournamentSystem.validPermissions.forEach(p => {
				newPermissions.set(p, account.permissions.includes(p))
			});
		}
		setPermissions(newPermissions);
	}, []);

	function handlePermissionChange(e: ChangeEvent<any>) {
		const permission = e.target.attributes['data-permission'].value;
		const newPermissions = new Map<string, boolean>(permissions);
		newPermissions.set(permission, e.target.checked);
		setPermissions(newPermissions);
	}

	function isActive(permission: string) {
		return permissions.get(permission) == true;
	}

	return (
		<Row>
			{tournamentSystem.validPermissions.map(p =>
				<Col className='col-12' key={p}>
					<FormCheck type="switch" data-permission={p} label={p} checked={isActive(p)} onChange={handlePermissionChange} />
				</Col>
			)}
		</Row>
	)
}
