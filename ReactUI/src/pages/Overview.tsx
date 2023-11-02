import React from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import PlayerTable from '../components/tables/playertable/PlayerTable'
import SendAllButton from '../components/buttons/SendAllButton'
import StartGameButton from '../components/buttons/StartGameButton'

export default function Overview() {
	return (
		<Container fluid>
			<Row>
				<Col>
					<PlayerTable />
				</Col>
			</Row>

			<Row>
				<Col>
					<SendAllButton className='me-2 mt-2'/>
					<StartGameButton className='me-2 mt-2'/>
				</Col>
			</Row>
		</Container>
	)
}
