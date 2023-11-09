import React from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import ServerCardsList from '../components/cards/servers/ServerCardsList'
import PageSelection from '../components/nav/PageSelection'

export default function Servers() {
	return (
		<>
			<PageSelection />
			
			<Container fluid>
				<Row>
					<ServerCardsList />
				</Row>
			</Container>
		</>
	)
}
