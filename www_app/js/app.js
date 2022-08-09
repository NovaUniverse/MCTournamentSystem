$(function () {
	if (localStorage.getItem("token") == null) {
		window.location = "/app/login/";
		return;
	} else {
		TournamentSystem.token = localStorage.getItem("token");
	}

	$(".btn-fetch-chat-log").on("click", () => TournamentSystem.fetchChatLog());

	$("#snapshot_file_upload").on("change", function () {
		let files = $("#snapshot_file_upload").get(0).files;

		//console.log(files);

		if (files.length > 0) {
			let f = files[0];

			let reader = new FileReader();

			reader.onload = (function (theFile) {
				return function (e) {
					jQuery('#snapshot_json_data').val(e.target.result);
				};
			})(f);

			reader.readAsText(f)
		} else {
			console.log("No file selected");
		}
	});

	$("#btn_import_snapshot_clear").on("click", function () {
		$("#snapshot_json_data").val("");
		$("#snapshot_file_upload").val("");
	});

	$("#btn_export_snapshot").on("click", function () {
		toastr.info("Exporting snapshot...");
		$.getJSON("/api/snapshot/export" + "?access_token=" + TournamentSystem.token, function (data) {
			if (data.error != undefined) {
				toastr.error("Snapshot export failed. Error: " + data.error);
				console.error("Snapshot export failed. Error: " + data.error);
				return;
			}

			console.log(data);

			console.log("Data collected. Downloading...");

			let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data.data, null, 4));
			let downloadAnchorNode = document.createElement('a');
			downloadAnchorNode.setAttribute("href", dataStr);
			downloadAnchorNode.setAttribute("download", "TournamentScoreSnapshot.json");
			document.body.appendChild(downloadAnchorNode); // required for firefox
			downloadAnchorNode.click();
			downloadAnchorNode.remove();

			toastr.success("Success. JSON Snapshot Download started");
		});
	});

	$("#btn_import_snapshot").on("click", function () {
		let text = $("#snapshot_json_data").val();

		if (text.length == 0) {
			toastr.error("Upload or paste the snapshot json data first");
			return;
		}

		let importedData = undefined;
		try {
			importedData = JSON.parse(text);
		} catch (err) {
			toastr.error("Failed to parse json data. Please try to upload the file again");
			return;
		}
		console.log(importedData);

		$.confirm({
			title: 'Confirm import',
			theme: 'dark',
			content: 'Importing the snapshot will overwrite the existing scores!',
			buttons: {
				confirm: function () {
					$.ajax({
						type: "POST",
						url: "/api/snapshot/import?access_token=" + TournamentSystem.token,
						data: JSON.stringify(importedData),
						success: function (data) {
							console.log(data);
							if (data.success) {
								toastr.info("Snapshot imported");
							} else {
								toastr.error("Failed to upload snapshot\n" + data.message);
							}
						},
						dataType: "json"
					});
				},
				cancel: function () { }
			}
		});
	});

	$("#btn_send_all_players_to").on("click", function () {
		sendTarget = "all";
		$('#select_server_modal').modal('show');
	});

	$("#btn_select_server_send").on("click", function () {
		let serverName = $('#select_server option:selected').val();

		console.log("Target server: " + serverName);

		if (sendTarget == "all") {
			$.getJSON("/api/send/send_players?server=" + encodeURIComponent(serverName) + "&access_token=" + TournamentSystem.token, function (data) {
				//console.log(data);
				if (data.success) {
					$('#select_server_modal').modal('hide');
					toastr.success("Success");
				} else {
					toastr.error(data.message);
				}
			});
		} else {
			$.getJSON("/api/send/send_player?server=" + encodeURIComponent(serverName) + "&player=" + encodeURIComponent(sendTarget) + "&access_token=" + TournamentSystem.token, function (data) {
				//console.log(data);
				if (data.success) {
					$('#select_server_modal').modal('hide');
					toastr.success("Success");
				} else {
					toastr.error(data.message);
				}
			});
		}
	});

	$(".btn-logout").on("click", function () {
		$.confirm({
			title: 'Confirm logout',
			theme: 'dark',
			content: 'Do you really want to logout',
			buttons: {
				confirm: function () {
					localStorage.removeItem("token");
					localStorage.removeItem("stored_credentials");
					window.location.reload();
				},
				cancel: function () { }
			}
		});
	});

	$(".btn-reset-data").on("click", function () {
		$("#broadcast_reset_data").modal("show");
	})

	$("#btn_remove_playerdata").on("click", function () {
		$.getJSON("/api/system/clear_players?access_token=" + TournamentSystem.token, function (data) {
			//console.log(data);
			if (data.success) {
				toastr.success("Player data wiped");
				$("#broadcast_reset_data").modal("hide");
			} else {
				toastr.error(data.message);
			}
		});
	});

	$("#btn_full_reset").on("click", function () {
		$.getJSON("/api/system/reset?access_token=" + TournamentSystem.token, function (data) {
			//console.log(data);
			if (data.success) {
				toastr.success("Player data wiped");
				$("#broadcast_reset_data").modal("hide");
			} else {
				toastr.error(data.message);
			}
		});
	});

	$("#btn_broadcast").on("click", function () {
		let text = $("#broadcast_text_message").val();
		TournamentSystem.broadcastMessage(text);

		$('#broadcast_modal').modal('hide');
	});

	$(".btn_broadcast").on("click", function () {
		$('#broadcast_modal').modal('show');
	});

	$(".btn-start-game").on("click", function () {
		$.confirm({
			title: 'Confirm start!',
			theme: 'dark',
			content: 'Please confirm that you want to start the game countdown',
			buttons: {
				confirm: function () {
					TournamentSystem.startGame();
				},
				cancel: function () { }
			}
		});
	});

	$(".page-link").on("click", function () {
		if ($(this).hasClass("active")) {
			return;
		}

		$(".page-link").removeClass("active");
		$(this).addClass("active");

		$(".nav-page").addClass("d-none");
		$("#" + $(this).data("page")).removeClass("d-none");
	});

	$("#btn_open_add_staff_modal").on("click", function () {
		$("#tbx_add_staff_username").val("");
		$("#add_staff_modal").modal("show");
	});

	$("#btn_search_staff_user").on("click", function () {
		$.getJSON("https://mojangapi.novauniverse.net/username_to_uuid/" + $("#tbx_add_staff_username").val(), function (data) {
			let uuid = data.uuid;

			TournamentSystem.addStaffUUID = uuid;

			$.getJSON("https://mojangapi.novauniverse.net/profile/" + uuid, function (profileData) {
				TournamentSystem.addStaffUsername = profileData.data.name;

				$("#add_staff_username").text(TournamentSystem.addStaffUsername);
				$("#add_staff_uuid").text(uuid);
				$("#add_staff_head").attr("src", "https://mc-heads.net/avatar/" + uuid)

				$("#add_staff_modal").modal("hide");
				$("#add_staff_role_modal").modal("show");
			});
		}).fail(function (e) {
			if (e.status == 404) {
				toastr.error("Could not find player")
			} else if (e.status == 400) {
				toastr.error("Invalid username")
			} else {
				toastr.error("Failed to fetch data from https://mojangapi.novauniverse.net")
			}
		});
	});

	$("#btn_add_staff_user").on("click", function () {
		let role = $("#staff_role_selector").val();

		TournamentSystem.staffTeam[TournamentSystem.addStaffUUID] = role;

		$("#add_staff_role_modal").modal("hide");

		toastr.info(TournamentSystem.addStaffUsername + " added as " + role);

		TournamentSystem.updateStaffTeam(true);
	});

	$("#add_whitelist").on("click", function () {
		$("#add_whitelist_modal").modal("show");
	});

	$("#clear_whitelist").on("click", () => TournamentSystem.showClearWhitelistConfirmation());

	$("#btn_search_whitelist_user").on("click", function () {
		let username = $("#tbx_add_whitlelist_username").val();

		$.getJSON("https://mojangapi.novauniverse.net/username_to_uuid/" + username, function (data) {
			let uuid = data.uuid;

			$.getJSON("/api/whitelist/add?access_token=" + TournamentSystem.token + "&uuid=" + uuid, function (data) {
				if (data.success) {
					toastr.info("User added");
					$("#add_whitelist_modal").modal("hide");
				} else {
					toastr.error("Failed to add user. " + data.message);
				}
			});
		}).fail(function (e) {
			if(e.status == 404) {
				toastr.error("Could not find player")
			} else if(e.status == 400) {
				toastr.error("Invalid username")
			} else {
				toastr.error("Failed to fetch data from https://mojangapi.novauniverse.net")
			}
		});
	});

	$(".set-tournament-name").on("click", function () {
		$("#new_tournament_name").val(TournamentSystem.lastData.system.tournament_name);
		$("#set_tournament_name_modal").modal("show");
	});

	$("#btn_set_tournament_name").on("click", function () {
		let name = $("#new_tournament_name").val();

		$.getJSON("/api/system/set_tournament_name?access_token=" + TournamentSystem.token + "&name=" + encodeURIComponent(name), function (data) {
			if (data.success) {
				$("#set_tournament_name_modal").modal("hide");
				toastr.info("Tournament name changed to " + name + ". Please restart the server for it to update in game");
			} else {
				toastr.error("Failed to update name. " + data.message);
			}
		});
	});

	$(".set-scoreboard-url").on("click", function () {
		$("#new_scoreboard_url").val(TournamentSystem.lastData.system.scoreboard_url);
		$("#set_scoreboard_url_modal").modal("show");
	});

	$("#btn_set_scoreboard_url").on("click", function () {
		let url = $("#new_scoreboard_url").val();

		$.getJSON("/api/system/set_scoreboard_url?access_token=" + TournamentSystem.token + "&url=" + encodeURIComponent(url), function (data) {
			if (data.success) {
				$("#set_scoreboard_url_modal").modal("hide");
				toastr.info("Scoreboard url changed to " + url + ". Please restart the server for it to update in game");
			} else {
				toastr.error("Failed to update scoreboard url. " + data.message);
			}
		});
	});

	$.getJSON("/api/system/status" + "?access_token=" + TournamentSystem.token, function (data) {
		if (data.error == "unauthorized") {
			window.location = "/app/login/";
			return;
		}

		TournamentSystem.lastData = data;

		TournamentSystem.activeServer = data.active_server;
		$("#commentator_guest_key").val(data.commentator_guest_key);

		for (let i = 0; i < data.servers.length; i++) {
			let server = data.servers[i];
			$("#select_server").append(new Option(server.name, server.name));
		}
	});

	$.getJSON("/api/staff/get_staff" + "?access_token=" + TournamentSystem.token, function (data) {
		if (!data.success) {
			toastr.error("Failed to fetch staff list" + (data.message == null ? "" : ". " + data.message));
			return;
		}

		for (let i = 0; i < data.staff_roles.length; i++) {
			let role = data.staff_roles[i];
			TournamentSystem.staffRoles.push(role);
			let newElement = $("<option></option>");
			newElement.text(role);
			newElement.attr("value", role);
			$("#staff_role_selector").append(newElement);
		}

		TournamentSystem.staffTeam = data.staff;

		TournamentSystem.updateStaffTeam();
	});

	$("#toggle_commentator_guest_key").on("click", () => TournamentSystem.toggleCommentatorKeyVisible());

	$("#copy_commentator_guest_key").on("click", function () {
		let key = $("#commentator_guest_key").val();
		ClipboardHelper.copyText(key);
		toastr.success("Commentator guest key copied to the clipboard");
	});

	$("#btn_export_summary").on("click", () => TournamentSystem.exportSummary());

	$("#btn_open_chat_log").on("click", () => TournamentSystem.openChatLog());

	setInterval(function () {
		TournamentSystem.update();
	}, 1000);
	TournamentSystem.update();
});


const TournamentSystem = {
	token: "",
	sendTarget: "all",
	lastData: null,
	addStaffUUID: null,
	addStaffUsername: null,
	staffRoles: [],
	staffTeam: {},
	commentatorKeyShown: false,
	activeServer: null,

	openChatLog: () => {
		let content = $("#chat_log").text();

		let win = window.open("", "Chat log", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes");
		win.document.body.innerText = content;
	},

	exportSummary: () => {
		toastr.info("Exporting...");
		console.log("Data export starting");
		$.getJSON("/api/system/status" + "?access_token=" + TournamentSystem.token, function (data) {
			if (data.error != undefined) {
				toastr.error("Data export failed. Error: " + data.error);
				console.error("Data export failed. Error: " + data.error);
				return;
			}

			let dataExport = {};
			let servers = [];
			let players = [];

			data.servers.forEach(server => {
				servers.push(server.name);
			});

			data.players.forEach(p => {
				let player = {};

				player["username"] = p.username;
				player["uuid"] = p.uuid;
				player["team_number"] = p.team_number;
				player["score"] = p.score;
				player["kills"] = p.kills;
				player["team_score"] = p.team_score;

				players.push(player);
			});

			dataExport["servers"] = servers;
			dataExport["teams"] = data.teams;
			dataExport["players"] = players;

			console.log("Data collected. Downloading...");
			console.log(dataExport);

			let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(dataExport, null, 4));
			let downloadAnchorNode = document.createElement('a');
			downloadAnchorNode.setAttribute("href", dataStr);
			downloadAnchorNode.setAttribute("download", "tournament_data.json");
			document.body.appendChild(downloadAnchorNode); // required for firefox
			downloadAnchorNode.click();
			downloadAnchorNode.remove();

			toastr.success("Success. JSON Download started");
		});
	},

	broadcastMessage: (text) => {
		$.getJSON("/api/system/broadcast?message=" + encodeURIComponent(text) + "&access_token=" + TournamentSystem.token, function (data) {
			if (data.success) {
				toastr.success("Message sent");
				$("#broadcast_text_message").val("");
			} else {
				toastr.error(data.message);
			}
		});
	},

	startGame: () => {
		$.getJSON("/api/game/start_game" + "?access_token=" + TournamentSystem.token, function (data) {
			//console.log(data);
			if (data.success) {
				toastr.success("Success");
			} else {
				toastr.error(data.message);
			}
		});
	},

	showClearWhitelistConfirmation: () => {
		$.confirm({
			title: 'Confirm clear!',
			theme: 'dark',
			content: 'Please confirm that you want to clear the whitelist',
			buttons: {
				confirm: () => {
					$.getJSON("/api/whitelist/clear" + "?access_token=" + TournamentSystem.token, function (data) {
						//console.log(data);
						if (data.success) {
							toastr.success("Whitelist cleared");
						} else {
							toastr.error(data.message);
						}
					});
				},
				cancel: () => { }
			}
		});
	},

	toggleCommentatorKeyVisible: () => {
		$("#commentator_guest_key").attr("type", TournamentSystem.commentatorKeyShown ? "password" : "text");
		$("#toggle_commentator_guest_key").text(TournamentSystem.commentatorKeyShown ? "Show key" : "Hide key");
		toastr.info(TournamentSystem.commentatorKeyShown ? "Guest commentator key hidden" : "Guest commentator key visible");
		TournamentSystem.commentatorKeyShown = !TournamentSystem.commentatorKeyShown;
	},

	fetchChatLog: () => {
		$("#chat_log").text("Loading...");
		$("#chat_log").attr("disabled", 1);

		$.getJSON("/api/chat/log" + "?access_token=" + TournamentSystem.token, function (data) {
			if (!data.success) {
				$("#chat_log").text(data.message);
			} else {
				let messages = "";

				data.messages.forEach(message => {
					messages += "[" + message.sent_at + "] <" + message.username + "> " + message.content + "\n";
				});

				$("#chat_log").text(messages);
			}
		});
	},

	updateStaffTeam: (update = false) => {
		let uuidList = Object.keys(TournamentSystem.staffTeam);

		for (let i = 0; i < uuidList.length; i++) {
			let uuid = uuidList[i];

			let found = false;

			$(".staff-tr").each(function () {
				if ($(this).data("uuid") == uuid) {
					found = true;
				}
			});

			if (found) {
				continue;
			}

			let newElement = $("#staff_team_tr_template").clone();

			newElement.removeAttr("id");
			newElement.addClass("staff-tr");

			newElement.attr("data-uuid", uuid);

			newElement.find(".staff-uuid").text(uuid);

			for (let j = 0; j < TournamentSystem.staffRoles.length; j++) {
				let role = TournamentSystem.staffRoles[j];
				let newOption = $("<option></option>");
				newOption.text(role);
				newOption.attr("value", role);
				newElement.find(".staff-role-selector").append(newOption);
			}

			newElement.find(".staff-avatar").attr("src", "https://mc-heads.net/avatar/" + uuid);

			newElement.find(".staff-role-selector").val(TournamentSystem.staffTeam[uuid]);
			newElement.find(".staff-role-selector").on("change", function () {
				TournamentSystem.updateStaffTeam(true);
			});

			newElement.find(".btn-remove-staff").on("click", function () {
				let uuid = $(this).parent().parent().data("uuid");
				console.debug("Remove staff " + uuid);
				delete TournamentSystem.staffTeam[uuid];
				$(this).parent().parent().remove();
				TournamentSystem.updateStaffTeam(true);
			});


			$.getJSON("https://mojangapi.novauniverse.net/profile/" + uuid, function (data) {
				newElement.find(".staff-name").text(data.name);
			});

			$("#staff_tbody").append(newElement);
		}

		if (update) {
			TournamentSystem.staffTeam = {};

			$(".staff-tr").each(function () {
				let uuid = $(this).data("uuid");
				let role = $(this).find(".staff-role-selector").val();

				TournamentSystem.staffTeam[uuid] = role;
			});

			$.ajax({
				type: "POST",
				url: "/api/staff/set_staff?access_token=" + TournamentSystem.token,
				data: JSON.stringify(TournamentSystem.staffTeam),
				success: function (data) {
					//console.log(data);
					if (data.success) {
						toastr.info("Staff updated");
					} else {
						toastr.error("Failed to upload staff settings. " + data.message);
					}
				},
				dataType: "json"
			});
			console.debug(TournamentSystem.staffTeam);
		}
	},

	updateWhitelist: () => {
		let found = [];

		$("#whitelist_entries").find(".whitelist-tr").each(function () {
			let uuid = $(this).data("uuid");

			found.push(uuid);

			if (!TournamentSystem.lastData.whitelist.includes(uuid)) {
				console.log("Removing whitelist entry: " + uuid);
				$(this).remove();
			}
		});

		TournamentSystem.lastData.whitelist.forEach(uuid => {
			if (!found.includes(uuid)) {
				console.log("Adding whitelist entry: " + uuid);

				let newElem = $("#whitelist_tr_template").clone();

				newElem.removeAttr("id");
				newElem.attr("data-uuid", uuid);
				newElem.addClass("whitelist-tr");
				newElem.find(".player-avatar").attr("src", "https://mc-heads.net/avatar/" + uuid);
				newElem.find(".uuid").text(uuid);

				$.getJSON("https://mojangapi.novauniverse.net/profile/" + uuid, function (data) {
					newElem.find(".name").text(data.name);
				});

				newElem.find(".btn-whitelist-remove").on("click", function () {
					let uuidToRemove = $(this).parent().parent().data("uuid");

					console.log("Remove clicked for " + uuidToRemove);

					$.getJSON("/api/whitelist/remove" + "?access_token=" + TournamentSystem.token + "&uuid=" + uuidToRemove, function (data) {
						//console.log(data);
						if (data.success) {
							toastr.success("Success");
						} else {
							toastr.error(data.message);
						}
					});
				});

				$("#whitelist_entries").append(newElem);
			}
		});
	},

	update: () => {
		$.getJSON("/api/system/status" + "?access_token=" + TournamentSystem.token, function (data) {
			//console.log(data);

			if (data.error == "unauthorized") {
				console.error("It seems like we are no longer authorised. Maybe we should add a real error message here");
				return;
			}

			TournamentSystem.lastData = data;

			if (TournamentSystem.activeServer != data.active_server) {
				if (data.active_server == null) {
					toastr.info("Setting active server to none");
				} else {
					toastr.info("Setting active server to " + data.active_server);
				}
				TournamentSystem.activeServer = data.active_server;
			}

			//console.log(data);

			let toRemove = [];

			$(".player-tr").each(function () {
				toRemove.push($(this).data("uuid"));
			});

			// Average ping
			let totalPing = 0;
			let totalPlayers = data.online_players.length;
			data.online_players.forEach(player => {
				totalPing += player.ping;
			});

			$("#stats_player_count").text(totalPlayers);
			if (totalPlayers == 0) {
				$("#stats_avg_ping").text("N/A");
			} else {
				$("#stats_avg_ping").text(Math.round(totalPing / totalPlayers));
			}

			$("#stats_software").text(data.system.proxy_software + " " + data.system.proxy_software_version);
			$("#stats_cores").text(data.system.cores);
			$("#stats_os").text(data.system.os_name);

			if (data.system.linux_distro == null) {
				$("#distro_info_full").hide();
			} else {
				$("#distro_info").text(data.system.linux_distro);
			}

			$("#stats_torurnament_name").text(data.system.tournament_name);
			$("#stats_scoreboard_link").text(data.system.scoreboard_url);


			data.players.forEach(player => {
				toRemove.remove(player.uuid);

				let playerElement = null;

				$(".player-tr").each(function () {
					if ($(this).data("uuid") == player.uuid) {
						playerElement = $(this);
					}
				});

				if (playerElement == null) {
					playerElement = $("#player_tr_template").clone();
					playerElement.removeAttr("id");
					playerElement.attr("data-uuid", player.uuid);
					playerElement.addClass("player-tr");

					$("#player_tbody").append(playerElement);

					playerElement.find(".player-avatar").attr("src", "https://mc-heads.net/avatar/" + player.uuid);
					playerElement.find(".player-uuid").text(player.uuid);

					playerElement.find(".player-send-to").on("click", function () {
						let uuid = $(this).parent().parent().data("uuid");

						sendTarget = uuid;

						console.log("UUID is: " + uuid);
						$('#select_server_modal').modal('show');
					});
				}

				let playerTeamInfo = $("<span></span>");
				playerTeamInfo.text("Team " + player.team_number);
				let team = data.teams.find(team => team.team_number == player.team_number);
				if (team != null) {
					if (team.display_name != ("Team " + player.team_number)) {
						playerTeamInfo.append($("<span></span>").text(" (" + team.display_name + ")").css('color', "rgb(" + team.color.r + "," + team.color.g + "," + team.color.b + ")"))
					}
				}

				playerElement.find(".player-name").text(player.username);
				playerElement.find(".player-score").text(player.score);
				playerElement.find(".player-kills").text(player.kills);
				playerElement.find(".player-team").html(playerTeamInfo);
				playerElement.find(".player-team-score").text(player.team_score);

				if (player.online) {
					playerElement.find(".player-ping").text(player.ping);
					playerElement.find(".player-server").text(player.server);

					playerElement.find(".offline-badge").hide();
					playerElement.find(".online-badge").show();

					playerElement.find(".player-send-to").attr("disabled", false);
				} else {
					playerElement.find(".player-ping").text("N/A");
					playerElement.find(".player-server").text("N/A");

					playerElement.find(".offline-badge").show();
					playerElement.find(".online-badge").hide();

					playerElement.find(".player-send-to").attr("disabled", true);
				}

			});

			toRemove.forEach((uuid) => {
				$(".player-tr").each(function () {
					if ($(this).data("uuid") == uuid) {
						$(this).remove();
					}
				});
			});

			TournamentSystem.updateWhitelist();
		});
	}
}