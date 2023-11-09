import React from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import PlayerTable from '../components/tables/playertable/PlayerTable'
import SendAllButton from '../components/buttons/SendAllButton'
import StartGameButton from '../components/buttons/StartGameButton'
import ExportSummaryButton from '../components/buttons/ExportSummaryButton'
import SystemStats from '../components/SystemStats'
import BroadcastMessageButton from '../components/buttons/BroadcastMessageButton'
import ClickableNextMinigameText from '../components/text/ClickableNextMinigameText'
import PageSelection from '../components/nav/PageSelection'

export default function Overview() {
	return (
		<>
			<PageSelection />
			
			<Container fluid>
				<Row className='mt-2'>
					<Col>
						<h4>
							<ClickableNextMinigameText />
						</h4>
					</Col>
				</Row>

				<Row className='mt-2'>
					<Col>
						<PlayerTable />
					</Col>
				</Row>

				<Row className='mt-2'>
					<Col>
						<BroadcastMessageButton className='me-2 mt-2' />
						<SendAllButton className='me-2 mt-2' />
						<StartGameButton className='me-2 mt-2' />
						<ExportSummaryButton className='me-2 mt-2' />
					</Col>
				</Row>

				<Row className='mt-2'>
					<Col md={6} sm={12} xs={12}>
						<h5>System stats</h5>
						<SystemStats />
					</Col>
				</Row>
			</Container>
		</>
	)
}
