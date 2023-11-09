import React, { ChangeEvent, useState } from 'react'
import { Button, Col, Container, FormControl, FormGroup, FormLabel, Row } from 'react-bootstrap'
import { useTournamentSystemContext } from '../context/TournamentSystemContext';
import toast from 'react-hot-toast';
import PageSelection from '../components/nav/PageSelection';
import { Permission } from '../scripts/enum/Permission';

export default function ScoreSnapshot() {
	const tournamentSystem = useTournamentSystemContext();

	const [data, setData] = useState<string>("");

	function handleDataChange(e: ChangeEvent<any>) {
		setData(e.target.value);
	}

	function handleFileChange(e: ChangeEvent<any>) {
		const file = e.target.files[0];

		if (file) {
			const reader = new FileReader();
			reader.onload = (r) => {
				const content = r.target!.result;
				setData(String(content));
				toast.success("File loaded");
			};
			reader.readAsText(file);
		}
	};

	async function exportData() {
		const data = await tournamentSystem.api.exportScoreSnapshot();

		if (data.success) {
			let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data.data, null, 4));
			let downloadAnchorNode = document.createElement('a');
			downloadAnchorNode.setAttribute("href", dataStr);
			downloadAnchorNode.setAttribute("download", "TournamentScoreSnapshot.json");
			document.body.appendChild(downloadAnchorNode); // required for firefox
			downloadAnchorNode.click();
			downloadAnchorNode.remove();

			toast.success("Score snapshot downloaded");
		} else {
			console.error("Failed to export snapshot. " + data.message);
			toast.error("Failed to export snapshot. " + data.message);
		}
	}

	async function importData() {
		if (data.trim().length == 0) {
			toast.error("Pleas paste JSON or import snapshot file first");
			return;
		}

		let json: any;
		try {
			json = JSON.parse(data);
		} catch (err) {
			console.log("Failed to parse json");
			console.error(err);
			toast.error("Failed to parse data. Please check that the input is valid json and try again");
			return;
		}

		if (!Array.isArray(json.players) || !Array.isArray(json.teams)) {
			toast.error("The provided data does not seem to be valid score anspshot data");
			return;
		}

		const response = await tournamentSystem.api.importScoreSnapshot(json);
		if (response.success) {
			toast.success("Score imported");
		} else {
			console.error("Failed to import score snapshot: " + response.message);
			toast.error("Failed to import score snapshot, please verify that valid data was provided. " + response.message);
		}
	}

	return (
		<Container fluid>
			<PageSelection />

			<Row>
				<Col>
					<Button variant='success' onClick={exportData} className='mx-2 my-2'>Export snapshot</Button>
					<Button variant='success' onClick={importData} className='my-2' disabled={!tournamentSystem.authManager.hasPermission(Permission.IMPORT_SCORE_SNAPSHOT)}>Import snapshot</Button>
				</Col>
			</Row>

			<Row>
				<Col>
					<FormGroup>
						<FormLabel>Pase JSON here</FormLabel>
						<FormControl as="textarea" rows={10} value={data} onChange={handleDataChange} />
					</FormGroup>
					<hr />
					<FormGroup>
						<FormLabel>Or upload JSON file with score data</FormLabel>
						<FormControl type='file' onChange={handleFileChange} />
					</FormGroup>
				</Col>
			</Row>
		</Container>
	)
}
