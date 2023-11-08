import React, { ChangeEvent, useEffect, useRef, useState } from 'react'
import ServerDTO from '../../../scripts/dto/ServerDTO';
import { Badge, Button, Col, Container, FormControl, InputGroup, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle, Row } from 'react-bootstrap';
import { useTournamentSystemContext } from '../../../context/TournamentSystemContext';
import { Terminal } from 'xterm';
import { WebglAddon } from 'xterm-addon-webgl';
import { FitAddon } from 'xterm-addon-fit';
import { WebLinksAddon } from 'xterm-addon-web-links';
import { ConsoleColor } from '../../../scripts/utils/ConsoleColor';

import "xterm/css/xterm.css";
import "./ServerConsoleModal.scss";
import StartServerButton from '../../buttons/server/StartServerButton';
import KillServerButton from '../../buttons/server/KillServerButton';
import toast from 'react-hot-toast';

interface Props {
	visible: boolean;
	server: ServerDTO;
	onClose: () => void;
}

export default function ServerConsoleModal({ server, visible, onClose }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	const [command, setCommand] = useState<string>("");
	const [failedToFetchLogs, setFailedToFetchLogs] = useState<boolean>(false);

	const terminalRef = useRef<any>();

	function formatLine(line: string) {
		// NovaCore log colors
		line = line.replace("[TRACE]", "[" + ConsoleColor.PURPLE + "TRACE" + ConsoleColor.RESET + "]")
		line = line.replace("[DEBUG]", "[" + ConsoleColor.CYAN + "DEBUG" + ConsoleColor.RESET + "]")
		line = line.replace("[INFO]", "[" + ConsoleColor.BLUE + "INFO" + ConsoleColor.RESET + "]")
		line = line.replace("[WARNING]", "[" + ConsoleColor.YELLOW + "WARNING" + ConsoleColor.RESET + "]")
		line = line.replace("[ERROR]", "[" + ConsoleColor.RED + "ERROR" + ConsoleColor.RESET + "]")
		line = line.replace("[FATAL]", "[" + ConsoleColor.RED + "FATAL" + ConsoleColor.RESET + "]")
		line = line.replace("[SUCCESS]", "[" + ConsoleColor.GREEN + "SUCCESS" + ConsoleColor.RESET + "]")
		return line;
	}

	useEffect(() => {
		if (visible) {
			console.log("Server console init");
			const terminal = new Terminal();
			const webLinks = new WebLinksAddon();
			const webgl = new WebglAddon();
			const fit = new FitAddon();

			const updateSize = () => {
				fit.fit();
			}

			let lastSessionId = "";
			let lastMessageId = 0;
			let isPrinting = false;
			let ready = false;
			let removed = false;
			let wasRunning = false;
			let initialFetch = true;

			terminal.loadAddon(webgl);
			terminal.loadAddon(fit);
			terminal.loadAddon(webLinks);

			terminal.open(terminalRef.current);

			window.addEventListener('resize', updateSize);

			const fetchInitial = async () => {
				const response = await tournamentSystem.api.getServerLogSession(server.name);

				if (removed) {
					return;
				}

				if (response.success) {
					lastSessionId = response.data.session_id;
					lastMessageId = -1;
					if (response.data.session_id == undefined) {
						terminal.writeln(ConsoleColor.YELLOW + "Server not yet started. Logs will start to display as soon as the server goes online" + ConsoleColor.RESET);
					} else {
						terminal.writeln(ConsoleColor.CYAN + "Session id is: " + lastSessionId + ConsoleColor.RESET);
					}
					ready = true;
				} else {
					terminal.writeln(ConsoleColor.RED + "Failed to fetch session id. " + response.message + ConsoleColor.RESET);
				}
			}

			const fetchInterval = setInterval(async () => {
				if (!ready) {
					return;
				}

				if (isPrinting) {
					return;
				}

				const logRequest = await tournamentSystem.api.getServerLogs(server.name);

				if (removed) {
					return;
				}

				if (logRequest.success) {
					setFailedToFetchLogs(false);

					if (logRequest.data.server_running && !wasRunning) {
						wasRunning = true;
					} else if (!logRequest.data.server_running && wasRunning) {
						wasRunning = false;
						terminal.writeln(ConsoleColor.RED + "Server changed state to: Offline" + ConsoleColor.RESET);
					}

					const lines = logRequest.data.log_data as string[];

					if (logRequest.data.session_id != lastSessionId) {
						console.log("New session id detected. Clearing console");
						terminal.clear();
						terminal.writeln(ConsoleColor.BLUE + "Session changed. Starting new console" + ConsoleColor.RESET);
						lastMessageId = -1;
						lastSessionId = logRequest.data.session_id;
						lastMessageId = 0;
					}

					if (lastMessageId < (lines.length - 1)) {
						console.debug("New data detected. Printing lines to console");
						isPrinting = true;
						while (lastMessageId < lines.length - 1) {
							lastMessageId++;

							const line = formatLine(lines[lastMessageId]);

							terminal.writeln(line);
						}
						isPrinting = false;
					}

					if (initialFetch) {
						initialFetch = false;
						if (!logRequest.data.server_running) {
							if (lastSessionId != undefined && lastSessionId != "") {
								terminal.writeln(ConsoleColor.RED + "Server is offline" + ConsoleColor.RESET);
							}
						}
					}
				} else {
					console.error("Failed to fetch logs");
					setFailedToFetchLogs(true);
				}
			}, 500);

			terminal.writeln(ConsoleColor.GREEN + "Connecting to " + server.name + "..." + ConsoleColor.RESET);

			updateSize();
			fetchInitial();

			return () => {
				removed = true;
				clearInterval(fetchInterval);
				window.removeEventListener('resize', updateSize);
				fit.dispose();
				webgl.dispose();
				webLinks.dispose();
				setTimeout(() => {
					console.log("Disposing XTerm (delayed)");
					terminal.dispose();
					console.log("XTerm disposed");
				}, 500);
			};
		}
	}, [visible]);

	function handleCommandChange(e: ChangeEvent<any>) {
		setCommand(e.target.value);
	}

	async function executeCommand() {
		if(command.trim().length == 0) {
			return;
		}

		const req = await tournamentSystem.api.execServerCommand(server.name, command);
		if (req.success) {
			toast.success("Command executed");
			setCommand("");
		} else {
			toast.error(String(req.message));
		}
	}

	function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
		if (e.key === 'Enter') {
			executeCommand();
		}
	}

	return (
		<Modal fullscreen show={visible} onHide={onClose} >
			<ModalHeader closeButton>
				<ModalTitle>
					Server console: {server.name} {server.is_running ? <Badge bg="success">Running</Badge> : <Badge bg="danger">Offline</Badge>} {failedToFetchLogs && <Badge className='mb-2' bg='warning'>Failed to fetch logs</Badge>}
				</ModalTitle>
			</ModalHeader>

			<ModalBody>
				<Container fluid>
					<Row>
						<Col>
							<div ref={terminalRef} className='server_xterm_container' />
						</Col>
					</Row>
				</Container>
			</ModalBody>
			<ModalFooter>
				<InputGroup>
					<FormControl type='text' value={command} onChange={handleCommandChange} onKeyDown={handleKeyDown} placeholder='Enter command. Press enter key to run' />
					<Button variant="primary" onClick={executeCommand}>Send</Button>
					{server.is_running ?
						<KillServerButton server={server} />
						:
						<StartServerButton server={server} />
					}
					<Button variant="secondary" onClick={onClose}>Close</Button>
				</InputGroup>
			</ModalFooter>
		</Modal>
	)
}
