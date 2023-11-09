import React from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Col, Container, Row } from 'react-bootstrap'
import GameTriggerCardsList from '../components/cards/triggers/GameTriggerCardsList'

export default function Triggers() {
	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<h4>Game triggers</h4>
						<p>This page allows for triggering in game events. Triggers will show up as soon as the game starts</p>
					</Col>
				</Row>

				<Row>
					<GameTriggerCardsList />
				</Row>
			</Container>
		</>
	)
}
