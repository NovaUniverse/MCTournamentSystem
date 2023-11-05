import axios from "axios";
import AuthManager from "./AuthManager";
import StateDTO, { createEmptyState } from "./dto/StateDTO";
import { Events } from "./enum/Events";
import EventEmitter from "./utils/EventEmitter";
import ServiceProvidersDTO from "./dto/ServiceProvidersDTO";
import TournamentSystemAPI from "./TournamentSystemAPI";
import ServerDTO from "./dto/ServerDTO";

/**
 * Main class for the amin ui
 */
export default class TournamentSystem {
	private _apiUrl: string;
	private _authManager: AuthManager;
	private _events: EventEmitter;
	private _state: StateDTO;
	private _servers: ServerDTO[];
	private _serviceProviders: ServiceProvidersDTO;
	private _api: TournamentSystemAPI;
	private _criticalError: string | null;
	private _mainInterval: NodeJS.Timeout | null;
	private _initialStateFetched: boolean;

	constructor() {
		this._apiUrl = process.env.REACT_APP_API_URL as string;
		this._authManager = new AuthManager(this);
		this._api = new TournamentSystemAPI(this);
		this._events = new EventEmitter();
		this._state = createEmptyState();
		this._servers = [];
		this._serviceProviders = {};
		this._criticalError = null;
		this._mainInterval = null;
		this._initialStateFetched = false;


		this.events.on(Events.LOGIN_STATE_CHANGED, () => {
			this.updateState();
			this.updateServers();
		});

		this.init().then(() => {
			console.log("Init complete");
		}).catch((error) => {
			console.error("Error occured during init");
			console.error(error);
			this.bigTimeFuckyWucky("An error occured during init");
		});
	}

	/**
	 * Sets up service providers and start the main loop
	 */
	private async init() {
		const serviceProviderResponse = await axios.get(this._apiUrl + "/v1/service_providers");
		this._serviceProviders = serviceProviderResponse.data as ServiceProvidersDTO;

		await this._authManager.loadExistingLogin();

		console.log("Using API url: " + this._apiUrl);

		this._mainInterval = setInterval(() => {
			this.updateState();
			this.updateServers();
		}, 1000);
	}

	/**
	 * Updates the current state
	 */
	async updateState() {
		if (!this.authManager.isLoggedIn) {
			return;
		}

		try {
			const response = await axios.get(this.apiUrl + "/v1/system/status", {
				headers: {
					Authorization: `Bearer ${this._authManager.token}`,
				}
			});
			this._initialStateFetched = true;
			this._state = response.data as StateDTO;
			this.events.emit(Events.STATE_UPDATE, this.state);
		} catch (err) {
			if (!this._initialStateFetched) {
				this.bigTimeFuckyWucky("Failed to fetch initial state");
			}
			console.error("Failed to update state");
			console.error(err);
		}
	}

	/**
	 * Updates the list of servers managed by the tournament system
	 */
	async updateServers() {
		if (!this.authManager.isLoggedIn) {
			return;
		}

		if (this.isInCrashState()) {
			return;
		}

		try {
			const response = await axios.get(this.apiUrl + "/v1/servers/get_servers", {
				headers: {
					Authorization: `Bearer ${this._authManager.token}`,
				}
			});
			this._servers = response.data.servers as ServerDTO[];
			this.events.emit(Events.SERVER_UPDATE, this.servers);
		} catch (err) {
			console.error("Failed to update servers");
			console.error(err);
		}
	}

	/**
	 * Stop main loop and show a critical error message
	 * @param errorMessage The message describing what went wrong
	 */
	bigTimeFuckyWucky(errorMessage: string) {
		this._criticalError = errorMessage;
		this.events.emit(Events.CRASH, this.criticalError);
		if (this._mainInterval != null) {
			clearInterval(this._mainInterval);
		}
	}

	/**
	 * Check if we have crashed
	 * @returns true if we are in a critical error state
	 */
	isInCrashState() {
		return this._criticalError != null;
	}

	get apiUrl() {
		return this._apiUrl;
	}

	get authManager() {
		return this._authManager;
	}

	get events() {
		return this._events;
	}

	get state() {
		return this._state;
	}

	get servers() {
		return this._servers;
	}

	get serviceProviders() {
		return this._serviceProviders;
	}

	get api() {
		return this._api;
	}

	get criticalError() {
		return this._criticalError;
	}
}