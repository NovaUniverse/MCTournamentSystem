import React from 'react'
import ServerDTO from '../../scripts/dto/ServerDTO';
import { Button, Col, Container, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import Utils from '../../scripts/utils/Utils';
import PluginTable from '../tables/plugins/PluginTable';
import ModulesTable from '../tables/modules/ModulesTable';
import StartServerButton from '../buttons/server/StartServerButton';
import StopServerButton from '../buttons/server/StopServerButton';
import KillServerButton from '../buttons/server/KillServerButton';
import ShowLogsButton from '../buttons/server/ServerLogsButton';

interface Props {
	visible: boolean;
	server: ServerDTO;
	onClose: () => void;
}

export default function ServerInfoModal({ visible, server, onClose }: Props) {

	function getBukkitVersion() {
		if (server.last_state_report.software != null) {
			return Utils.stringifyBukkitVersion(server.last_state_report.software.bukkit);
		}
		return "Unknown";
	}

	function getJavaVersion() {
		if (server.last_state_report.software != null) {
			return Utils.stringifyJavaVersion(server.last_state_report.software.java);
		}
		return "Unknown";
	}

	return (
		<Modal size='xl' show={visible} onHide={onClose} >
			<ModalHeader closeButton>
				<ModalTitle>Server info: {server.name}</ModalTitle>
			</ModalHeader>

			<ModalBody>
				<Container fluid>
					<Row>
						<Col>
							<h5>Software</h5>
							<p>
								Bukkit: {getBukkitVersion()}<br />
								Java: {getJavaVersion()}<br />
							</p>
							<hr />
						</Col>
					</Row>
					<Row>
						<Col>
							<h5>Controls</h5>
							<div>
								<StartServerButton server={server} className="me-2 mt-2" />
								<StopServerButton server={server} className="me-2 mt-2" />
								<KillServerButton server={server} className="me-2 mt-2" />
								<ShowLogsButton server={server} className='me-2 mt-2' />
							</div>
							<hr />
						</Col>
					</Row>
					<Row>
						<Col>
							<h5>Plugins</h5>
							<PluginTable server={server} />
							<hr />
						</Col>
					</Row>
					<Row>
						<Col>
							<h5>Modules</h5>
							<ModulesTable server={server} />
						</Col>
					</Row>
				</Container>
			</ModalBody>
			<ModalFooter>
				<Button variant="secondary" onClick={onClose}>Close</Button>
			</ModalFooter>
		</Modal>
	)
}
