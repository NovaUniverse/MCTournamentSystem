import React from 'react'
import { Module } from '../../../scripts/dto/ServerDTO'

interface Props {
	module: Module;
}

export default function ModulesTableEntry({ module }: Props) {
	return (
		<tr>
			<td title={module.class_name}>{module.name}</td>
			<td className={module.enabled ? "table-success" : "table-danger"}>{module.enabled ? "Enabled" : "Disabled"}</td>
		</tr>
	)
}
