import React, { ChangeEvent, useEffect, useState } from 'react'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import axios from 'axios';
import PageSelection from '../components/nav/PageSelection';
import { Button, Col, Container, FormCheck, FormControl, FormLabel, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import AccountDTO from '../scripts/dto/AccountDTO';
import AccountsTable from '../components/tables/accounts/AccountsTable';
import { Events } from '../scripts/enum/Events';
import AccountPermissions from '../components/tables/accounts/AccountPermissions';
import toast from 'react-hot-toast';
import Validation from '../scripts/utils/Validation';

export default function Accounts() {
	const tournamentSystem = useTournamentSystemContext();

	const [accounts, setAccounts] = useState<AccountDTO[]>([]);

	const [newUserModalVisible, setNewUserModalVisible] = useState<boolean>(false);
	const [newUserUsername, setNewUserUsername] = useState<string>("");
	const [newUserPassword, setNewUserPassword] = useState<string>("");
	const [newUserHideIPs, setNewUserHideIPs] = useState<boolean>(false);
	const [newUserAllowManagerUsers, setNewUserAllowManagerUsers] = useState<boolean>(false);
	const [newUserPermissions, setNewUserPermissions] = useState<Map<string, boolean>>(new Map());

	useEffect(() => {
		const interval = setInterval(() => {
			fetchAccounts();
		}, 4000);

		const handleChange = () => {
			fetchAccounts();
		}

		tournamentSystem.events.on(Events.ACCOUNTS_CHANGED, handleChange);

		fetchAccounts();

		return () => {
			clearInterval(interval);
			tournamentSystem.events.off(Events.ACCOUNTS_CHANGED, handleChange);
		}
	}, []);

	async function fetchAccounts() {
		try {
			const result = await tournamentSystem.api.getAccounts();
			setAccounts(result);
		} catch (err) {
			console.error("Failed to fetch accounts");
			console.error(err);
		}
	}

	function handlePermissionChange(p: Map<string, boolean>) {
		setNewUserPermissions(p);
	}

	function handleHideIPsChange(e: ChangeEvent<any>) {
		setNewUserHideIPs(e.target.checked);
	}

	function handleManageUsersChange(e: ChangeEvent<any>) {
		setNewUserAllowManagerUsers(e.target.checked);
	}

	function handleUsernameChange(e: ChangeEvent<any>) {
		setNewUserUsername(e.target.value);
	}

	function handlePasswordChange(e: ChangeEvent<any>) {
		setNewUserPassword(e.target.value);
	}

	async function createUser() {
		const regex = /^[a-zA-Z0-9-_]+$/;

		const name = newUserUsername.trim();
		const password = newUserPassword;

		if (name.length == 0) {
			toast.error("Please enter a valid username");
			return;
		}

		if (!regex.test(name)) {
			toast.error("Please only use alphanumeric, dashes and underscores for usernames");
			return;
		}

		if (password.trim().length == 0) {
			toast.error("Please enter a password");
			return;
		}

		const permissionList: string[] = [];
		newUserPermissions.forEach((val, key) => {
			if (val == true) {
				permissionList.push(key);
			}
		});

		const req = await tournamentSystem.api.createUser(name, password, newUserHideIPs, newUserAllowManagerUsers, permissionList);
		if (req.success) {
			toast.success("Account created");
			setNewUserModalVisible(false);
			tournamentSystem.events.emit(Events.ACCOUNTS_CHANGED);
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<h2>Accounts</h2>
					</Col>
				</Row>
				<Row>
					<Col>
						<AccountsTable accounts={accounts} />
					</Col>
				</Row>
				<Row>
					<Col>
						<Button variant='primary' onClick={() => { setNewUserModalVisible(true) }} disabled={!tournamentSystem.authManager.hasEditUserPermission}>New user</Button>
					</Col>
				</Row>
			</Container>

			<Modal show={newUserModalVisible} onHide={() => { setNewUserModalVisible(false) }}>
				<ModalHeader closeButton>
					<ModalTitle>Create user</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Username</FormLabel>
								<FormControl value={newUserUsername} onChange={handleUsernameChange} placeholder='Username' />
							</Col>
						</Row>

						<Row className='mt-1'>
							<Col>
								<FormLabel>Password</FormLabel>
								<FormControl value={newUserPassword} onChange={handlePasswordChange} placeholder='Password' type="password" />
								<hr />
							</Col>
						</Row>

						<Row className='mt-2'>
							<Col>
								<p>IP Visibility settings</p>
								<FormCheck type="switch" label="Hide IPs" checked={newUserHideIPs} onChange={handleHideIPsChange} />
								<hr />
							</Col>
						</Row>

						<Row className='mt-2'>
							<Col>
								<p>Allow editing, creating and deleting users</p>
								<FormCheck type="switch" label="Allow manage users" checked={newUserAllowManagerUsers} onChange={handleManageUsersChange} />
								<hr />
							</Col>
						</Row>

						<Row className='mt-2'>
							<Col>
								<p>Permissions</p>
							</Col>
						</Row>

						<AccountPermissions handlePermissions={handlePermissionChange} />
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="secondary" onClick={() => { setNewUserModalVisible(false) }}>Cancel</Button>
					<Button variant="primary" onClick={createUser}>Create user</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}
