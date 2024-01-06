import axios from "axios";
import TeamEditorEntry from "./TeamEditorEntry";
import TournamentSystem from "./TournamentSystem";
import { Events } from "./enum/Events";
import StateDTO from "./dto/StateDTO";
import toast from "react-hot-toast";

export default class TeamEditor {
	private _tournamentSystem;
	private _teamSize: number;
	private _players: TeamEditorEntry[];
	private _isReady: boolean;

	constructor(tournamentSystem: TournamentSystem) {
		this._tournamentSystem = tournamentSystem;
		this._players = [];
		this._isReady = true;
		this._teamSize = 12;
		this.init();
	}

	private async init() {
		try {
			const response = await axios.get(this.tournamentSystem.apiUrl + "/v1/system/status", {
				headers: {
					Authorization: `Bearer ${this.tournamentSystem.authManager.token}`,
				}
			});
			const state = response.data as StateDTO;
			this._teamSize = state.system.team_size;
			this.tournamentSystem.events.emit(Events.TEAM_EDITOR_TEAM_SIZE_CHANGED);
		} catch (err: any) {
			console.error("Failed to fetch state");
			console.error(err);
			this.tournamentSystem.errorMessage = err.message + " " + err.stack;
			this.tournamentSystem.bigTimeFuckyWucky("Team editor failed to load initial team size");
			return;
		}

		try {
			this.loadExistingTeam();
			console.log("Loaded " + this.players.length + " entries");
		} catch (err: any) {
			console.error("Failed to fetch team list");
			console.error(err);
			this.tournamentSystem.errorMessage = err.message + " " + err.stack;
			this.tournamentSystem.bigTimeFuckyWucky("Team editor failed to load team list");
			return;
		}

		this._isReady = true;
		this.sendTeamChange();
	}

	public async loadExistingTeam() {
		const response = await axios.get(this.tournamentSystem.apiUrl + "/v1/team/export_team_data", {
			headers: {
				Authorization: `Bearer ${this.tournamentSystem.authManager.token}`,
			}
		});
		const teams = Object.values(response.data.teams_data) as TeamEditorEntry[];
		this.players = teams;
		toast.success("Teams imported from tournament system");
	}

	async updateUsernames() {
		if (this.offlineMode) {
			toast.error("Cant update names in offline mode");
			return;
		}

		let count = 0;

		const newPlayers = this.players.map(p => p);

		for (let i = 0; i < newPlayers.length; i++) {
			const name = newPlayers[i].username;
			const uuid = newPlayers[i].uuid;
			try {
				const profile = await this.tournamentSystem.mojangApi.getProfile(newPlayers[i].uuid);
				if (profile.found) {
					newPlayers[i].username = profile.data!.name;
					count++;
				} else {
					toast.error("Could not find player with uuid " + uuid);
				}
			} catch (err) {
				console.error("Failed to fetch profile for " + name + " (" + uuid + ")");
				console.error(err);
				toast.error("Failed to update name for " + name + " (" + uuid + ")");
			}
		}

		this.players = newPlayers;

		toast.success(count + " names successfully updated");
	}

	get tournamentSystem() {
		return this._tournamentSystem;
	}

	public sendTeamChange() {
		this.tournamentSystem.events.emit(Events.TEAM_EDITOR_UPDATE, this.players);
	}

	set players(p: TeamEditorEntry[]) {
		this._players = p;
		this.sendTeamChange();
	}

	get teamSize() {
		return this._teamSize;
	}

	get players() {
		return this._players;
	}

	get isReady() {
		return this._isReady;
	}

	get offlineMode() {
		return this.tournamentSystem.state.system.offline_mode;
	}

	getTeamNumbersAsList() {
		const result: number[] = [];
		for (let i = 0; i < this.teamSize; i++) {
			result.push(i + 1);
		}
		return result;
	}
}