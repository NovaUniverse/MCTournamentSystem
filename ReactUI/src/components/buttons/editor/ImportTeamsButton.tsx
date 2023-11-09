import React, { ChangeEvent, useEffect, useState } from 'react'
import { useTeamEditorContext } from '../../../context/TeamEditorContext'
import { Button, Col, Container, FormControl, FormGroup, FormLabel, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import toast from 'react-hot-toast';
import TeamEditorEntry from '../../../scripts/TeamEditorEntry';

interface Props {
	className?: string;
	disabled?: boolean;
}

export default function ImportTeamsButton({ className, disabled = false }: Props) {
	const teamEditor = useTeamEditorContext();

	const [modalVisible, setModalVisible] = useState<boolean>(false);
	const [data, setData] = useState<string>("");

	useEffect(() => {
		if (modalVisible) {
			setData("");
		}
	}, [modalVisible]);

	function handleDataChange(e: ChangeEvent<any>) {
		setData(e.target.value);
	}

	function handleFileChange(e: ChangeEvent<any>) {
		const file = e.target.files[0];

		if (file) {
			const reader = new FileReader();
			reader.onload = (r) => {
				const content = r.target!.result;
				setData(String(content));
				toast.success("File loaded. Press import to load it into the editor");
			};
			reader.readAsText(file);
		}
	};

	function importData() {
		let json: any[] = [];
		let players: TeamEditorEntry[] = [];

		try {
			json = JSON.parse(data);
		} catch (err) {
			console.log("Failed to parse json");
			console.error(err);
			toast.error("Failed to parse data. Please check that the input is valid json and try again");
			return;
		}

		if (!Array.isArray(json)) {
			toast.error("The provided data does not seem to be valid team data");
			return;
		}

		try {
			json.forEach((entry: any) => {
				const username = entry.username;
				const uuid = entry.uuid;
				const teamNumber = entry.team_number;
				const metadata = entry.metadata;

				if (username == null || uuid == null || teamNumber == null || metadata == null) {
					console.log("Found entry missing one or more parameters");
					toast.error("One or more of the entries seems to not be a valid player entry. Please check the integrity of the data and try again");
					return;
				}

				players.push({
					metadata: metadata,
					team_number: teamNumber,
					username: username,
					uuid: uuid
				});
			});
		} catch (err) {
			console.log("Failed to parse team data");
			console.error(err);
			toast.error("Failed to parse data. Please check that the input is valid json and try again");
			return;
		}

		teamEditor.players = players;
		setModalVisible(false);
		toast.success("Teams imported to editor");
	}

	return (
		<>
			<Button autoFocus onClick={() => { setModalVisible(true) }} variant='primary' className={className} disabled={disabled}>Import team</Button>

			<Modal show={modalVisible} onHide={() => setModalVisible(false)} dialogClassName='modal-xl'>
				<ModalHeader closeButton>
					<ModalTitle>Add player</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormGroup>
									<FormLabel>Pase JSON here</FormLabel>
									<FormControl as="textarea" rows={10} value={data} onChange={handleDataChange} />
								</FormGroup>
								<hr />
								<FormGroup>
									<FormLabel>Or upload JSON file with team data</FormLabel>
									<FormControl type='file' onChange={handleFileChange} />
								</FormGroup>
							</Col>
						</Row>
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="secondary" onClick={() => { setModalVisible(false) }}>Cancel</Button>
					<Button variant="primary" onClick={importData}>Import</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}