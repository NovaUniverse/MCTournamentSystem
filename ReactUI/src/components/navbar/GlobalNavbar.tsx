import React from 'react'
import { Container, Nav, NavItem, NavLink, Navbar, NavbarBrand, NavbarCollapse, NavbarToggle } from 'react-bootstrap'
import PHPMyAdminLink from './items/PHPMyAdminLink'
import { Link } from 'react-router-dom'

interface Props {
	loggedIn: boolean
}

export default function GlobalNavbar({ loggedIn }: Props) {
	return (
		<>
			<Navbar expand="lg" bg="dark" data-bs-theme="dark">
				<Container>
					<NavbarBrand as={Link} to='/'>TournamentSystem</NavbarBrand>
					<NavbarToggle aria-controls="basic-navbar-nav" />
					<NavbarCollapse id="basic-navbar-nav">
						<Nav>
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
		</>
	)
}
