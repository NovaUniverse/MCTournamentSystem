var terminal = null;
var fitAddon = null;
var webGLAddon = null;
var webLinkAddon = null;
var token = null;

var lastMessageId = -1;

$(() => {
	let config = {};

	terminal = new Terminal(config);
	fitAddon = new FitAddon.FitAddon();
	webGLAddon = new WebglAddon.WebglAddon();
	webLinkAddon = new WebLinksAddon.WebLinksAddon();

	webGLAddon.onContextLoss(e => {
		console.log("WebglAddon onContextLoss");
		webGLAddon.dispose();
	});

	terminal.loadAddon(webGLAddon);
	terminal.loadAddon(fitAddon);
	terminal.loadAddon(webLinkAddon);
	terminal.open(document.getElementById("xterm"));

	fitAddon.fit();

	window.addEventListener("resize", (event) => {
		fitAddon.fit();
	});

	token = localStorage.getItem("token");
	if (token == null) {
		terminal.writeln(ConsoleColor.RED + "ERROR: Token not found. Please log in to see live chat" + ConsoleColor.RESET)
		return;
	}

	terminal.writeln(ConsoleColor.CYAN + "Checking login status..." + ConsoleColor.RESET);
	$.getJSON("/api/user/whoami?access_token=" + token, (whoamiData) => {
		console.log(whoamiData);
		if (whoamiData.logged_in) {
			terminal.writeln(ConsoleColor.GREEN + "Logged in as " + whoamiData.username + ConsoleColor.RESET);

			setInterval(() => update(), 500);
		} else {
			terminal.writeln(ConsoleColor.RED + "ERROR: Token invalod or expired. Please log in again and refresh this page" + ConsoleColor.RESET)
		}
	});
});

function update() {
	$.getJSON("/api/chat/log?access_token=" + token, (data) => {
		for (let i = 0; i < data.messages.length; i++) {
			if (i > lastMessageId) {
				lastMessageId = i;
				console.log(data.messages[i]);
				let message = data.messages[i];
				let text = ConsoleColor.CYAN + message.sent_at + " " + ConsoleColor.PURPLE + message.username + ConsoleColor.YELLOW + " > " + ConsoleColor.RESET + message.content + ConsoleColor.RESET;
				terminal.writeln(text);
			}
		}
	});
}