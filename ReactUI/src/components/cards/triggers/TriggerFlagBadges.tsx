import React from 'react'
import { Trigger } from '../../../scripts/dto/StateDTO'
import { Badge } from 'react-bootstrap';

interface Props {
	trigger: Trigger;
}

export default function StriggerFlagBadges({ trigger }: Props) {
	return (
		<>
			{trigger.flags.map(flag => <Badge bg='secondary' className='mx-1' key={flag}>{flag}</Badge>)}
		</>
	)
}
