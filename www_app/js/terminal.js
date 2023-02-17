const ServerConsole = {
	activeServer: null,
	lastMessageId: 0,
	lastSessionId: "00000000-0000-0000-0000-000000000000",
	pollingIntervalId: null,
	terminal: null,
	fitAddon: null,
	webGLAddon: null,
	webLinkAddon: null,
	ready: false,
	isPrinting: false,
	theme: null,

	openConsole(server) {
		if (this.pollingIntervalId != null) {
			this.closeConsole();
		}
		this.ready = false;
		console.log("Opening console");
		this.activeServer = server;

		this.terminal.clear();

		$("#serverConsoleModalTitle").text("Console: " + server);
		$("#serverConsoleModal").modal("show");

		this.pollingIntervalId = setInterval(() => {
			this.fitAddon.fit();
			if (this.ready) {
				if (this.isPrinting) {
					return;
				}

				$.ajax({
					type: "GET",
					url: "/api/v1/servers/logs?server=" + this.activeServer,
					success: (data) => {
						if (data.session_id != this.lastSessionId) {
							console.log("New session id detected. Clearing console");
							this.terminal.clear();
							this.terminal.writeln("Session changed. Starting new console");
							this.lastMessageId = -1;
							this.lastSessionId = data.session_id;
							this.lastMessageId = 0;
						}

						if (this.lastMessageId < (data.log_data.length - 1)) {
							console.log("New data detected. Printing lines to console");
							this.isPrinting = true;
							while (this.lastMessageId < data.log_data.length - 1) {
								this.lastMessageId++;
								this.terminal.writeln(data.log_data[this.lastMessageId]);
							}
							this.isPrinting = false;
						}
					},
					error: (xhr, ajaxOptions, thrownError) => {
						console.error("Failed to fetch logs");
						console.error(xhr);
					},
					dataType: "json"
				});
			}
		}, 500);

		this.terminal.writeln("Connecting to " + server + "...");

		$.ajax({
			type: "GET",
			url: "/api/v1/servers/log_session_id?server=" + this.activeServer,
			success: (data) => {
				this.lastSessionId = data.session_id;
				this.lastMessageId = -1;
				if (data.session_id == undefined) {
					this.terminal.writeln("Server not yet started. Logs will start to display as soon as the server goes online");
				} else {
					this.terminal.writeln("Session id is: " + this.lastSessionId);
				}
				this.ready = true;
			},
			error: (xhr, ajaxOptions, thrownError) => {
				console.error(xhr);

				if (xhr.status == 0 || xhr.status == 503) {
					this.terminal.writeln("Backend communication failure");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 404 || xhr.status == 500) {
					this.terminal.writeln("Failed to fetch session id. " + xhr.responseJSON.message);
				} else {
					this.terminal.writeln("Failed to fetch session id due to an unknown error");
				}
			},
			dataType: "json"
		});
	},

	closeConsole() {
		if (this.pollingIntervalId == null) {
			return;
		}
		console.log("Closing console");
		$("#serverConsoleModal").modal("hide");
		clearInterval(this.pollingIntervalId);
		this.terminal.clear();
		this.lastSessionId = "00000000-0000-0000-0000-000000000000";
		this.lastMessageId = -1;
		this.isPrinting = false;
		this.ready = false;
		this.activeServer = null;
	},

	init() {
		if (this.terminal != null) {
			return;
		}
		$(".close-console-button").on("click", () => this.closeConsole());

		let config = {};

		if (this.theme != null) {
			config["theme"] = this.theme;
		}

		this.terminal = new Terminal(config);
		this.fitAddon = new FitAddon.FitAddon();
		this.webGLAddon = new WebglAddon.WebglAddon();
		this.webLinkAddon = new WebLinksAddon.WebLinksAddon();

		this.webGLAddon.onContextLoss(e => {
			console.log("WebglAddon onContextLoss");
			this.webGLAddon.dispose();
		});

		this.terminal.loadAddon(this.webGLAddon);
		this.terminal.loadAddon(this.fitAddon);
		this.terminal.loadAddon(this.webLinkAddon);
		this.terminal.open(document.getElementById("server_terminal"));

		$("#console_input_field")[0].addEventListener("keypress", (e) => {
			if (e.key == "Enter") {
				let command = $("#console_input_field").val();

				if (command.trim().length == 0) {
					return;
				}

				console.log("Attempting to run command " + command);
				$("#console_input_field").val("");
				$.ajax({
					type: "POST",
					url: "/api/v1/servers/run_command?server=" + this.activeServer,
					data: command,
					contentType: 'text/plain',
					success: (data) => {
						toastr.info("Ran command: " + command + " on server " + this.activeServer);
					},
					error: (xhr, ajaxOptions, thrownError) => {
						console.error(xhr);

						if (xhr.status == 0 || xhr.status == 503) {
							toastr.error("Failed to communicate with backend server");
							return;
						}

						if (xhr.status == 418) {
							toastr.error("This server is offline. Please start the server before sending commands to it");
							return;
						}

						if (xhr.status == 405 || xhr.status == 403 || xhr.status == 500) {
							toastr.error("Failed to execute command. " + xhr.responseJSON.message);
						} else {
							toastr.error("Failed to execute command due to unknown error");
							toastr.error("Could not run server command due to an error. " + xhr.statusText);
						}
						$("#console_input_field").val(command);
					}
				});
			}
		});
	},

	clearCustomTheme() {
		this.theme = null;
		this.terminal.options.theme = {};
		localStorage.removeItem("server_xtermjs_custom_theme");
	},

	setCustomTheme(data) {
		if (data == null) {
			this.clearCustomTheme();
			return;
		}
		console.log(this);
		this.theme = data;
		this.terminal.options.theme = data;
		localStorage.setItem("server_xtermjs_custom_theme", JSON.stringify(data));
	}
}

$(() => {
	let theme = localStorage.getItem("server_xtermjs_custom_theme");
	if (theme != null) {
		if (theme.trim().length > 0) {
			console.log("Tring to read custom XTerm.JS theme");
			try {
				let themeData = JSON.parse(theme);
				console.log("Custom XTerm.JS theme data:");
				console.log(themeData);
				ServerConsole.theme = themeData;
			} catch (err) {
				console.error(err);
				console.error("Failed to parse custom XTerm.JS theme");
				setTimeout(() => {
					toastr.error("Failed to parse custom XTerm.JS theme");
				}, 1000);
			}
		}
	}

	ServerConsole.init();
});