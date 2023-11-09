import React from 'react'
import { NavItem, NavLink } from 'react-bootstrap';
import { Link, useLocation } from 'react-router-dom';

interface Props {
	url: string;
	text: string;
}

export default function PageNavLink({url, text}: Props) {
	const location = useLocation();

	return (
		<NavItem>
			<NavLink active={location.pathname == url} as={Link} to={url}>{text}</NavLink>
		</NavItem>
	)
}
