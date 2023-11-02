import axios from "axios";
import AuthManager from "./AuthManager";
import { StateDTO, createEmptyState } from "./dto/StateDTO";
import { Events } from "./enum/Events";
import EventEmitter from "./utils/EventEmitter";
import toast from "react-hot-toast";
import { error } from "console";
import { ServiceProvidersDTO } from "./dto/ServiceProvidersDTO";

export default class TournamentSystem {
	private _apiUrl: string;
	private _authManager: AuthManager;
	private _events: EventEmitter;
	private _state: StateDTO;
	private _serviceProviders: ServiceProvidersDTO;

	constructor() {
		this._apiUrl = process.env.REACT_APP_API_URL as string;
		this._authManager = new AuthManager(this);
		this._events = new EventEmitter();
		this._state = createEmptyState();
		this._serviceProviders = {};
		
		this.events.on(Events.LOGIN_STATE_CHANGED, () => { this.updateState(); });

		this.init().then(() => {
			console.log("Init complete");
		}).catch((error) => {
			console.log("Error occured during init");
		});
	}

	private async init() {
		const serviceProviderResponse = await axios.get(this._apiUrl + "/v1/service_providers");
		this._serviceProviders = serviceProviderResponse.data as ServiceProvidersDTO;

		await this._authManager.loadExistingLogin();

		console.log("Using API url: " + this._apiUrl);

		setInterval(() => {
			this.updateState();
		}, 1000);
	}

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
			this._state = response.data as StateDTO;
			this.events.emit(Events.STATE_UPDATE, this.state);
			console.log(this.state);
		} catch (err) {
			console.error("Failed to update state");
			console.error(err);
		}
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

	get serviceProviders() {
		return this._serviceProviders;
	}
}