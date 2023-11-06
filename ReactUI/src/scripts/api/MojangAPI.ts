import axios from "axios";

export default class MojangAPI {
	private _baseUrl: string;

	constructor(baseUrl: string) {
		this._baseUrl = baseUrl.replace(/\/$/, '');
	}

	async usernameToUUID(username: string): Promise<UUIDLookupResult> {
		try {
			const result = await axios.get(this.baseUrl + "/username_to_uuid/" + username);
			return {
				found: true,
				uuid: result.data.uuid
			}
		} catch (err: any) {
			if (err.request.status == 404) {
				return {
					found: false
				}
			} else if (err.request.status == 400) {
				throw new Error("Bad request: The username is invalid");
			}
			throw new Error(err);
		}
	}

	async getProfile(uuid: string): Promise<ProfileLookupResult> {
		try {
			const result = await axios.get(this.baseUrl + "/profile/" + uuid);
			return {
				found: true,
				data: result.data
			}
		} catch (err: any) {
			if (err.request.status == 404) {
				return {
					found: false
				}
			} else if (err.request.status == 400) {
				throw new Error("Bad request: The uuid is invalid");
			}
			throw new Error(err);
		}
	}

	get baseUrl(): string {
		return this._baseUrl;
	}
}

export interface ProfileData {
	id: string;
	name: string;
	properties: Property[];
	profileActions: string[];
}

export interface Property {
	name: string;
	value: string;
}

export interface ProfileLookupResult {
	found: boolean;
	data?: ProfileData;
}

export interface UUIDLookupResult {
	found: boolean;
	uuid?: string;
}