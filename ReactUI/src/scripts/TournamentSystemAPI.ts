import axios, { AxiosError, AxiosRequestConfig, AxiosResponse, AxiosResponseHeaders, RawAxiosResponseHeaders } from "axios";
import TournamentSystem from "./TournamentSystem";
import { ScoreEntryType } from "./enum/ScoreEntryType";
import OfflineUserIdDTO from "./dto/OfflineUserIdDTO";

export default class TournamentSystemAPI {
	private tournamentSystem;

	constructor(tournamentSystem: TournamentSystem) {
		this.tournamentSystem = tournamentSystem;
	}

	async usernameToOfflineUserUUID(username: string): Promise<OfflineUserIdDTO> {
		const url = "/v1/utils/offline_username_to_uuid?username=" + username;
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return result.response as OfflineUserIdDTO;
		}
		throw new Error("Server communication failure");
	}

	async addScore(type: ScoreEntryType, targetId: number, reason: string, amount: number) {
		const url = "/v1/score?type=" + type + "&id=" + targetId;

		const body: any = {
			reason: reason,
			amount: amount
		}

		const result = await this.authenticatedRequest(RequestType.PUT, url, body);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "That " + String(type).toLowerCase() + " could not be found"
			}
		}

		return this.defaultResponses(result);
	}

	async clearScore(): Promise<GenericRequestResponse> {
		const url = "/v1/score?all";
		const result = await this.authenticatedRequest(RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		}

		return this.defaultResponses(result);
	}

	async deleteScoreEntry(type: ScoreEntryType, entryId: number): Promise<GenericRequestResponse> {
		const url = "/v1/score?type=" + type + "&id=" + entryId;
		const result = await this.authenticatedRequest(RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		}

		return this.defaultResponses(result);
	}

	async getScore(): Promise<GenericRequestResponse> {
		const url = "/v1/score";
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		}

		return this.defaultResponses(result);
	}

	async broadcastMessage(text: string) {
		const url = "/v1/system/broadcast";
		const result = await this.authenticatedRequest(RequestType.POST, url, text);
		if (result.status == 200) {
			return {
				success: true,
				data: {}
			}
		}
		return this.defaultResponses(result);
	}

	async setNextMinigame(text: string) {
		const url = "/v1/next_minigame";
		const result = await this.authenticatedRequest(RequestType.POST, url, text);
		if (result.status == 200) {
			return {
				success: true,
				data: {}
			}
		}
		return this.defaultResponses(result);
	}

	async sendPlayerToServer(player: string, server: string): Promise<GenericRequestResponse> {
		const url = "/v1/send/send_player?server=" + encodeURIComponent(server) + "&player=" + encodeURIComponent(player);
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: result.response.message
			}
		}

		return this.defaultResponses(result);
	}

	async sendAllToServer(server: string): Promise<GenericRequestResponse> {
		const url = "/v1/send/send_players?server=" + encodeURIComponent(server);
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: result.response.message
			}
		}

		return this.defaultResponses(result);
	}

	async startGame(): Promise<GenericRequestResponse> {
		const url = "/v1/game/start_game";
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 409) {
			return {
				success: false,
				message: "The message broker server could not be reached"
			}
		}

		return this.defaultResponses(result);
	}

	async killServer(serverName: string): Promise<GenericRequestResponse> {
		const url = "/v1/servers/stop?server=" + serverName;
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 409) {
			return {
				success: false,
				message: "Server not running"
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "Server not found"
			}
		} else if (result.status == 403) {
			return {
				success: false,
				message: "You dont have permission to manage servers"
			}
		} else if (result.status == 500) {
			return {
				success: false,
				message: "Failed to kill server. " + result.response.message
			}
		} else if (result.status == 400) {
			return {
				success: false,
				message: "Bad request, there might be somethiing wrong with the code interacting with the api. Server message: " + result.response.message
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "404 Not found, the frontend version might be outdated. Server message: " + result.response.message
			}
		}

		return this.defaultResponses(result);
	}

	async startServer(serverName: string): Promise<GenericRequestResponse> {
		const url = "/v1/servers/start?server=" + serverName;
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 409) {
			return {
				success: false,
				message: "Server already running"
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "Server not found"
			}
		} else if (result.status == 403) {
			return {
				success: false,
				message: "You dont have permission to manage servers"
			}
		} else if (result.status == 500) {
			return {
				success: false,
				message: "Failed to start server. " + result.response.message
			}
		}

		return this.defaultResponses(result);
	}

	async execServerCommand(serverName: string, command: string): Promise<GenericRequestResponse> {
		const url = "/v1/servers/run_command?server=" + serverName;
		const result = await this.authenticatedRequest(RequestType.POST, url, command);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 418) {
			return {
				success: false,
				message: "This server is offline. Please start the server before sending commands to it"
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "Server not found"
			}
		} else if (result.status == 403) {
			return {
				success: false,
				message: "You dont have permission to manage servers"
			}
		} else if (result.status == 500) {
			return {
				success: false,
				message: "Could not run server command due to an error. " + result.response.message
			}
		}

		return this.defaultResponses(result);
	}

	async getServerLogs(serverName: string): Promise<GenericRequestResponse> {
		const url = "/v1/servers/logs?server=" + serverName;
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "Server not found"
			}
		}

		return this.defaultResponses(result);
	}

	async getServerLogSession(serverName: string): Promise<GenericRequestResponse> {
		const url = "/v1/servers/log_session_id?server=" + serverName;
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 405 || result.status == 403 || result.status == 404 || result.status == 500) {
			return {
				success: false,
				message: result.response.message
			}
		}

		return this.defaultResponses(result);
	}

	private defaultResponses(response: WebRequestResponse, useUnauthenticatedMessage = true, usePermissionErrorMessage = true, use500Message = true, use503Message = true): GenericRequestResponse {
		if (response.status == 401 && useUnauthenticatedMessage) {
			return {
				success: false,
				message: "Not authenticated. Try reloading the page"
			}
		}

		if (response.status == 403 && usePermissionErrorMessage) {
			return {
				success: false,
				message: "You dont have permission to do this"
			}
		}

		if (response.status == 500 && use500Message) {
			return {
				success: false,
				message: "An internal server error occured"
			}
		}

		if ((response.status == 500 || response.status == 0) && use503Message) {
			return {
				success: false,
				message: "Service unavailable. The backend server might be down"
			}
		}

		return {
			success: false,
			message: "An unknown error occured"
		}
	}

	private async authenticatedRequest(type: RequestType, endpoint: string, data?: any, config: AxiosRequestConfig<any> = {}): Promise<WebRequestResponse> {
		if (!endpoint.startsWith("http:") && !endpoint.startsWith("https:")) {
			endpoint = this.tournamentSystem.apiUrl + (endpoint.startsWith("/") ? endpoint : "/" + endpoint);
		}

		if (config.headers == null) {
			config.headers = {};
		}

		config.headers.Authorization = `Bearer ${this.tournamentSystem.authManager.token}`;

		let response: AxiosResponse | null | undefined = null;
		try {
			if (type == RequestType.GET) {
				response = await axios.get(endpoint, config);
			} else if (type == RequestType.DELETE) {
				response = await axios.delete(endpoint, config);
			} else if (type == RequestType.POST) {
				response = await axios.post(endpoint, data, config);
			} else if (type == RequestType.PUT) {
				response = await axios.put(endpoint, data, config);
			} else {
				throw new Error("Invalid request type");
			}
		} catch (err: any) {
			if (err.code == "ERR_NETWORK") {
				return {
					status: err.request.status,
					response: {}
				}
			}
			console.log(err);
			response = (err as AxiosError).response;
		}

		const result = {
			response: (response as AxiosResponse).data,
			status: response!.status
		};

		return result;
	}
}

export interface GenericRequestResponse {
	success: boolean;
	data?: any;
	message?: string;
}

interface WebRequestResponse {
	response: any;
	status: number;
}

enum RequestType {
	GET,
	POST,
	DELETE,
	PUT,
}