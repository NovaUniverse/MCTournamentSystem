const ServerConsole = {
	activeServer: null,
	lastMessageId: 0,
	lastSessionId: "00000000-0000-0000-0000-000000000000",
	pollingIntervalId: null,
	terminal: null,
	fitAddon: null,
	webGLAddon: null,
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
				$.getJSON("/api/servers/logs?server=" + this.activeServer + "&access_token=" + TournamentSystem.token, (data) => {
					if (data.success) {
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
					} else {
						console.error("Failed to fetch logs. " + data.error);
					}
				});
			}
		}, 500);

		this.terminal.writeln("Connecting to " + server + "...");

		$.getJSON("/api/servers/log_session_id?server=" + this.activeServer + "&access_token=" + TournamentSystem.token, (data) => {
			if (data.success) {
				this.lastSessionId = data.session_id;
				this.lastMessageId = -1;
				if (data.session_id == undefined) {
					this.terminal.writeln("Server not yet started. Logs will start to display as soon as the server goes online");
				} else {
					this.terminal.writeln("Session id is: " + this.lastSessionId);
				}
				this.ready = true;
			} else {
				this.terminal.writeln("Failed to fetch session id. " + data.error);
			}
		});
	},

	closeConsole() {
		if (this.pollingIntervalId == null) {
			return;
		}
		console.log("Closing console");
		$("#serverConsoleModal").modal("hide");
		clearInterval(this.pollingIntervalId);
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

		this.webGLAddon.onContextLoss(e => {
			console.log("WebglAddon onContextLoss");
			this.webGLAddon.dispose();
		});

		this.terminal.loadAddon(this.webGLAddon);
		this.terminal.loadAddon(this.fitAddon);
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
					url: "/api/servers/run_command?server=" + this.activeServer + "&access_token=" + TournamentSystem.token,
					data: command,
					contentType: 'text/plain',
					success: (data) => {
						let response = JSON.parse(data);
						if (response.success) {
							toastr.info("Ran command: " + command + " on server " + this.activeServer);
						} else {
							toastr.error("Failed to execute command. " + response.message);
							$("#console_input_field").val(command);
						}
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