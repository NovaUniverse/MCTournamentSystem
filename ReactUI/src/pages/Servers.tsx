import React from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import ServerCardsList from '../components/cards/servers/ServerCardsList'

export default function Servers() {
	return (
		<>
			<Container fluid>
				<Row>
					<ServerCardsList />
				</Row>
			</Container>
		</>
	)
}
