import React, { ChangeEvent, useEffect, useState } from 'react'
import { Button, Col, Container, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import { Theme } from '../../scripts/enum/Theme';

interface Props {
	visible: boolean;
	onClose: () => void;
}

export default function ThemeSelector({ visible, onClose }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [selectedTheme, setSelectedTheme] = useState<Theme>(tournamentSystem.activeTheme);

	useEffect(() => {
		if (visible) {
			setSelectedTheme(tournamentSystem.activeTheme);
		}
	}, [visible]);

	function handleThemeChange(e: ChangeEvent<any>) {
		setSelectedTheme(e.target.value);
		tournamentSystem.setTheme(e.target.value as Theme, true);
	}

	return (
		<Modal show={visible} onHide={onClose}>
			<ModalHeader closeButton>
				<ModalTitle>Select theme</ModalTitle>
			</ModalHeader>

			<ModalBody>
				<Container fluid>
					<Row>
						<Col>
							<FormSelect onChange={handleThemeChange} value={selectedTheme}>
								{Object.values(Theme).map((t) => <option key={t} value={t}>{t}</option>)}
							</FormSelect>
						</Col>
					</Row>
				</Container>
			</ModalBody>
			<ModalFooter>
				<Button variant="secondary" onClick={onClose}>Close</Button>
			</ModalFooter>
		</Modal>
	)
}
