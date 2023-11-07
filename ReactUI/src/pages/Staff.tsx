import React, { ChangeEvent, useEffect, useState } from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Button, Col, Container, FormCheck, FormControl, FormLabel, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap'
import StaffTable from '../components/tables/staff/StaffTable'
import StaffDTO, { createEmptyStaffDTO } from '../scripts/dto/StaffDTO';
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import toast from 'react-hot-toast';
import { Events } from '../scripts/enum/Events';
import Validation from '../scripts/utils/Validation';

export default function Staff() {
	const tournamentSystem = useTournamentSystemContext();

	const [staff, setStaff] = useState<StaffDTO>(createEmptyStaffDTO());

	useEffect(() => {
		const interval = setInterval(() => {
			update();
		}, 3000);

		const handleStaffUpdate = () => {
			update();
		}

		update();

		tournamentSystem.events.on(Events.STATE_UPDATE, handleStaffUpdate);

		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStaffUpdate);
			clearInterval(interval);
		}
	}, []);

	async function update() {
		try {
			const data = await tournamentSystem.api.getStaffList();

			data.staff.sort((a, b) => {
				return a.username.localeCompare(b.username);
			});

			data.staff_roles.sort((a, b) => {
				return a.localeCompare(b);
			});

			setStaff(data);
		} catch (err) {
			console.error("Failed to update staff list");
			console.error(err);
			toast.error("Failed to update staff list");
		}
	}

	// Add modal
	const [addModalVisible, setAddModalVisible] = useState<boolean>(false);
	const [username, setUsername] = useState<string>("");
	const [role, setRole] = useState<string>("");
	const [offline, setOffline] = useState<boolean>(false);

	useEffect(() => {
		if (addModalVisible) {
			setUsername("");
			setRole(staff.staff_roles.length > 0 ? staff.staff_roles[0] : "");
			setOffline(false);
		}
	}, [addModalVisible]);

	function showModal() {
		setAddModalVisible(true);
	}

	function closeModal() {
		setAddModalVisible(false);
	}

	async function addUser() {
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

		const req = await tournamentSystem.api.setUserStaffRole(uuid, trueUsername, role, offline);
		if (req.success) {
			toast.success(trueUsername + " added as " + role);
			closeModal();
			await update();
		} else {
			toast.error("Failed to add staff member. " + req.message);
		}
	}

	function handleUsernameChange(e: ChangeEvent<any>) {
		setUsername(e.target.value);
	}

	function handleRoleChange(e: ChangeEvent<any>) {
		setRole(e.target.value);
	}

	function handleOfflineChange(e: ChangeEvent<any>) {
		setOffline(e.target.checked);
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<StaffTable staff={staff} />
					</Col>
				</Row>
				<Row>
					<Col>
						<Button variant='primary' onClick={showModal}>Add user</Button>
					</Col>
				</Row>
			</Container>

			<Modal show={addModalVisible} onHide={closeModal}>
				<ModalHeader closeButton>
					<ModalTitle>Add staff member</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Username</FormLabel>
								<FormControl type='text' placeholder='Username' value={username} onChange={handleUsernameChange} maxLength={16} />
							</Col>
						</Row>

						<Row class="mt-2">
							<Col className='mt-2'>
								<FormLabel>Role</FormLabel>
								<FormSelect value={role} onChange={handleRoleChange}>
									{staff.staff_roles.map(role =>
										<option key={role} value={role}>{role}</option>
									)}
								</FormSelect>
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
					<Button variant="secondary" onClick={closeModal}>Cancel</Button>
					<Button variant="success" onClick={addUser}>Add</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}
