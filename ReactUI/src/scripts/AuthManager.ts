import axios from "axios";
import TournamentSystem from "./TournamentSystem";
import { Events } from "./enum/Events";
import { Permission } from "./enum/Permission";

export default class AuthManager {
	private tournamentSystem;

	private _isLoggedIn: boolean;
	private _token: string | null;
	private _username: string | null;
	private _permissions: Permission[];

	constructor(tournamentSystem: TournamentSystem) {
		this.tournamentSystem = tournamentSystem;
		this._isLoggedIn = false;
		this._username = null;
		this._token = null;
		this._permissions = [];
	}

	async loadExistingLogin(): Promise<boolean> {
		if (window.localStorage.getItem("token")) {
			this._token = window.localStorage.getItem("token");
			console.log("Validating token");
			const response = await axios.get(this.tournamentSystem.apiUrl + "/v1/user/whoami", {
				headers: {
					Authorization: `Bearer ${this._token}`,
				}
			});
			if (response.data.logged_in) {
				this._isLoggedIn = true;
				this.tournamentSystem.events.emit(Events.LOGIN_STATE_CHANGED);
				console.log("Existing login found");
				return true;
			}
		}
		console.log("Could not find existing login");
		return false;
	}

	async login(username: string, password: string): Promise<boolean> {
		const data = {
			username: username,
			password: password
		};

		const response = await axios.post(this.tournamentSystem.apiUrl + "/v1/user/login", data, {
			validateStatus: (status) => {
				return status == 200 || status == 401;
			},
		});
		if (response.data.success) {
			const token = response.data.token;
			window.localStorage.setItem("token", token);
			this._token = token;
			this._username = response.data.user.username;
			this._permissions = response.data.user.permissions;
			this._isLoggedIn = true;

			console.log("Logged in as " + username);
			console.log("Permissions: " + JSON.stringify(this.permissions));

			this.tournamentSystem.events.emit(Events.LOGIN_STATE_CHANGED);
			return true;
		}
		return false;
	}

	hasPermission(permission: Permission) {
		if (this.permissions.includes(Permission.ADMIN)) {
			return true;
		}
		return this.permissions.includes(permission);
	}

	get username() {
		return this._username;
	}

	get permissions() {
		return this._permissions;
	}

	get isLoggedIn() {
		return this._isLoggedIn;
	}

	get token() {
		return this._token;
	}
}