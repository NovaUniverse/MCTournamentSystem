import React from 'react'
import { Button, Col, Container, Row } from 'react-bootstrap'
import { useLocation, useNavigate } from 'react-router-dom'

export default function NotFound() {
	const location = useLocation();

	function goHome() {
		window.location.href = "/";
	}

	return (
		<Container fluid>
			<Row>
				<Col>
					<h1>404: Page not found</h1>
					<p>
						The page at <code>{location.pathname}</code> could not be found. Check that you spelled the url correctly
					</p>
					<Button variant='primary' onClick={goHome}>Back to main page</Button>
				</Col>
			</Row>
		</Container>
	)
}
