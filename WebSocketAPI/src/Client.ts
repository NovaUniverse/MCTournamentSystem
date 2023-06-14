import { Socket } from "socket.io";
import { v4 } from "uuid";
import TournamentSystemWebsocketAPI from "./TournamentSystemWebsocketAPI";

export default class Client {
	public uuid: string;
	private socket: Socket;
	public disconnected: boolean;
	public authenticated: boolean;
	private server: TournamentSystemWebsocketAPI;

	private timeout: any;

	constructor(socket: Socket, server: TournamentSystemWebsocketAPI) {
		this.uuid = v4();
		this.socket = socket;
		this.disconnected = false;
		this.authenticated = false;
		this.server = server;

		let authOk = false;
		if (socket.handshake.auth["key"] != null) {
			let key = socket.handshake.auth["key"];
			if (typeof key === 'string') {
				if (this.server.tryLogin(key)) {
					this.authenticated = true;
					console.log("Client with uuid " + this.uuid + " authenticated");
					this.sendData("auth_response", { "success": true, "uuid": this.uuid });
				} else {
					this.sendData("auth_response", { "success": false, "message": "Authentication failed" });
					this.socket.disconnect(true);
					return;
				}
			} else {
				this.sendData("error", {
					"error": "Invalid data received. Disconnecting socket"
				});
				this.socket.disconnect(true);
				return;
			}
		}

		socket.on("disconnect", () => {
			console.log("Client with id " + this.uuid + " disconnected");
			this.disconnected = true;

			if (this.timeout != null) {
				clearTimeout(this.timeout);
				this.timeout = null;
			}
		});

		socket.on("message", (message: string, content: any) => {
			this.handleIncommingMessage(message, content);
		});

		if (!authOk) {
			this.timeout = setTimeout(() => {
				console.log("Auth timeout in client with id " + this.uuid);
				this.disconnected = true;
				this.timeout = null;
				this.sendData("auth_timeout", {
					"message": "Socket did not authenticate within 20 seconds. Disconnecting"
				});
				this.socket.disconnect(true);
			}, 20000);
		}
	}

	public sendData(message: string, content: any): void {
		this.socket.send(message, content);
	}

	private handleIncommingMessage(message: string, content: any): void {
		try {
			if (message.toLocaleLowerCase() == "ping") {
				this.socket.send("pong", {
					"client_uuid": this.uuid
				});
				return;
			}

			if (message.toLocaleLowerCase() == "auth") {
				if (!this.authenticated) {
					let key = content.key;
					if (key != null) {
						if (typeof key === 'string') {
							if (this.server.tryLogin(key)) {
								this.authenticated = true;
								console.log("Client with uuid " + this.uuid + " authenticated");
								this.sendData("auth_response", { "success": true, "uuid": this.uuid });
								if (this.timeout != null) {
									clearTimeout(this.timeout);
									this.timeout = null;
								}
								return;
							}
						}
					}
					if (this.timeout != null) {
						clearTimeout(this.timeout);
						this.timeout = null;
					}
					console.log("Client with uuid " + this.uuid + " failed to authenticate and got disconnected");
					this.sendData("auth_response", { "success": false, "message": "Authentication failed" });
					this.socket.disconnect(true);
				} else {
					this.sendData("auth_response", { "success": false, "message": "Already authenticated" });
				}
				return;
			}
		} catch (err) {
			if (this.timeout != null) {
				clearTimeout(this.timeout);
				this.timeout = null;
			}

			console.error(err);
			this.sendData("error", {
				"error": "Invalid data received. Disconnecting socket"
			});
			this.socket.disconnect(true);
		}
	}
}