import React from 'react'
import { Plugin } from '../../../scripts/dto/ServerDTO'

interface Props {
	plugin: Plugin;
}

export default function PluginTableEntry({ plugin }: Props) {
	return (
		<tr className={plugin.enabled ? "" : "table-danger"}>
			<td title={"Authors: " + plugin.authors}>{plugin.name}</td>
			<td>{plugin.version}</td>
		</tr>
	)
}
