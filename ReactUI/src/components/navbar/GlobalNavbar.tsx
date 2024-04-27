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
import TextPromptModal from '../modals/TextPromptModal'

/// @ts-ignore
import novaLogo256 from "../../assets/img/nova_logo_256.png";

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
		setShutdownPromptVisible(true);
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

	const [nameModalOpen, setNameModalOpen] = useState<boolean>(false);
	const [motdModalOpen, setMOTDModalOpen] = useState<boolean>(false);
	const [urlModalOpen, setUrlModalOpen] = useState<boolean>(false);


	function openSetTournamentName() {
		if (!tournamentSystem.authManager.hasPermission(Permission.MANAGE_SETTINGS)) {
			toast.error("You dont have permission to manage this setting");
			return;
		}
		setNameModalOpen(true);
	}

	function openSetMOTD() {
		if (!tournamentSystem.authManager.hasPermission(Permission.MANAGE_SETTINGS)) {
			toast.error("You dont have permission to manage this setting");
			return;
		}
		setMOTDModalOpen(true);
	}

	function openSetScoreboardURL() {
		if (!tournamentSystem.authManager.hasPermission(Permission.MANAGE_SETTINGS)) {
			toast.error("You dont have permission to manage this setting");
			return;
		}
		setUrlModalOpen(true);
	}

	async function onSetName(name: string) {
		const response = await tournamentSystem.api.setTournamentName(name);
		if (response.success) {
			toast.success("Tournament name set. You might have to restart for it to be applied");
			setNameModalOpen(false);
		} else {
			console.error("Failed to set tournament name. " + response.message);
			toast.error("Failed to set tournament name. " + response.message);
		}
	}

	async function onSetMOTD(motd: string) {
		const response = await tournamentSystem.api.setMOTD(motd);
		if (response.success) {
			toast.success("MOTD set");
			setMOTDModalOpen(false);
		} else {
			console.error("Failed to set tournament name. " + response.message);
			toast.error("Failed to set tournament name. " + response.message);
		}
	}

	async function onSetUrl(url: string) {
		const response = await tournamentSystem.api.setScoreboardURL(url);
		if (response.success) {
			toast.success("Scoreboard URL set. You might have to restart for it to be applied");
			setUrlModalOpen(false);
		} else {
			console.error("Failed to set scoreboard url. " + response.message);
			toast.error("Failed to set scoreboard url. " + response.message);
		}
	}

	async function reloadDynamicConfig() {
		const response = await tournamentSystem.api.reloadDynamicConfig();
		if (response.success) {
			if (response.data.success) {
				toast.success("Dynamic config reloaded");
			} else {
				console.error(response.data.message);
				toast.error(response.data.message);
			}
		} else {
			console.error("Failed to reload dynamic config. " + response.message);
			toast.error("Failed to reload dynamic config. " + response.message);
		}
	}

	return (
		<>
			<Navbar expand="lg" bg="dark" data-bs-theme="dark">
				<Container>
					<NavbarBrand as={Link} to='/' className='main-navbar-brand'>
						<img src={novaLogo256} width="36" height="36" className="d-inline-block align-top me-1" />
						TournamentSystem
					</NavbarBrand>
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
									<DropdownItemText>Settings</DropdownItemText>
									<DropdownItem onClick={openSetTournamentName}>Set tournament name</DropdownItem>
									<DropdownItem onClick={openSetMOTD}>Set MOTD</DropdownItem>
									<DropdownItem onClick={openSetScoreboardURL}>Set scoreboard url</DropdownItem>
									<DropdownDivider />
									<DropdownItemText>Account management</DropdownItemText>
									<DropdownItem as={Link} to="/accounts">Manage accounts</DropdownItem>
									<DropdownDivider />
									<DropdownItemText>Management</DropdownItemText>
									<DropdownItem onClick={reloadDynamicConfig}>Reload dynamic config</DropdownItem>
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

			<TextPromptModal onClose={() => { setNameModalOpen(false) }} initialValue={tournamentSystem.state.system.tournament_name} onSubmit={onSetName} title='Set tournament name' visible={nameModalOpen} cancelText='Cancel' cancelType='secondary' confirmType='primary' confirmText='Set name' placeholder='Tournament name'>
				<p>
					Enter the new tournament name
				</p>
			</TextPromptModal>

			<TextPromptModal onClose={() => { setMOTDModalOpen(false) }} initialValue={tournamentSystem.state.system.motd} onSubmit={onSetMOTD} title='Set MOTD' visible={motdModalOpen} cancelText='Cancel' cancelType='secondary' confirmType='primary' confirmText='Set MOTD' placeholder='MOTD'>
				<p>
					Enter the new MOTD
				</p>
			</TextPromptModal>

			<TextPromptModal onClose={() => { setUrlModalOpen(false) }} initialValue={tournamentSystem.state.system.scoreboard_url} onSubmit={onSetUrl} title='Set scoreboard url' visible={urlModalOpen} cancelText='Cancel' cancelType='secondary' confirmType='primary' confirmText='Set URL' placeholder='Scoreboard URL'>
				<p>
					Enter the new scoreboard url
				</p>
			</TextPromptModal>

			<ThemeSelector onClose={closeThemeSelector} visible={themeSelectorVisible} />
		</>
	)
}
