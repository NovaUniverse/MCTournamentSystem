import React, { ChangeEvent, useEffect, useRef, useState } from 'react'
import { useTeamEditorContext } from '../../../context/TeamEditorContext'
import { Button, Col, Container, FormControl, FormLabel, FormSelect, InputGroup, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import toast from 'react-hot-toast';
import Validation from '../../../scripts/utils/Validation';
import TournamentSystem from '../../../scripts/TournamentSystem';
import PlayerHead from '../../PlayerHead';

interface Props {
	className?: string;
	disabled?: boolean;
}

interface NewPlayerInfo {
	uuid: string;
	username: string;
}

export default function AddPlayerButton({ className, disabled = false }: Props) {
	const teamEditor = useTeamEditorContext();

	const addButtonRef = useRef<any>();
	const usernameField = useRef<any>();
	const teamSelectorRef = useRef<any>();

	const [modalVisible, setModalVisible] = useState<boolean>(false);
	const [player, setPlayer] = useState<NewPlayerInfo | null>(null);
	const [selectedTeam, setSelectedTeam] = useState<string>("1");
	const [username, setUsername] = useState<string>("");

	useEffect(() => {
		if (modalVisible) {
			setPlayer(null);
			setUsername("");
			usernameField.current.focus();
		}
	}, [modalVisible]);

	function handleUsernameChange(e: ChangeEvent<any>) {
		setUsername(e.target.value);
	}

	function handleUsernameKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
		if (e.key === 'Enter') {
			searchUser();
		}
	}

	async function searchUser() {
		let realName = username;
		let uuid: string | null = null;

		if (realName.trim().length == 0) {
			toast.error("Please enter a username");
			return;
		}

		if (teamEditor.offlineMode) {
			try {
				const lookup = await teamEditor.tournamentSystem.api.usernameToOfflineUserUUID(username);
				uuid = lookup.uuid;
			} catch (err) {
				console.error("Offline name to uuid lookup failed");
				console.error(err);
				toast.error("Failed to lookup uuid");
				return;
			}
		} else {
			if (!Validation.validateMinecraftUsername(username)) {
				toast.error("Please enter a valid username");
				return;
			}

			try {
				const uuidLookup = await teamEditor.tournamentSystem.mojangApi.usernameToUUID(username);
				if (!uuidLookup.found) {
					toast.error("Could nof find user named " + username);
					return;
				}
				uuid = uuidLookup.uuid!;
			} catch (err) {
				console.error("UUID lookup failed");
				console.error(err);
				toast.error("An error occured while fetching uuid from mojang");
				return;
			}

			try {
				const profileLookup = await teamEditor.tournamentSystem.mojangApi.getProfile(uuid);
				if (!profileLookup.found) {
					toast.error("Failed to fetch profile data");
					return;
				}
				realName = profileLookup.data!.name;
			} catch (err) {
				console.error("Profile lookup failed");
				console.error(err);
				toast.error("An error occured while fetching profile info from mojang");
				return;
			}
		}

		setPlayer({
			username: realName,
			uuid: uuid!
		});
	}

	function handleTeamChange(e: ChangeEvent<any>) {
		setSelectedTeam(e.target.value);
	}

	function handleTeamSelectKeyDown(e: React.KeyboardEvent<HTMLSelectElement>) {
		if (e.key === 'a') {
			add();
		}
	}

	function add() {
		if (player == null) {
			console.warn("add() called while player was null");
			return;
		}

		const players = teamEditor.players;
		const existing = players.find(p => p.uuid == player.uuid);
		if (existing != null) {
			existing.team_number = parseInt(selectedTeam);
			teamEditor.players = players.map((x) => x);
			toast.success("Changed the team number of " + player!.username + " to " + selectedTeam);
			setModalVisible(false);
			return;
		}

		const newPlayers = players;
		newPlayers.push({
			team_number: parseInt(selectedTeam),
			username: player!.username,
			uuid: player!.uuid,
			metadata: {}
		});
		teamEditor.players = newPlayers.map((x) => x);
		toast.success(player!.username + " added to team " + selectedTeam);
		setModalVisible(false);
	}

	return (
		<>
			<Button autoFocus onClick={() => { setModalVisible(true) }} variant='primary' className={className} ref={addButtonRef} disabled={disabled}>Add player</Button>

			<Modal show={modalVisible} onHide={() => setModalVisible(false)}>
				<ModalHeader closeButton>
					<ModalTitle>Add player</ModalTitle>
				</ModalHeader>

				<ModalBody>
					<Container fluid>
						<Row>
							<Col>
								<FormLabel>Search player</FormLabel>
								<InputGroup >
									<FormControl type='text' maxLength={16} placeholder='Username' value={username} onChange={handleUsernameChange} onKeyDown={handleUsernameKeyDown} ref={usernameField}></FormControl>
									<Button onClick={searchUser} variant='info'>Search</Button>
								</InputGroup >
							</Col>
						</Row>

						{player != null &&
							<>
								<Row className="mt-2">
									{!teamEditor.offlineMode &&
										<Col>
											<PlayerHead uuid={player.uuid} width={64} />
										</Col>
									}
									<Col>
										{player.uuid}
									</Col>
									<Col>
										{player.username}
									</Col>
								</Row>
								<Row className="mt-2">
									<Col>
										<FormLabel>Select team</FormLabel>
										<FormSelect autoFocus ref={teamSelectorRef} value={selectedTeam} onChange={handleTeamChange} onKeyDown={handleTeamSelectKeyDown}>
											{teamEditor.getTeamNumbersAsList().map(n =>
												<option key={n} value={n}>Team {n}</option>
											)}
										</FormSelect>
										<p>Press 'a' while in the team selector to add player</p>
									</Col>
								</Row>
							</>
						}
					</Container>
				</ModalBody>
				<ModalFooter>
					<Button variant="secondary" onClick={() => { setModalVisible(false) }}>Cancel</Button>
					<Button variant="primary" onClick={add} disabled={player == null}>Add player</Button>
				</ModalFooter>
			</Modal>
		</>
	)
}