import axios, { AxiosError, AxiosRequestConfig, AxiosResponse, AxiosResponseHeaders, RawAxiosResponseHeaders } from "axios";
import TournamentSystem from "../TournamentSystem";
import { ScoreEntryType } from "../enum/ScoreEntryType";
import OfflineUserIdDTO from "../dto/OfflineUserIdDTO";
import StaffDTO from "../dto/StaffDTO";
import MapDataDTO from "../dto/MapDataDTO";
import AccountDTO from "../dto/AccountDTO";

export default class TournamentSystemAPI {
	private tournamentSystem;

	constructor(tournamentSystem: TournamentSystem) {
		this.tournamentSystem = tournamentSystem;
	}

	async shutdown(): Promise<GenericRequestResponse> {
		const url = "/v1/system/shutdown";
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true
			}
		}
		throw new Error("Server communication failure");
	}

	async reset(): Promise<GenericRequestResponse> {
		const url = "/v1/system/reset";
		const result = await this.authenticatedRequest(RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true
			}
		}
		throw new Error("Server communication failure");
	}

	async createUser(username: string, password: string, hideIPs: boolean, allowManageUsers: boolean, permissions: string[]): Promise<GenericRequestResponse> {
		const data: any = {
			username: username,
			password: password,
			hide_ips: hideIPs,
			allow_manage_users: allowManageUsers,
			permissions: permissions
		}

		const url = "/v1/user_management/users/create";

		const result = await this.authenticatedRequest(RequestType.PUT, url, data);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 409) {
			return {
				success: false,
				message: "A user with that name already exists"
			}
		}

		return this.defaultResponses(result);
	}

	async changePassword(username: string, password: string): Promise<GenericRequestResponse> {
		const url = "/v1/user_management/users/change_password?username=" + username;
		const result = await this.authenticatedRequest(RequestType.POST, url, password, {
			headers: {
				'Content-Type': 'text/plain'
			}
		});
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "That user could not be found"
			}
		}

		return this.defaultResponses(result);
	}

	async editAccounts(username: string, permission: string[], hideIPs: boolean): Promise<GenericRequestResponse> {
		const data: any = {
			hide_ips: hideIPs,
			permissions: permission
		}

		const url = "/v1/user_management/users/edit?username=" + username;
		const result = await this.authenticatedRequest(RequestType.POST, url, data);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "That user could not be found"
			}
		}

		return this.defaultResponses(result);
	}

	async deleteAccount(username: string): Promise<GenericRequestResponse> {
		const url = "/v1/user_management/users/delete?username=" + username;
		const result = await this.authenticatedRequest(RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "That user could not be found"
			}
		}

		return this.defaultResponses(result);
	}

	async getAccounts(): Promise<AccountDTO[]> {
		const url = "/v1/user_management/users/get";
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return result.response as AccountDTO[];
		}
		throw new Error("Failed to fetch user list. Server responded with code " + result.status);
	}

	async setMapEnabled(mapId: string, enabled: boolean): Promise<GenericRequestResponse> {
		const url = "/v1/maps?mapId=" + mapId;
		const result = await this.authenticatedRequest(enabled ? RequestType.PUT : RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		} else if (result.status == 404) {
			return {
				success: false,
				message: "Could not find map with id " + mapId
			}
		}

		return this.defaultResponses(result);
	}

	async getMaps(): Promise<MapDataDTO[]> {
		const url = "/v1/maps";
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return result.response as MapDataDTO[];
		}
		throw new Error("Failed to fetch map list. Server responded with code " + result.status);
	}

	async setUserStaffRole(uuid: string, username: string, role: string, offlineMode: boolean): Promise<GenericRequestResponse> {
		const url = "/v1/staff";
		const data: any = {
			uuid: uuid,
			role: role,
			username: username,
			offline_mode: offlineMode
		}

		const result = await this.authenticatedRequest(RequestType.PUT, url, data);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		}

		return this.defaultResponses(result);
	}

	async removeStaffUser(uuid: string): Promise<GenericRequestResponse> {
		const url = "/v1/staff?uuid=" + uuid;
		const result = await this.authenticatedRequest(RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true,
				data: result.response
			}
		}

		return this.defaultResponses(result);
	}

	async getStaffList(): Promise<StaffDTO> {
		const url = "/v1/staff";
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return result.response as StaffDTO;
		}
		throw new Error("Failed to fetch staff list. Server responded with code " + result.status);
	}

	async activateTrigger(triggerName: string, sessionId: string): Promise<GenericRequestResponse> {
		const url = "/v1/game/trigger?triggerId=" + triggerName + "&sessionId=" + sessionId;
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true
			}
		} else if (result.status == 409) {
			return {
				success: false,
				message: "No online players found for plugin channel communication"
			}
		}
		throw new Error("Server communication failure");
	}

	async addWhitelistUser(uuid: string, username: string, offline: boolean): Promise<GenericRequestResponse> {
		const url = "/v1/whitelist/users?uuid=" + uuid + "&username=" + username + "&offline_mode=" + String(offline);
		const result = await this.authenticatedRequest(RequestType.PUT, url);
		if (result.status == 200) {
			return {
				success: true
			}
		}
		throw new Error("Server communication failure");
	}

	async removeWhitelistUser(uuid: string): Promise<GenericRequestResponse> {
		const url = "/v1/whitelist/users?uuid=" + uuid;
		const result = await this.authenticatedRequest(RequestType.DELETE, url);
		if (result.status == 200) {
			return {
				success: true
			}
		}
		throw new Error("Server communication failure");
	}

	async getWhitelist(): Promise<GenericRequestResponse> {
		const url = "/v1/whitelist/users";
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return {
				success: true
			}
		}
		throw new Error("Server communication failure");
	}

	async clearWhitelist(): Promise<GenericRequestResponse> {
		const url = "/v1/whitelist/clear";
		const result = await this.authenticatedRequest(RequestType.POST, url);
		if (result.status == 200) {
			return {
				success: true
			}
		}
		throw new Error("Server communication failure");
	}

	async usernameToOfflineUserUUID(username: string): Promise<OfflineUserIdDTO> {
		const url = "/v1/utils/offline_username_to_uuid?username=" + username;
		const result = await this.authenticatedRequest(RequestType.GET, url);
		if (result.status == 200) {
			return result.response as OfflineUserIdDTO;
		}
		throw new Error("Server communication failure");
	}

	async addScore(type: ScoreEntryType, targetId: number, reason: string, amount: number): Promise<GenericRequestResponse> {
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

	async broadcastMessage(text: string): Promise<GenericRequestResponse> {
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

	async setNextMinigame(text: string): Promise<GenericRequestResponse> {
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