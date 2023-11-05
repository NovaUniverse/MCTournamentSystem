import React from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import PlayerTable from '../components/tables/playertable/PlayerTable'
import SendAllButton from '../components/buttons/SendAllButton'
import StartGameButton from '../components/buttons/StartGameButton'
import ExportSummaryButton from '../components/buttons/ExportSummaryButton'
import SystemStats from '../components/SystemStats'

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
					<SendAllButton className='me-2 mt-2' />
					<StartGameButton className='me-2 mt-2' />
					<ExportSummaryButton className='me-2 mt-2' />
				</Col>
			</Row>

			<Row>
				<Col md={6} sm={12} xs={12}>
					<h5>System stats</h5>
					<SystemStats/>
				</Col>
			</Row>
		</Container>
	)
}
