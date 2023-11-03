import React from 'react'
import { Nav } from 'react-bootstrap'
import PageNavLink from './PageNavLink'

export default function PageSelection() {
	return (
		<Nav className='my-1' variant='tabs'>
			<PageNavLink url="/" text="Overview"/>
			<PageNavLink url="/servers" text="Servers"/>
		</Nav>
	)
}
