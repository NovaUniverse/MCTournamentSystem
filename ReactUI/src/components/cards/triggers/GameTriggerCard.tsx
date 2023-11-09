import React from 'react'
import { Trigger } from '../../../scripts/dto/StateDTO';
import { Badge, Button, Card, CardBody, CardFooter, CardText, CardTitle } from 'react-bootstrap';
import TriggerStatusBadge from './TriggerStatusBadge';
import StriggerFlagBadges from './TriggerFlagBadges';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Permission } from '../../../scripts/enum/Permission';
import toast from 'react-hot-toast';

interface Props {
	trigger: Trigger;
	className?: string;
}

export default function GameTriggerCard({ className, trigger }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	async function activateTrigger() {
		const req = await tournamentSystem.api.activateTrigger(trigger.name, trigger.session_id);
		if (req.success) {
			toast.success("Trigger activated");
		} else {
			toast.error("Failed to activate trigger. " + req.message);
		}
	}

	return (
		<>
			<Card className={className}>
				<CardBody>
					<CardTitle>
						<h3>
							{trigger.name}
						</h3>
						<h5>Server: <Badge bg='info'>{trigger.server}</Badge></h5>
					</CardTitle>
					<CardText>
						{trigger.description}
					</CardText>
					<hr />
					<CardText className="mt-2">
						Status: <TriggerStatusBadge trigger={trigger} /><br />
						Activation count: <Badge bg="info">{trigger.trigger_count}</Badge><br />
					</CardText>

					{trigger.ticks_left != null &&
						<CardText className="mt-2">
							Seconds left: <Badge bg="info">{Math.round(trigger.ticks_left / 20)}</Badge><br />
							Ticks left: <Badge bg="info">{trigger.ticks_left}</Badge><br />
						</CardText>
					}

					<CardText className="mt-2">
						Flags: <StriggerFlagBadges trigger={trigger} /><br />
					</CardText>


				</CardBody>
				<CardFooter>
					<Button variant='primary' onClick={activateTrigger} disabled={!tournamentSystem.authManager.hasPermission(Permission.MANAGE_TRIGGERS)}>Trigger</Button>
				</CardFooter>
			</Card >
		</>
	)
}
