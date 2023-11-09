import React from 'react'
import { Nav } from 'react-bootstrap'
import PageNavLink from './PageNavLink'

export default function PageSelection() {
	return (
		<Nav className='my-1 mx-2' variant='tabs'>
			<PageNavLink url="/" text="Overview" />
			<PageNavLink url="/score" text="Score" />
			<PageNavLink url="/score_snapshot" text="Score Snapshot" />
			<PageNavLink url="/triggers" text="Triggers" />
			<PageNavLink url="/servers" text="Servers" />
			<PageNavLink url="/whitelist" text="Whitelist" />
			<PageNavLink url="/staff" text="Staff" />
			<PageNavLink url="/maps" text="Maps" />
			<PageNavLink url="/chat" text="Chat" />
		</Nav>
	)
}
