import React from 'react'
import { Container, Nav, NavItem, NavLink, Navbar, NavbarBrand, NavbarCollapse, NavbarToggle } from 'react-bootstrap'
import PHPMyAdminLink from './items/PHPMyAdminLink'

interface Props {
	loggedIn: boolean
}

export default function GlobalNavbar({ loggedIn }: Props) {
	return (
		<>
			<Navbar expand="lg" bg="dark" data-bs-theme="dark">
				<Container>
					<NavbarBrand>TournamentSystem</NavbarBrand>
					<NavbarToggle aria-controls="basic-navbar-nav" />
					<NavbarCollapse id="basic-navbar-nav">
						<Nav>
							<NavItem>
								<NavLink href="/plan">Analytics</NavLink>
							</NavItem>
							<NavItem>
								<PHPMyAdminLink useNavLink />
							</NavItem>
						</Nav>
					</NavbarCollapse>
				</Container>
			</Navbar>
		</>
	)
}
