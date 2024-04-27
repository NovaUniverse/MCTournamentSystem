import React, { useEffect, useState } from 'react'
import { Col, Container, Row, Table } from 'react-bootstrap'
import { useTournamentSystemContext } from '../context/TournamentSystemContext'
import axios from 'axios';
import { PublicStatusDTO } from '../scripts/dto/PublicStatusDTO';
import PlayerHeadWithSkinrestorer from '../components/PlayerHeadWithSkinrestorer';

interface TeamEntry {
	teamNumber: number;
	name: string;
	score: number;
	kills: number;
	placement: number;
}

interface PlayerEntry {
	uuid: string;
	name: string;
	teamName: string;
	score: number;
	kills: number;
	placement: number;
}

interface PlayerEntry {

}

export default function LiveStats() {
	const tournamentSystem = useTournamentSystemContext();

	const [leaderboardPlayers, setLeaderboardPlayers] = useState<PlayerEntry[]>([]);
	const [leaderboardTeams, setLeaderboardTeams] = useState<TeamEntry[]>([]);

	const [tournamentName, setTournamentName] = useState<string>("");
	const [winnerTeamName, setWinnerTeamName] = useState<string | null>(null)

	const [error, setError] = useState<string | null>(null);

	useEffect(() => {
		tournamentSystem.hideNavbar();
	}, []);

	useEffect(() => {
		const refresh = async () => {
			let response: any;
			try {
				response = await axios.get(tournamentSystem.apiUrl + "/v1/public/status");
			} catch (err: any) {
				setError("Failed to fetch data. " + err + (err.stack ? " " + err.stack : ""));
				console.error(err);
				return;
			}

			setError(null);

			const status = response.data as PublicStatusDTO;

			status.teams.sort((a, b) => b.score - a.score);
			status.players.sort((a, b) => b.score - a.score);

			if (status.locked_winner == -1) {
				setWinnerTeamName(null);
			} else {
				const team = status.teams.find(t => t.team_number == status.locked_winner);
				if (team != null) {
					setWinnerTeamName(team.display_name);
				}
			}

			setTournamentName(status.tournament_name);

			const players: PlayerEntry[] = [];
			const teams: TeamEntry[] = [];

			for (let i = 0; i < status.teams.length; i++) {
				const data = status.teams[i];

				teams.push({
					name: data.display_name,
					kills: data.kills,
					score: data.score,
					teamNumber: data.team_number,
					placement: i + 1
				});

				setLeaderboardTeams(teams);
			}

			for (let i = 0; i < status.players.length; i++) {
				const data = status.players[i];

				let teamName = "";
				const team = status.teams.find(t => t.team_number == data.team_number);
				if (team != null) {
					teamName = team.display_name;
				}

				players.push({
					kills: data.kills,
					name: data.username,
					score: data.score,
					uuid: data.uuid,
					teamName: teamName,
					placement: i + 1
				});

				setLeaderboardPlayers(players);
			}
		}

		refresh();

		const interval = setInterval(() => {
			refresh();
		}, 2000);

		return () => {
			clearInterval(interval);
		}
	}, []);

	return (
		<>
			<Container className='mt-2'>
				<Row>
					<Col>
						<h1>{tournamentName} live stats</h1>
					</Col>
				</Row>

				{winnerTeamName != null &&
					<Row className="mt-2">
						<Col>
							<h2>Winners: <span className="text-info">{winnerTeamName}</span></h2>
						</Col>
					</Row>
				}

				{error != null &&
					<Row className='mt-2'>
						<Col>
							<p className='text-danger'>{error}</p>
						</Col>
					</Row>
				}

				<Row className='mt-2'>
					<Col sm="12" md="6">
						<h3>Teams</h3>
						<Table striped bordered hover>
							<thead>
								<tr>
									<th>Team</th>
									<th>Placement</th>
									<th>Kills</th>
									<th>Score</th>
								</tr>
							</thead>

							<tbody>
								{leaderboardTeams.map((team) =>
									<tr key={team.teamNumber}>
										<td>{team.name}</td>
										<td>{team.score == 0 ? "n/a" : team.placement}</td>
										<td>{team.kills}</td>
										<td>{team.score}</td>
									</tr>
								)}
							</tbody>
						</Table>
					</Col>
					<Col sm="12" md="6">
						<h3>Players</h3>
						<Table striped bordered hover>
							<thead>
								<tr>
									<th className="t-fit"></th>
									<th>Name</th>
									<th>Placement</th>
									<th>Team</th>
									<th>Kills</th>
									<th>Score</th>
								</tr>
							</thead>

							<tbody>
								{leaderboardPlayers.map((player) =>
									<tr key={player.uuid}>
										<td>
											<PlayerHeadWithSkinrestorer uuid={player.uuid} username={player.name} width={32} height={32} />
										</td>
										<td>{player.name}</td>
										<td>{player.score == 0 ? "n/a" : player.placement}</td>
										<td>{player.teamName}</td>
										<td>{player.kills}</td>
										<td>{player.score}</td>
									</tr>
								)}
							</tbody>
						</Table>
					</Col>
				</Row>
			</Container>
		</>

	)
}
