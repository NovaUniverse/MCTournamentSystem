import React from 'react'
import { Trigger } from '../../../scripts/dto/StateDTO'
import { Badge } from 'react-bootstrap';

interface Props {
	trigger: Trigger;
}

export default function TriggerStatusBadge({ trigger }: Props) {
	return (
		<>
			{trigger.running ?
				<Badge bg="success">Running</Badge>
				:
				<Badge bg="danger">Not running</Badge>
			}
		</>
	)
}
