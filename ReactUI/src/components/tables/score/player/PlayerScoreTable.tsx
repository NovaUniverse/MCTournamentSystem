import React, { ChangeEvent, useEffect, useState } from 'react'
import ScoreDTO from '../../../../scripts/dto/ScoreDTO'
import { Button, Col, Container, FormControl, FormLabel, FormSelect, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row, Table } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../../context/TournamentSystemContext';
import { Permission } from '../../../../scripts/enum/Permission';
import PlayerScoreTableEntry from './PlayerScoreTableEntry';
import StateDTO from '../../../../scripts/dto/StateDTO';
import { Events } from '../../../../scripts/enum/Events';
import toast from 'react-hot-toast';
import { ScoreEntryType } from '../../../../scripts/enum/ScoreEntryType';
import ScrollOnXOverflow from '../../../ScrollOnXOverflow';

interface Props {
	score: ScoreDTO;
}

export default function PlayerScoreTable({ score }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [state, setState] = useState<StateDTO>(tournamentSystem.state);
	const [modalVisible, setModalVisible] = useState<boolean>(false);
	const [selectedPlayer, setSelectedPlayer] = useState<number>(tournamentSystem.state.players.length > 0 ? tournamentSystem.state.players[0].id : 0);
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
			setSelectedPlayer(state.players.length > 0 ? state.players[0].id : 0);
			setReason("");
		}
	}, [modalVisible]);

	function onClose() {
		setModalVisible(false);
	}

	async function onAdd() {
		if (isNaN(Number(selectedPlayer))) {
			toast.error("Invalid player selected");
			return;
		}

		if (isNaN(Number(amount))) {
			toast.error("Invalid amount selected");
			return;
		}

		const req = await tournamentSystem.api.addScore(ScoreEntryType.PLAYER, selectedPlayer, reason, amount);
		if (req.success) {
			toast.success("Score added");
			setModalVisible(false);
		} else {
			toast.error("" + req.message);
		}
	}

	function handlePlayerChange(e: ChangeEvent<any>) {
		setSelectedPlayer(e.target.value);
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
							<th>Username</th>
							<th>Server</th>
							<th>Reason</th>
							<th>Amount</th>
							<th>Gained at</th>
							<th className='t-fit'></th>
						</tr>
					</thead>

					<tbody>
						{score.players.map(s => <PlayerScoreTableEntry score={s} key={String(s.id)} />)}
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
					<ModalTitle>Add player score</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Player</FormLabel>
								<FormSelect onChange={handlePlayerChange} value={selectedPlayer}>
									{state.players.map(p => <option key={String(p.id)} value={p.id}>{p.username}</option>)}
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
