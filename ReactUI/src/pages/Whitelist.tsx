import React, { ChangeEvent, useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import StateDTO from '../scripts/dto/StateDTO';
import { Events } from '../scripts/enum/Events';
import { Button, Col, Container, FormCheck, FormControl, FormLabel, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import WhitelistTable from '../components/tables/whitelist/WhitelistTable';
import PageSelection from '../components/nav/PageSelection';
import ConfirmModal from '../components/modals/ConfirmModal';
import toast from 'react-hot-toast';
import Validation from '../scripts/utils/Validation';

export default function Whitelist() {
	const tournamentSystem = useTournamentSystemContext();

	const [state, setState] = useState<StateDTO>(tournamentSystem.state);

	useEffect(() => {
		const handleStateUpdate = (state: StateDTO) => {
			setState(state);
		}
		tournamentSystem.events.on(Events.STATE_UPDATE, handleStateUpdate);
		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStateUpdate);
		};
	}, []);

	/* CLEAR MODAL */
	const [clearModalVisible, setClearModalVisible] = useState<boolean>(false);

	function showClearModal() {
		setClearModalVisible(true);
	}

	async function clear() {
		const req = await tournamentSystem.api.clearWhitelist();
		if (req.success) {
			toast.success("Whitelist cleared");
			setClearModalVisible(false);
			await tournamentSystem.updateState();
		} else {
			toast.error("Failed to clear whitelist. " + req.message);
		}
	}

	/* ADD MODAL */
	const [addModalVisible, setAddModalVisible] = useState<boolean>(false);

	const [username, setUsername] = useState<string>("");
	const [offline, setOffline] = useState<boolean>(false);

	useEffect(() => {
		if (addModalVisible) {
			setUsername("");
			setOffline(false);
		}
	}, [addModalVisible]);

	function handleUsernameChange(e: ChangeEvent<any>) {
		setUsername(e.target.value);
	}

	function handleOfflineChange(e: ChangeEvent<any>) {
		setOffline(e.target.checked);
	}

	function closeAddModal() {
		setAddModalVisible(false);
	}

	function showAddModal() {
		setAddModalVisible(true);
	}

	async function handleAddUser() {
		let uuid: string | null = null;
		let trueUsername: string = username;
		if (offline) {
			const uuidLookup = await tournamentSystem.api.usernameToOfflineUserUUID(username);
			uuid = uuidLookup.uuid;
			console.log("Offline user " + username + " has a uuid of " + uuid);
		} else {
			if (!Validation.validateMinecraftUsername(username)) {
				toast.error("Invalid username provided");
				return;
			}

			try {
				const uuidLookup = await tournamentSystem.mojangApi.usernameToUUID(username);
				if (!uuidLookup.found) {
					console.error(username + " could not be found");
					toast.error("That user could not be found");
					return;
				}
				uuid = uuidLookup.uuid!;
				console.log(username + " has the uuid " + uuid);
			} catch (err) {
				console.error(err);
				toast.error("An error occured during username lookup");
				return;
			}

			try {
				const profile = await tournamentSystem.mojangApi.getProfile(uuid);
				if (!profile.found) {
					toast.error("Username seems to be valid but the profile was not found");
					return;
				}
				trueUsername = profile.data!.name;
				console.log(username + " has the username " + trueUsername);
			} catch (err) {
				console.error(err);
				toast.error("Failed to lookup profile from mojang api");
				return;
			}
		}

		const req = await tournamentSystem.api.addWhitelistUser(uuid, trueUsername, offline);
		if (req.success) {
			toast.success(trueUsername + " was added to the whitelist");
			setAddModalVisible(false);
			await tournamentSystem.updateState();
		} else {
			toast.error("Failed to whitelist user. " + req.message);
		}
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<WhitelistTable entries={state.whitelist} onAddButtonClicked={showAddModal} onClearButtonClicked={showClearModal} />
					</Col>
				</Row>
			</Container>

			<ConfirmModal onCancel={() => { setClearModalVisible(false) }} onConfirm={clear} title='Clear whitelist' visible={clearModalVisible} cancelButtonVariant='secondary' confirmButtonVariant='danger' confirmText='Clear whitelist'>
				<p>
					Please confirm that you want to clear the whitelist
				</p>
			</ConfirmModal>

			<Modal show={addModalVisible} onHide={closeAddModal}>
				<ModalHeader closeButton>
					<ModalTitle>Whitelist user</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Username</FormLabel>
								<FormControl type="text" onChange={handleUsernameChange} value={username} maxLength={16} placeholder="Username" />
							</Col>
						</Row>
						<Row className='mt-2'>
							<Col>
								<FormCheck type="switch" label="Offline mode" checked={offline} onChange={handleOfflineChange} />
							</Col>
						</Row>
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button className='mx-1' variant="secondary" onClick={closeAddModal}>Cancel</Button>
					<Button className='mx-1' variant="success" onClick={handleAddUser}>Add</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}
