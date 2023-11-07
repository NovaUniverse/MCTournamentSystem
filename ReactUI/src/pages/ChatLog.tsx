import React, { useEffect, useRef } from 'react'
import PageSelection from '../components/nav/PageSelection'
import { Col, Container, Row } from 'react-bootstrap'
import { Terminal } from 'xterm';
import { WebLinksAddon } from 'xterm-addon-web-links';
import { WebglAddon } from 'xterm-addon-webgl';
import { FitAddon } from 'xterm-addon-fit';
import axios from 'axios';
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import ChatDataDTO from '../scripts/dto/ChatDataDTO';
import { ConsoleColor } from '../scripts/utils/ConsoleColor';

import "./ChatLog.scss";

export default function ChatLog() {
	const tournamentSystem = useTournamentSystemContext();

	const terminalRef = useRef<any>();

	useEffect(() => {
		console.log("Setting up chat terminal");

		const terminal = new Terminal();
		const webLinks = new WebLinksAddon();
		const webgl = new WebglAddon();
		const fit = new FitAddon();

		let closed = false;
		let initialFetched = false;
		let lastId = -1;

		const updateSize = () => {
			fit.fit();
		}

		terminal.loadAddon(webgl);
		terminal.loadAddon(fit);
		terminal.loadAddon(webLinks);

		terminal.open(terminalRef.current);

		window.addEventListener('resize', updateSize);

		const processData = (data: ChatDataDTO) => {
			data.messages.forEach(message => {
				if (message.message_id > lastId) {
					lastId = message.message_id;
					terminal.writeln("[" + ConsoleColor.CYAN + message.sent_at + ConsoleColor.RESET + "] <" + ConsoleColor.PURPLE + message.username + ConsoleColor.RESET + "> " + message.content);
				}
			});
		}

		fetchLogs().then(data => {
			processData(data);
			initialFetched = true;
		}).catch((err) => {
			console.error("Failed to fetch logs");
			console.error(err);
			terminal.write(ConsoleColor.RED + "An error occured while fetching chat logs" + ConsoleColor.RESET);
		});

		const interval = setInterval(async () => {
			if (closed) {
				return;
			}

			if (!initialFetched) {
				return;
			}

			try {
				const data = await fetchLogs(lastId);
				processData(data);
			} catch (err) {
				console.error("Failed to fetch logs");
				console.error(err);
			}
		}, 500);

		setTimeout(() => {
			if(closed) {
				return;
			}
			console.log("Delayed resize");
			updateSize();
		}, 1000);

		return () => {
			closed = true;
			clearInterval(interval);
			window.removeEventListener('resize', updateSize);
			fit.dispose();
			webgl.dispose();
			webLinks.dispose();
			setTimeout(() => {
				console.log("Disposing XTerm (delayed)");
				terminal.dispose();
				console.log("XTerm disposed");
			}, 500);
		}
	}, []);

	async function fetchLogs(after: number = -1): Promise<ChatDataDTO> {
		const result = await axios.get(tournamentSystem.apiUrl + "/v1/chat/log?after=" + after, {
			headers: {
				Authorization: `Bearer ${tournamentSystem.authManager.token}`,
			}
		});
		return result.data as ChatDataDTO;
	}

	return (
		<>
			<PageSelection />

			<Container fluid>
				<Row>
					<Col>
						<div ref={terminalRef} className='chat_xterm_container' />
					</Col>
				</Row>
			</Container>
		</>
	)
}
