import axios from "axios";
import AuthManager from "./AuthManager";
import StateDTO, { createEmptyState } from "./dto/StateDTO";
import { Events } from "./enum/Events";
import EventEmitter from "./utils/EventEmitter";
import ServiceProvidersDTO from "./dto/ServiceProvidersDTO";
import TournamentSystemAPI from "./api/TournamentSystemAPI";
import ServerDTO from "./dto/ServerDTO";
import MojangAPI from "./api/MojangAPI";
import toast from "react-hot-toast";
import { Theme, getThemeFix } from "./enum/Theme";

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
	private _mojangApi: MojangAPI;
	private _criticalError: string | null;
	private _mainInterval: NodeJS.Timeout | null;
	private _initialStateFetched: boolean;
	private _activeTheme: Theme;
	private _errorCount: number;
	private _connectionLost: boolean;

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
		this._activeTheme = Theme.QUARTZ;
		this._errorCount = 0;
		this._connectionLost = false;

		// Use default until service providers are loaded
		this._mojangApi = new MojangAPI("https://mojangapi.novauniverse.net/");

		this.events.on(Events.FORCE_STATE_UPDATE, () => {
			this.updateState();
		});

		this.events.on(Events.LOGIN_STATE_CHANGED, () => {
			this.updateState();
			this.updateServers();
		});

		if (localStorage.getItem("ts_theme") != null) {
			const theme = localStorage.getItem("ts_theme") as Theme;
			if (Object.values(Theme).includes(theme)) {
				this.setTheme(theme, false);
			} else {
				console.warn("Could not find theme " + theme);
				setTimeout(() => { toast.error("Could not find theme " + theme) }, 1000); // Delay to allow toast library to load
			}
		}

		this.init().then(() => {
			console.log("Init complete");
		}).catch((error) => {
			console.error("Error occured during init");
			console.error(error);
			this.bigTimeFuckyWucky("An error occured during init");
		});
	}

	private killMainLoop() {
		if (this._mainInterval != null) {
			clearInterval(this._mainInterval);
		}
	}

	private async addAPIErrorCount() {
		if (this._connectionLost) {
			// At this point we are dead so no need to worry about the small problems in life
			return;
		}

		this._errorCount++;

		if (window.location.pathname.endsWith("/editor")) {
			// It would not be nice to remove all the users progress if the server disconnects in the editor so lets wait instead
			return;
		}

		if (this._errorCount > 10) {
			this._errorCount = 0;

			// Check what the error is
			try {
				const response = await axios.get(this.apiUrl + "/v1/user/whoami", {
					headers: {
						Authorization: `Bearer ${this._authManager.token}`,
					}
				});
				if (!response.data.logged_in) {
					// Token invalid or expired. Reload to allow the user to log in again
					window.location.reload();
				} else {
					// Well everything seems alright so lets continue as normal
				}
			} catch (err) {
				// This is bad
				toast.error("Seems like we lost connection to the server");
				this.killMainLoop();
				this._connectionLost = true;
				this.events.emit(Events.DISCONNECTED);
			}
		}
	}

	setTheme(theme: Theme, persistent: boolean = true) {
		if (persistent) {
			localStorage.setItem("ts_theme", theme);
		}
		const event = new CustomEvent("ts_SetTheme", {
			bubbles: false,
			cancelable: true,
			detail: {
				theme: String(theme),
				fix: getThemeFix(theme)
			}
		});
		window.dispatchEvent(event);
		this._activeTheme = theme;
		this.events.emit(Events.THEME_CHANGED, theme);
	}

	/**
	 * Sets up service providers and start the main loop
	 */
	private async init() {
		const serviceProviderResponse = await axios.get(this._apiUrl + "/v1/service_providers");
		this._serviceProviders = serviceProviderResponse.data as ServiceProvidersDTO;

		if (this.serviceProviders.mojang_api_proxy != null) {
			console.log("Using mojang api proxy provider: " + this.serviceProviders.mojang_api_proxy);
			this._mojangApi = new MojangAPI(this.serviceProviders.mojang_api_proxy);
		} else {
			console.warn("No mojang api proxy provider configured. Consider setting up your own to not run into rate limits https://github.com/NovaUniverse/MojangAPIProxy");
		}

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
			} else {
				this.addAPIErrorCount();
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
			this.addAPIErrorCount();
		}
	}

	/**
	 * Stop main loop and show a critical error message
	 * @param errorMessage The message describing what went wrong
	 */
	bigTimeFuckyWucky(errorMessage: string) {
		this._criticalError = errorMessage;
		this.events.emit(Events.CRASH, this.criticalError);
		this.killMainLoop();
	}

	/**
	 * Check if we have crashed
	 * @returns true if we are in a critical error state
	 */
	isInCrashState() {
		return this._criticalError != null;
	}

	get connectionLost() {
		return this._connectionLost;
	}

	get activeTheme() {
		return this._activeTheme;
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

	get mojangApi() {
		return this._mojangApi;
	}

	get criticalError() {
		return this._criticalError;
	}
}