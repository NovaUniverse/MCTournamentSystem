import React, { useEffect, useState } from 'react'
import ServerDTO, { Plugin } from '../../../scripts/dto/ServerDTO'
import { Table } from 'react-bootstrap';
import PluginTableEntry from './PluginTableEntry';
import ScrollOnXOverflow from '../../ScrollOnXOverflow';

interface Props {
	server: ServerDTO;
}

export default function PluginTable({ server }: Props) {
	const [plugins, setPlugins] = useState<Plugin[]>(server.last_state_report.plugins != null ? server.last_state_report.plugins : []);

	useEffect(() => {
		setPlugins(server.last_state_report.plugins != null ? server.last_state_report.plugins : []);
	}, [server]);

	return (
		<ScrollOnXOverflow>
			<Table bordered striped hover>
				<thead>
					<tr>
						<th>Name</th>
						<th>Version</th>
					</tr>
				</thead>
				<tbody>
					{plugins.map((plugin) => <PluginTableEntry key={plugin.name} plugin={plugin} />)}
				</tbody>
			</Table>
		</ScrollOnXOverflow>
	)
}
