import React, { useState } from 'react'
import { Container, Nav, NavItem, NavLink, Navbar, NavbarBrand, NavbarCollapse, NavbarToggle } from 'react-bootstrap'
import PHPMyAdminLink from './items/PHPMyAdminLink'
import { Link } from 'react-router-dom'
import ThemeSelector from '../modals/ThemeSelectorModal'

interface Props {
	loggedIn: boolean
}

export default function GlobalNavbar({ loggedIn }: Props) {
	const [themeSelectorVisible, setThemeSelectorVisible] = useState<boolean>(false);

	function openThemeSelector() {
		setThemeSelectorVisible(true);
	}

	function closeThemeSelector() {
		setThemeSelectorVisible(false);
	}

	return (
		<>
			<Navbar expand="lg" bg="dark" data-bs-theme="dark">
				<Container>
					<NavbarBrand as={Link} to='/'>TournamentSystem</NavbarBrand>
					<NavbarToggle aria-controls="basic-navbar-nav" />
					<NavbarCollapse id="basic-navbar-nav">
						<Nav>
							<NavItem>
								<NavLink onClick={openThemeSelector}>Theme</NavLink>
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
							</>}
						</Nav>
					</NavbarCollapse>
				</Container>
			</Navbar>

			<ThemeSelector onClose={closeThemeSelector} visible={themeSelectorVisible} />
		</>
	)
}
