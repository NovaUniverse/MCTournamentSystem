import React, { ChangeEvent, useEffect, useState } from 'react'
import { Button, Col, Container, FormCheck, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import { Theme } from '../../scripts/enum/Theme';
import CSSMod from '../../scripts/CSSMod';
import { Events } from '../../scripts/enum/Events';

interface Props {
	visible: boolean;
	onClose: () => void;
}

export default function ThemeSelector({ visible, onClose }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [selectedTheme, setSelectedTheme] = useState<Theme>(tournamentSystem.activeTheme);
	const [availableCssMods, setAvailableCssMods] = useState<CSSMod[]>(tournamentSystem.cssMods);
	const [activeCssMods, setActiveCssMods] = useState<string[]>(tournamentSystem.activeCSSMods);

	useEffect(() => {
		if (visible) {
			setSelectedTheme(tournamentSystem.activeTheme);
		}
	}, [visible]);


	useEffect(() => {
		const handleCssModsChanged = (e: any) => {
			setAvailableCssMods(tournamentSystem.cssMods);
			setActiveCssMods(tournamentSystem.activeCSSMods);
		}

		tournamentSystem.events.on(Events.CSS_MODS_CHANGED, handleCssModsChanged);
		return () => {
			tournamentSystem.events.off(Events.CSS_MODS_CHANGED, handleCssModsChanged);
		}
	});

	function handleThemeChange(e: ChangeEvent<any>) {
		setSelectedTheme(e.target.value);
		tournamentSystem.setTheme(e.target.value as Theme, true);
	}

	function handleModChange(e: ChangeEvent<any>) {
		const enabled = e.target.checked as boolean;
		const name = e.target.dataset.name as string;
		tournamentSystem.setCSSModActive(name, enabled);
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

					<Row>
						<Col>
							<hr />
							<h5>CSS Mods</h5>
							<p>These mods allows for customising the ui</p>
						</Col>
					</Row>

					{availableCssMods.map(m =>
						<Row key={m.name}>
							<Col>
								<FormCheck type="switch" label={m.name} checked={activeCssMods.includes(m.name)} data-name={m.name} onChange={handleModChange} />
							</Col>
						</Row>
					)}
				</Container>
			</ModalBody>
			<ModalFooter>
				<Button variant="secondary" onClick={onClose}>Close</Button>
			</ModalFooter>
		</Modal>
	)
}
