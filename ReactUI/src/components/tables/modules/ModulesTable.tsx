import React, { useEffect, useState } from 'react'
import ServerDTO, { Module, Plugin } from '../../../scripts/dto/ServerDTO'
import { Table } from 'react-bootstrap';
import ModulesTableEntry from './ModulesTableEntry';

interface Props {
	server: ServerDTO;
}

export default function ModulesTable({ server }: Props) {
	const [modules, setModules] = useState<Module[]>(server.last_state_report.modules != null ? server.last_state_report.modules : []);

	useEffect(() => {
		setModules(server.last_state_report.modules != null ? server.last_state_report.modules : []);
	}, [server]);

	return (
		<Table bordered striped hover>
			<thead>
				<tr>
					<th>Name</th>
					<th>Version</th>
				</tr>
			</thead>
			<tbody>
				{modules.map((module) => <ModulesTableEntry key={module.class_name} module={module} />)}
			</tbody>
		</Table>
	)
}
