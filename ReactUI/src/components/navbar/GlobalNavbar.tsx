import React, { useState } from 'react'
import { Container, DropdownDivider, DropdownItem, DropdownItemText, Nav, NavDropdown, NavItem, NavLink, Navbar, NavbarBrand, NavbarCollapse, NavbarToggle } from 'react-bootstrap'
import PHPMyAdminLink from './items/PHPMyAdminLink'
import { Link } from 'react-router-dom'
import ThemeSelector from '../modals/ThemeSelectorModal'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext'
import { Permission } from '../../scripts/enum/Permission'
import toast from 'react-hot-toast'
import ConfirmModal from '../modals/ConfirmModal'
import { LocalStorageKeys } from '../../scripts/enum/LocalStorageKeys'

interface Props {
	loggedIn: boolean
}

export default function GlobalNavbar({ loggedIn }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [themeSelectorVisible, setThemeSelectorVisible] = useState<boolean>(false);

	function openThemeSelector() {
		setThemeSelectorVisible(true);
	}

	function closeThemeSelector() {
		setThemeSelectorVisible(false);
	}

	function logout() {
		window.localStorage.removeItem(LocalStorageKeys.TOKEN);
		window.location.reload();
	}

	const [shutdownPromptVisible, setShutdownPromptVisible] = useState<boolean>(false);
	function openShutdownPropmpt() {
		if (!tournamentSystem.authManager.hasPermission(Permission.SHUTDOWN)) {
			toast.error("You dont have permission to do that");
			return;
		}
		setResetPromptVisible(true);
	}

	async function shutdown() {
		const req = await tournamentSystem.api.shutdown();
		if (req.success) {
			toast.success("Shutting down");
			setShutdownPromptVisible(false);
		} else {
			toast.error("" + req.message);
		}
	}

	const [shutdownResetVisible, setResetPromptVisible] = useState<boolean>(false);
	function openResetPropmpt() {
		if (!tournamentSystem.authManager.hasPermission(Permission.CLEAR_DATA)) {
			toast.error("You dont have permission to do that");
			return;
		}
		setResetPromptVisible(true);
	}

	async function reset() {
		const req = await tournamentSystem.api.reset();
		if (req.success) {
			toast.success("Data cleared");
			setResetPromptVisible(false);
		} else {
			toast.error("" + req.message);
		}
	}

	return (
		<>
			<Navbar expand="lg" bg="dark" data-bs-theme="dark">
				<Container>
					<NavbarBrand as={Link} to='/' className='main-navbar-brand'>TournamentSystem</NavbarBrand>
					<NavbarToggle aria-controls="basic-navbar-nav" />
					<NavbarCollapse id="basic-navbar-nav">
						<Nav>
							<NavItem>
								<NavLink onClick={openThemeSelector} className='main-navbar-set-theme'>Theme</NavLink>
							</NavItem>
							<NavItem>
								<NavLink href="/plan">Analytics</NavLink>
							</NavItem>
							<NavItem>
								<PHPMyAdminLink useNavLink />
							</NavItem>

							{loggedIn && <>
								<NavItem>
									<NavLink as={Link} to="/editor">Editor</NavLink>
								</NavItem>
								<NavDropdown title="System" id="basic-nav-dropdown">
									<DropdownDivider />
									<DropdownItem as={Link} to="/accounts">Manage accounts</DropdownItem>
									<DropdownDivider />
									<DropdownItem onClick={openResetPropmpt} className='text-danger'>Reset</DropdownItem>
									<DropdownItem onClick={openShutdownPropmpt} className='text-danger'>Shutdown</DropdownItem>
								</NavDropdown>

								<NavDropdown title="Account" id="basic-nav-dropdown">
									<DropdownItemText>
										Logged in as: <span className='text-info'>{tournamentSystem.authManager.username}</span>
									</DropdownItemText>
									<DropdownDivider />
									<DropdownItem onClick={logout} className='text-danger'>Logout</DropdownItem>
								</NavDropdown>
							</>}
						</Nav>
					</NavbarCollapse>
				</Container>
			</Navbar>

			<ConfirmModal onCancel={() => { setShutdownPromptVisible(false) }} onConfirm={shutdown} title='Confirm shutdown' visible={shutdownPromptVisible} cancelButtonVariant='secondary' confirmButtonVariant='danger' cancelText='Cancel' confirmText='Shutdown'>
				<p>
					Please confirm that you want to shutdown the tournament system
				</p>
			</ConfirmModal>

			<ConfirmModal onCancel={() => { setResetPromptVisible(false) }} onConfirm={reset} title='Confirm reset' visible={shutdownResetVisible} cancelButtonVariant='secondary' confirmButtonVariant='danger' cancelText='Cancel' confirmText='Reset'>
				<p>
					Please confirm that you want to reset all player data
				</p>
			</ConfirmModal>

			<ThemeSelector onClose={closeThemeSelector} visible={themeSelectorVisible} />
		</>
	)
}
