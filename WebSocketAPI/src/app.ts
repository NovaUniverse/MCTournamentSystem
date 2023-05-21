import * as FS from "fs";
import TournamentSystemWebsocketAPI from "./TournamentSystemWebsocketAPI";

require('console-stamp')(console, '[HH:MM:ss.l]');

const port: number = parseInt(process.env.PORT) || 8080;

const rabbitmqConnectionString: string = process.env.RABBITMQ_CONNECTION_STRING || "amqp://localhost:5672";

if (!FS.existsSync("./data")) {
	FS.mkdirSync("./data");
}

if (!FS.existsSync("./data/client_keys.json")) {
	FS.writeFileSync("./data/client_keys.json", JSON.stringify([], null, 4), 'utf8');
}

const clientKeys: string[] = require("../data/client_keys.json");


if (process.env.WS_API_CLIENT_KEY != null) {
	console.log("Adding client key from env variables");
	clientKeys.push(process.env.WS_API_CLIENT_KEY);
}

new TournamentSystemWebsocketAPI(port, rabbitmqConnectionString, clientKeys);