import axios, { AxiosHeaders, AxiosRequestConfig } from "axios";
import TournamentSystem from "./TournamentSystem";
import { Events } from "./enum/Events";
import { Permission } from "./enum/Permission";
import { LocalStorageKeys } from "./enum/LocalStorageKeys";

/**
 * This class manages authentication and api tokens
 */
export default class AuthManager {
	private tournamentSystem;

	private _isLoggedIn: boolean;
	private _token: string | null;
	private _username: string | null;
	private _permissions: Permission[];
	private _hasEditUserPermission: boolean;

	constructor(tournamentSystem: TournamentSystem) {
		this.tournamentSystem = tournamentSystem;
		this._isLoggedIn = false;
		this._username = null;
		this._token = null;
		this._hasEditUserPermission = false;
		this._permissions = [];
	}

	/**
	 * Attempt to load existing token from localStorage
	 * @returns true if existing login was found
	 */
	async loadExistingLogin(): Promise<boolean> {
		if (window.localStorage.getItem(LocalStorageKeys.TOKEN)) {
			this._token = window.localStorage.getItem(LocalStorageKeys.TOKEN);
			console.log("Validating token");
			const response = await axios.get(this.tournamentSystem.apiUrl + "/v1/user/whoami", {
				headers: {
					Authorization: `Bearer ${this._token}`,
				}
			});
			if (response.data.logged_in) {
				this._permissions = response.data.permissions;
				this._username = response.data.username;
				this._hasEditUserPermission = response.data.can_manage_accounts;
				this._isLoggedIn = true;
				this.tournamentSystem.events.emit(Events.LOGIN_STATE_CHANGED);
				console.log("Existing login found");
				return true;
			}
		}
		console.log("Could not find existing login");
		return false;
	}

	/**
	 * Tries to log in using the provided credentials
	 * @param username The username
	 * @param password The password
	 * @returns true on success
	 */
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
			window.localStorage.setItem(LocalStorageKeys.TOKEN, token);
			this._token = token;
			this._username = response.data.user.username;
			this._permissions = response.data.user.permissions;
			this._hasEditUserPermission = response.data.user.can_manage_accounts;
			this._isLoggedIn = true;

			console.log("Logged in as " + username);
			console.log("Permissions: " + JSON.stringify(this.permissions));

			this.tournamentSystem.events.emit(Events.LOGIN_STATE_CHANGED);
			return true;
		}
		return false;
	}

	/**
	 * Check if a user has the provided permission
	 * @param permission The permission value to check
	 * @returns true if the user has the permission or is admin
	 */
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

	get authHeaders(): any {
		return {
			Authorization: `Bearer ${this._token}`,
		}
	}

	get hasEditUserPermission() {
		return this._hasEditUserPermission;
	}
}