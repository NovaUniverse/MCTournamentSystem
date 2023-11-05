import React from 'react'
import { useTournamentSystemContext } from '../../context/TournamentSystemContext';
import { Button } from 'react-bootstrap';
import toast from 'react-hot-toast';

interface Props {
	className?: string;
}

export default function ExportSummaryButton({ className }: Props) {
	const tournamentSystem = useTournamentSystemContext();

	async function exportStats() {
		try {
			await tournamentSystem.updateState();

			const dataExport: any = {};
			const servers: any[] = [];
			const players: any[] = [];

			tournamentSystem.state.servers.forEach(server => {
				servers.push(server.name);
			});

			tournamentSystem.state.players.forEach(p => {
				let player: any = {};

				player["username"] = p.username;
				player["uuid"] = p.uuid;
				player["team_number"] = p.team_number;
				player["score"] = p.score;
				player["kills"] = p.kills;
				player["team_score"] = p.team_score;

				players.push(player);
			});

			dataExport["servers"] = servers;
			dataExport["teams"] = tournamentSystem.state.teams;
			dataExport["players"] = players;

			console.log("Data collected. Downloading...");
			console.log(dataExport);

			let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(dataExport, null, 4));
			let downloadAnchorNode = document.createElement('a');
			downloadAnchorNode.setAttribute("href", dataStr);
			downloadAnchorNode.setAttribute("download", "tournament_data.json");
			document.body.appendChild(downloadAnchorNode); // required for firefox
			downloadAnchorNode.click();
			downloadAnchorNode.remove();

			toast.success("Success. JSON Download started");
		} catch (err) {
			toast.error("An error occured while exporting summary");
			console.error("An error occured");
			console.error(err);
		}
	}

	return (
		<Button className={className} variant='info' onClick={exportStats}>Export Summary</Button>
	)
}
