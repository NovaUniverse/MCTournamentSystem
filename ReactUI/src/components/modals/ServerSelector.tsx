import React, { ChangeEvent, useEffect, useState } from 'react'
import { Button, Col, Container, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import toast from 'react-hot-toast';
import { Server, StateDTO } from '../../scripts/dto/StateDTO';
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import { Events } from '../../scripts/enum/Events';

interface Props {
	visible: boolean;
	title?: string;
	text?: string;
	cancelText?: string;
	confirmText?: string;
	onClose: () => void;
	onSubmit: (serverName: string) => void;
}

export default function ServerSelector({ visible, text, title = "Select server", onClose, onSubmit, cancelText = "Cancel", confirmText = "Confirm" }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [servers, setServers] = useState<Server[]>(tournamentSystem.state.servers);
	const [server, setServer] = useState<string>();

	useEffect(() => {
		const handleStateUpdate = (state: StateDTO) => {
			setServers(state.servers);
		}
		tournamentSystem.events.on(Events.STATE_UPDATE, handleStateUpdate);
		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStateUpdate);
		};
	}, []);

	useEffect(() => {
		console.debug("Resetting server picker modal");
		if (servers.length > 0) {
			setServer(servers[0].name);
		}
	}, [visible]);


	function handleServerChange(e: ChangeEvent<any>) {
		setServer(e.target.value);
	}

	function handleSubmit() {
		if (server == null) {
			toast.error("Please select a server");
			return;
		}

		if (servers.find(s => s.name == server) == null) {
			toast.error("The selected server was not found");
			return;
		}

		onSubmit(server);
	}

	return (
		<Modal show={visible} onHide={onClose}>
			<ModalHeader closeButton>
				<ModalTitle>{title}</ModalTitle>
			</ModalHeader>

			<ModalBody>
				<Container fluid>
					{text != null &&
						<Row>
							<Col>
								{text}
							</Col>
						</Row>
					}
					<Row>
						<Col>
							<FormSelect onChange={handleServerChange} value={server}>
								{servers.map((s) => <option key={s.name} value={s.name}>{s.name}</option>)}
							</FormSelect>
						</Col>
					</Row>
				</Container>
			</ModalBody>
			<ModalFooter>
				<Button variant="secondary" onClick={onClose}>
					{cancelText}
				</Button>
				<Button variant="success" onClick={handleSubmit}>
					{confirmText}
				</Button>
			</ModalFooter>
		</Modal>
	)
}
