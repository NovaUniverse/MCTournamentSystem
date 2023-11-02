import React, { useState } from 'react'
import ServerDTO from '../../../scripts/dto/ServerDTO';
import { Badge, Button, Card, CardBody, CardFooter, CardText, CardTitle } from 'react-bootstrap';
import KillServerButton from '../../buttons/server/KillServerButton';
import StopServerButton from '../../buttons/server/StopServerButton';
import StartServerButton from '../../buttons/server/StartServerButton';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import toast from 'react-hot-toast';
import ServerInfoModal from '../../modals/ServerInfoModal';
import ShowLogsButton from '../../buttons/server/ServerLogsButton';
import ServerConsoleModal from '../../modals/console/ServerConsoleModal';

interface Props {
	server: ServerDTO;
	className?: string;
}

export default function ServerCard({ className, server }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [serverInfoModalVisible, setServerInfoModalVisible] = useState<boolean>(false);
	const [serverConsoleModalVisible, setServerConsoleModalVisible] = useState<boolean>(false);

	return (
		<>
			<Card className={className}>
				<CardBody>
					<CardTitle>
						<h3>
							{server.name}
						</h3>
					</CardTitle>
					<CardText>
						{server.is_running ?
							<Badge bg='success'>Online</Badge>
							:
							<Badge bg='danger'>Offline</Badge>
						}
					</CardText>

					<CardFooter>
						{server.is_running ?
							<>
								<KillServerButton server={server} className='me-2 mt-2' />
								<StopServerButton server={server} className='me-2 mt-2' />
							</>
							:
							<>
								<StartServerButton server={server} className='me-2 mt-2' />
							</>
						}
						<ShowLogsButton className='me-2 mt-2' server={server} />
						<Button className='me-2 mt-2' variant='secondary' onClick={() => { setServerInfoModalVisible(true) }}>Info</Button>

						<Button className='me-2 mt-2' variant='info' onClick={() => { setServerConsoleModalVisible(true) }}>Console</Button>
					</CardFooter>
				</CardBody>
			</Card>
			<ServerInfoModal server={server} visible={serverInfoModalVisible} onClose={() => { setServerInfoModalVisible(false) }} />
			<ServerConsoleModal server={server} visible={serverConsoleModalVisible} onClose={() => { setServerConsoleModalVisible(false) }} />
		</>
	)
}
