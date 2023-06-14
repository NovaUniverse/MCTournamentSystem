import express, { Express, Request, Response } from 'express';
import * as HTTP from 'http';
import * as SocketIO from 'socket.io';
import Client from './Client';
import client, { Connection, Channel, ConsumeMessage } from 'amqplib'


export default class TournamentSystemWebsocketAPI {
	private clientKeys: string[];

	private io;
	private express: Express;
	private http: HTTP.Server;

	private clients: Client[];

	private rabbitmq: Connection;
	private channel: Channel;

	constructor(port: number, rabbitMQConnectionString: string, clientKeys: string[]) {
		this.clients = [];

		this.clientKeys = clientKeys;

		this.express = express();
		this.express.set("port", port);
		this.http = new HTTP.Server(this.express);
		this.io = new SocketIO.Server(this.http);

		this.express.use('/test', express.static(__dirname + '/../test'));

		this.io.on("connection", (socket: SocketIO.Socket) => {
			let client = new Client(socket, this);
			this.clients.push(client);
			console.log("Client connected and assigned uuid " + client.uuid + " (Client count: " + this.clients.length + ")");
		});

		this.http.listen(port, (): void => {
			console.log("Listening on port " + port);
		});

		this.setupRabbitMQ(rabbitMQConnectionString);

		setInterval(() => {
			this.clients = this.clients.filter(c => !c.disconnected);
		}, 1000);
	}

	private async setupRabbitMQ(connectionString: string) {
		this.rabbitmq = await client.connect(connectionString);
		const consumer = (channel: Channel) => (msg: ConsumeMessage | null): void => {
			if (msg) {
				const data = JSON.parse(msg.content.toString('utf8'));
				this.broadcast(data.key, data.value);
				channel.ack(msg)
			}
		}
		this.channel = await this.rabbitmq.createChannel()
		await this.channel.assertExchange("ts.wsdata", "fanout", { durable: false });
		await this.channel.assertQueue("ts.wsdata", { durable: false })
		await this.channel.bindQueue("ts.wsdata", "ts.wsdata", "");
		this.channel.consume("ts.wsdata", consumer(this.channel))
	}

	public tryLogin(key: string): boolean {
		if (this.clientKeys.includes(key)) {
			return true;
		}
		return false;
	}

	public broadcast(message: string, data: any) {
		console.log("Broadcasting message " + message + " with data " + JSON.stringify(data))
		this.clients.filter(c => c.authenticated && !c.disconnected).forEach(c => c.sendData(message, data));
	}
}