import React, { ChangeEvent, useEffect, useState } from 'react'
import AccountDTO from '../../../scripts/dto/AccountDTO'
import { Badge, Button, Col, Container, FormCheck, FormControl, FormLabel, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import ConfirmModal from '../../modals/ConfirmModal';
import toast from 'react-hot-toast';
import { Events } from '../../../scripts/enum/Events';
import AccountPermissions from './AccountPermissions';

interface Props {
	account: AccountDTO;
}

export default function AccountsTableEntry({ account }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [deleteVisible, setDeleteVisble] = useState<boolean>(false);
	const [editVisible, setEditVisible] = useState<boolean>(false);
	const [changePasswordVisible, setChangePasswordVisible] = useState<boolean>(false);

	const [hideIPs, setHideIPs] = useState<boolean>(account.hide_ips);
	const [permissions, setPermissions] = useState<Map<string, boolean>>(new Map<string, boolean>());

	const [password, setPassword] = useState<string>("");
	const [confirmPassword, setConfirmPassword] = useState<string>("");

	useEffect(() => {
		if (changePasswordVisible) {
			setPassword("");
			setConfirmPassword("");
		}
	}, [changePasswordVisible]);

	async function deleteUser() {
		const req = await tournamentSystem.api.deleteAccount(account.username);
		if (req.success) {
			toast.success("Account deleted");
			setDeleteVisble(false);
			tournamentSystem.events.emit(Events.ACCOUNTS_CHANGED);
		} else {
			toast.error("" + req.message);
		}
	}

	function handleHideIPsChange(e: ChangeEvent<any>) {
		setHideIPs(e.target.checked);
	}

	function handlePermissionChange(p: Map<string, boolean>) {
		setPermissions(p);
	}

	async function saveUser() {
		const permissionList: string[] = [];
		permissions.forEach((val, key) => {
			if (val == true) {
				permissionList.push(key);
			}
		});

		const req = await tournamentSystem.api.editAccounts(account.username, permissionList, hideIPs);
		if (req.success) {
			toast.success("Account updated");
			setEditVisible(false);
			tournamentSystem.events.emit(Events.ACCOUNTS_CHANGED);
		} else {
			toast.error("" + req.message);
		}
	}

	function onPasswordInputChange(e: ChangeEvent<any>) {
		setPassword(e.target.value);
	}

	function onConfimePasswordInputChange(e: ChangeEvent<any>) {
		setConfirmPassword(e.target.value);
	}

	async function changePassword() {
		if (password != confirmPassword) {
			toast.error("Passwords not matching");
			return;
		}

		if (password.trim().length == 0) {
			toast.error("Password cant be empty");
			return;
		}

		const req = await tournamentSystem.api.changePassword(account.username, password);
		if (req.success) {
			toast.success("Password changed");
			setChangePasswordVisible(false);
			if (tournamentSystem.authManager.username!.toLocaleLowerCase() == account.username.toLocaleLowerCase()) {
				setTimeout(() => {
					window.location.reload();
				}, 2000);
			}
		} else {
			toast.error("" + req.message);
		}
	}

	function canChangePassword() {
		return tournamentSystem.authManager.hasEditUserPermission || tournamentSystem.authManager.username!.toLocaleLowerCase() == account.username.toLocaleLowerCase();
	}

	function handlePasswordKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
		if (e.key === 'Enter') {
			changePassword();
		}
	}

	return (
		<>
			<tr>
				<td>{account.username}</td>
				<td>
					{account.permissions.map(p => <Badge bg='primary' className='mx-1' key={p}>{p}</Badge>)}
				</td>
				<td>{account.hide_ips ? "Yes" : "No"}</td>
				<td>{account.allow_manage_users ? "Yes" : "No"}</td>
				<td>
					<Button variant='info' className='text-nowrap' onClick={() => { setChangePasswordVisible(true) }} disabled={!canChangePassword()}>Change password</Button>
				</td>
				<td>
					<Button variant='primary' onClick={() => { setEditVisible(true) }} disabled={!tournamentSystem.authManager.hasEditUserPermission}>Edit</Button>
				</td>
				<td>
					<Button variant='danger' onClick={() => { setDeleteVisble(true) }} disabled={!tournamentSystem.authManager.hasEditUserPermission}>Remove</Button>
				</td>
			</tr>

			<ConfirmModal onCancel={() => { setDeleteVisble(false) }} title='Delete user' onConfirm={deleteUser} visible={deleteVisible} cancelButtonVariant='secondary' cancelText='Cancel' confirmButtonVariant='danger' confirmText='Delete'>
				<p>Confirm that you want to delete the user {account.username}</p>
			</ConfirmModal>

			<Modal show={editVisible} onHide={() => { setEditVisible(false) }}>
				<ModalHeader closeButton>
					<ModalTitle>Edit user {account.username}</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row className='mt-2'>
							<Col>
								<p>IP Visibility settings</p>
								<FormCheck type="switch" label="Hide IPs" checked={hideIPs} onChange={handleHideIPsChange} />
								<hr />
							</Col>
						</Row>

						<Row className='mt-2'>
							<Col>
								<p>Permissions</p>
							</Col>
						</Row>

						<AccountPermissions account={account} handlePermissions={handlePermissionChange} />
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="secondary" onClick={() => { setEditVisible(false) }}>Cancel</Button>
					<Button variant="primary" onClick={saveUser}>Save</Button>
				</ModalFooter>
			</Modal>

			<Modal show={changePasswordVisible} onHide={() => { setChangePasswordVisible(false) }}>
				<ModalHeader closeButton>
					<ModalTitle>Change password for {account.username}</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Password</FormLabel>
								<FormControl value={password} onChange={onPasswordInputChange} onKeyDown={handlePasswordKeyDown} placeholder='Password' type='password' />
							</Col>
						</Row>

						<Row className='mt-2'>
							<Col>
								<FormLabel>Confirm password</FormLabel>
								<FormControl value={confirmPassword} onChange={onConfimePasswordInputChange} onKeyDown={handlePasswordKeyDown} placeholder='Confirm password' type='password' />
							</Col>
						</Row>
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="secondary" onClick={() => { setChangePasswordVisible(false) }}>Cancel</Button>
					<Button variant="primary" onClick={changePassword}>Change password</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}
