import React from 'react'
import { Col, Container, Row, Table } from 'react-bootstrap'
import PlayerList from '../components/tables/playertable/PlayerList'
import PlayerTable from '../components/tables/playertable/PlayerTable'

export default function Overview() {
	return (
		<Container fluid>
			<Row>
				<Col>
					<PlayerTable/>
				</Col>
			</Row>
		</Container>
	)
}
