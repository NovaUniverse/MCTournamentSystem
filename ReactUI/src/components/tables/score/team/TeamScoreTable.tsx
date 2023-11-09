import React, { ChangeEvent, useEffect, useState } from 'react'
import ScoreDTO from '../../../../scripts/dto/ScoreDTO'
import { Button, Col, Container, FormControl, FormLabel, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row, Table } from 'react-bootstrap';
import { Permission } from '../../../../scripts/enum/Permission';
import { useTournamentSystemContext } from '../../../../context/TournamentSystemContext';
import TeamScoreTableEntry from './TeamScoreTableEntry';
import StateDTO from '../../../../scripts/dto/StateDTO';
import { Events } from '../../../../scripts/enum/Events';
import toast from 'react-hot-toast';
import { ScoreEntryType } from '../../../../scripts/enum/ScoreEntryType';
import ScrollOnXOverflow from '../../../ScrollOnXOverflow';

interface Props {
	score: ScoreDTO;
}

export default function TeamScoreTable({ score }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [state, setState] = useState<StateDTO>(tournamentSystem.state);
	const [modalVisible, setModalVisible] = useState<boolean>(false);
	const [selectedTeam, setSelectedTeam] = useState<number>(tournamentSystem.state.teams.length > 0 ? tournamentSystem.state.teams[0].id : 0);
	const [amount, setAmount] = useState<number>(0);
	const [reason, setReason] = useState<string>("");

	useEffect(() => {
		const handleStateUpdate = (state: StateDTO) => {
			setState(state);
		}
		tournamentSystem.events.on(Events.STATE_UPDATE, handleStateUpdate);
		return () => {
			tournamentSystem.events.off(Events.STATE_UPDATE, handleStateUpdate);
		};
	}, []);

	useEffect(() => {
		if (modalVisible) {
			setAmount(0);
			setSelectedTeam(state.teams.length > 0 ? state.teams[0].id : 0);
			setReason("");
		}
	}, [modalVisible]);

	function onClose() {
		setModalVisible(false);
	}

	async function onAdd() {
		if (isNaN(Number(selectedTeam))) {
			toast.error("Invalid team selected");
			return;
		}

		if (isNaN(Number(amount))) {
			toast.error("Invalid amount selected");
			return;
		}

		const req = await tournamentSystem.api.addScore(ScoreEntryType.TEAM, selectedTeam, reason, amount);
		if (req.success) {
			toast.success("Score added");
			setModalVisible(false);
		} else {
			toast.error("" + req.message);
		}
	}

	function handleTeamChange(e: ChangeEvent<any>) {
		setSelectedTeam(e.target.value);
	}

	function handleAmountChange(e: ChangeEvent<any>) {
		setAmount(e.target.value);
	}

	function handleReasonChange(e: ChangeEvent<any>) {
		setReason(e.target.value);
	}

	return (
		<>
			<ScrollOnXOverflow>
				<Table striped bordered hover>
					<thead>
						<tr>
							<th className='t-fit'>ID</th>
							<th>Team number</th>
							<th>Server</th>
							<th>Reason</th>
							<th>Amount</th>
							<th>Gained at</th>
							<th className='t-fit'></th>
						</tr>
					</thead>

					<tbody>
						{score.teams.map(s => <TeamScoreTableEntry score={s} key={String(s.id)} />)}
					</tbody>

					<tbody>
						<tr>
							<td colSpan={6}></td>
							<td>
								<Button variant='success' disabled={!tournamentSystem.authManager.hasPermission(Permission.ALTER_SCORE)} onClick={() => { setModalVisible(true) }}>Add</Button>
							</td>
						</tr>
					</tbody>
				</Table>
			</ScrollOnXOverflow>

			<Modal show={modalVisible} onHide={onClose}>
				<ModalHeader closeButton>
					<ModalTitle>Add team score</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Team</FormLabel>
								<FormSelect onChange={handleTeamChange} value={selectedTeam}>
									{state.teams.map(t => <option key={String(t.id)} value={t.id}>{t.display_name}</option>)}
								</FormSelect>
							</Col>
						</Row>

						<Row>
							<Col>
								<FormLabel>Amount</FormLabel>
								<FormControl type='number' value={amount} onChange={handleAmountChange} />
							</Col>
						</Row>

						<Row>
							<Col>
								<FormLabel>Reason</FormLabel>
								<FormControl type='text' value={reason} onChange={handleReasonChange} placeholder='Reason' />
							</Col>
						</Row>
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button className='mx-1' variant="secondary" onClick={onClose}>Cancel</Button>
					<Button className='mx-1' variant="primary" onClick={onAdd}>Add</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}
