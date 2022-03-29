var sendTarget = "all";
var token = "";
var lastData = undefined;

var addStaffUUID = null;
var addStaffUsername = null;
var staffRoles = [];
var staffTeam = {};

var licenseWarningShown = false;

$(function () {
	if (localStorage.getItem("token") == null) {
		window.location = "/app/login/";
		return;
	} else {
		token = localStorage.getItem("token");
	}

	$("#btn_send_all_players_to").on("click", function () {
		sendTarget = "all";
		$('#select_server_modal').modal('show');
	});

	$("#btn_select_server_send").on("click", function () {
		let serverName = $('#select_server option:selected').val();

		console.log("Target server: " + serverName);

		if (sendTarget == "all") {
			$.getJSON("/api/send/send_players?server=" + encodeURIComponent(serverName) + "&access_token=" + token, function (data) {
				console.log(data);
				if (data.success) {
					$('#select_server_modal').modal('hide');
					toastr.success("Success");
				} else {
					toastr.error(data.message);
				}
			});
		} else {
			$.getJSON("/api/send/send_player?server=" + encodeURIComponent(serverName) + "&player=" + encodeURIComponent(sendTarget) + "&access_token=" + token, function (data) {
				console.log(data);
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
		localStorage.removeItem("token");
		window.location.reload();
	});

	$(".btn-reset-data").on("click", function () {
		$("#broadcast_reset_data").modal("show");
	})

	$("#btn_remove_playerdata").on("click", function () {
		$.getJSON("/api/system/clear_players?access_token=" + token, function (data) {
			console.log(data);
			if (data.success) {
				toastr.success("Player data wiped");
				$("#broadcast_reset_data").modal("hide");
			} else {
				toastr.error(data.message);
			}
		});
	});

	$("#btn_full_reset").on("click", function () {
		$.getJSON("/api/system/reset?access_token=" + token, function (data) {
			console.log(data);
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
		$.getJSON("/api/system/broadcast?message=" + encodeURIComponent(text) + "&access_token=" + token, function (data) {
			if (data.success) {
				toastr.success("Message sent");
				$("#broadcast_text_message").val("");
			} else {
				toastr.error(data.message);
			}
		});

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
					$.getJSON("/api/game/start_game" + "?access_token=" + token, function (data) {
						console.log(data);
						if (data.success) {
							toastr.success("Success");
						} else {
							toastr.error(data.message);
						}
					});
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
		$.getJSON("https://api.minetools.eu/uuid/" + $("#tbx_add_staff_username").val(), function (data) {
			if (data.id == null) {
				toastr.error("User not found");
				return;
			}

			let uuid = expandUUID(data.id);

			addStaffUUID = uuid;
			addStaffUsername = data.name;

			$("#add_staff_username").text(data.name);
			$("#add_staff_uuid").text(uuid);
			$("#add_staff_head").attr("src", "https://mc-heads.net/avatar/" + uuid)

			$("#add_staff_modal").modal("hide");
			$("#add_staff_role_modal").modal("show");
		});
	});

	$("#btn_add_staff_user").on("click", function () {
		let role = $("#staff_role_selector").val();

		staffTeam[addStaffUUID] = role;

		$("#add_staff_role_modal").modal("hide");

		toastr.info(addStaffUsername + " added as " + role);

		updateStaffTeam(true);
	});

	$("#add_whitelist").on("click", function () {
		$("#add_whitelist_modal").modal("show");
	});

	$("#clear_whitelist").on("click", function () {
		$.confirm({
			title: 'Confirm clear!',
			theme: 'dark',
			content: 'Please confirm that you want to clear the whitelist',
			buttons: {
				confirm: function () {
					$.getJSON("/api/whitelist/clear" + "?access_token=" + token, function (data) {
						console.log(data);
						if (data.success) {
							toastr.success("Whitelist cleared");
						} else {
							toastr.error(data.message);
						}
					});
				},
				cancel: function () { }
			}
		});
	});

	$("#btn_search_whitelist_user").on("click", function () {
		let username = $("#tbx_add_whitlelist_username").val();

		$.getJSON("https://api.minetools.eu/uuid/" + username, function (data) {
			if (data.id == null) {
				toastr.error("User not found");
				return;
			}

			let uuid = expandUUID(data.id);

			$.getJSON("/api/whitelist/add?access_token=" + token + "&uuid=" + uuid, function (data) {
				if (data.success) {
					toastr.info("User added");
					$("#add_whitelist_modal").modal("hide");
				} else {
					toastr.error("Failed to add user. " + data.message);
				}
			});
		});
	});

	$(".set-tournament-name").on("click", function () {
		$("#new_tournament_name").val(lastData.system.tournament_name);
		$("#set_tournament_name_modal").modal("show");
	});

	$("#btn_set_tournament_name").on("click", function () {
		let name = $("#new_tournament_name").val();

		$.getJSON("/api/system/set_tournament_name?access_token=" + token + "&name=" + encodeURIComponent(name), function (data) {
			if (data.success) {
				$("#set_tournament_name_modal").modal("hide");
				toastr.info("Tournament name changed to " + name + ". Please restart the server for it to update in game");
			} else {
				toastr.error("Failed to update name. " + data.message);
			}
		});
	});

	$(".set-scoreboard-url").on("click", function () {
		$("#new_scoreboard_url").val(lastData.system.scoreboard_url);
		$("#set_scoreboard_url_modal").modal("show");
	});

	$("#btn_set_scoreboard_url").on("click", function () {
		let url = $("#new_scoreboard_url").val();

		$.getJSON("/api/system/set_scoreboard_url?access_token=" + token + "&url=" + encodeURIComponent(url), function (data) {
			if (data.success) {
				$("#set_scoreboard_url_modal").modal("hide");
				toastr.info("Scoreboard url changed to " + url + ". Please restart the server for it to update in game");
			} else {
				toastr.error("Failed to update scoreboard url. " + data.message);
			}
		});
	});

	$.getJSON("/api/system/status" + "?access_token=" + token, function (data) {
		if (data.error == "unauthorized") {
			window.location = "/app/login/";
			return;
		}

		lastData = data;

		for (let i = 0; i < data.servers.length; i++) {
			let server = data.servers[i];
			$("#select_server").append(new Option(server.name, server.name));
		}
	});

	$.getJSON("/api/staff/get_staff" + "?access_token=" + token, function (data) {
		if (!data.success) {
			toastr.error("Failed to fetch staff list" + (data.message == null ? "" : ". " + data.message));
			return;
		}

		for (let i = 0; i < data.staff_roles.length; i++) {
			let role = data.staff_roles[i];
			staffRoles.push(role);
			let newElement = $("<option></option>");
			newElement.text(role);
			newElement.attr("value", role);
			$("#staff_role_selector").append(newElement);
		}

		staffTeam = data.staff;

		updateStaffTeam();
	});

	$("#btn_export_summary").on("click", function () {
		toastr.info("Exporting...");
		console.log("Data export starting");
		$.getJSON("/api/system/status" + "?access_token=" + token, function (data) {
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
	});

	setInterval(function () {
		update();
	}, 1000);
	update();
});

function updateStaffTeam(update = false) {
	let uuidList = Object.keys(staffTeam);

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

		for (let j = 0; j < staffRoles.length; j++) {
			let role = staffRoles[j];
			let newOption = $("<option></option>");
			newOption.text(role);
			newOption.attr("value", role);
			newElement.find(".staff-role-selector").append(newOption);
		}

		newElement.find(".staff-avatar").attr("src", "https://mc-heads.net/avatar/" + uuid);

		newElement.find(".staff-role-selector").val(staffTeam[uuid]);
		newElement.find(".staff-role-selector").on("change", function () {
			updateStaffTeam(true);
		});

		newElement.find(".btn-remove-staff").on("click", function () {
			let uuid = $(this).parent().parent().data("uuid");
			console.debug("Remove staff " + uuid);
			delete staffTeam[uuid];
			$(this).parent().parent().remove();
			updateStaffTeam(true);
		});


		$.getJSON("https://api.minetools.eu/profile/" + uuid, function (data) {
			newElement.find(".staff-name").text(data.decoded.profileName);
		});

		$("#staff_tbody").append(newElement);
	}

	if (update) {
		staffTeam = {};

		$(".staff-tr").each(function () {
			let uuid = $(this).data("uuid");
			let role = $(this).find(".staff-role-selector").val();

			staffTeam[uuid] = role;
		});

		$.ajax({
			type: "POST",
			url: "/api/staff/set_staff?access_token=" + token,
			data: JSON.stringify(staffTeam),
			success: function (data) {
				//console.log(data);
				if (data.success) {
					toastr.info("Staff updated");
				} else {
					toastr.error("Failed to uppload staff settings. " + data.message);
				}
			},
			dataType: "json"
		});
		console.debug(staffTeam);
	}
}

function updateWhitelist() {
	let found = [];

	$("#whitelist_entries").find(".whitelist-tr").each(function () {
		let uuid = $(this).data("uuid");

		found.push(uuid);

		if (!lastData.whitelist.includes(uuid)) {
			console.log("Removing whitelist entry: " + uuid);
			$(this).remove();
		}
	});

	lastData.whitelist.forEach(uuid => {
		if (!found.includes(uuid)) {
			console.log("Adding whitelist entry: " + uuid);

			let newElem = $("#whitelist_tr_template").clone();

			newElem.removeAttr("id");
			newElem.attr("data-uuid", uuid);
			newElem.addClass("whitelist-tr");
			newElem.find(".player-avatar").attr("src", "https://mc-heads.net/avatar/" + uuid);
			newElem.find(".uuid").text(uuid);

			$.getJSON("https://api.minetools.eu/profile/" + uuid, function (data) {
				newElem.find(".name").text(data.decoded.profileName);
			});

			newElem.find(".btn-whitelist-remove").on("click", function () {
				let uuidToRemove = $(this).parent().parent().data("uuid");

				console.log("Remove clicked for " + uuidToRemove);

				$.getJSON("/api/whitelist/remove" + "?access_token=" + token + "&uuid=" + uuidToRemove, function (data) {
					console.log(data);
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
}

function showLicenseWarning(text) {
	if (licenseWarningShown) {
		return;
	}
	licenseWarningShown = true;

	toastr.warning(text);

	$("#license_warning_body").text(text);
	$("#license_warning").modal("show");
}

function update() {
	$.getJSON("/api/system/status" + "?access_token=" + token, function (data) {
		console.log(data);

		if (data.error == "unauthorized") {
			console.error("It seems like we are no longer authorised. Maybe we should add a real error message here");
			return;
		}

		lastData = data;

		//console.log(data);

		let toRemove = [];

		$(".player-tr").each(function () {
			toRemove.push($(this).data("uuid"));
		});


		if (data.license.is_valid) {
			if (data.license.is_expired) {
				$(".license-status").text("License expired");
				showLicenseWarning("License expired. Please go the the support tab and contact us to extend your license");
			} else {
				if (data.license.is_demo) {
					$(".license-status").text("Demo license");

					showLicenseWarning("You are using a demo license. Please go the the support tab and contact us to get a license");
				} else {
					$(".license-status").text("License valid");
				}
			}
			$(".licensed-to").text(data.license.owner);
			$(".licensed-expires").text(data.license.expires_at);
		} else {
			$(".license-status").text("Invalid license");

			showLicenseWarning("Invalid license key. Please go the the support tab and contact us to extend your license");
		}

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

			playerElement.find(".player-name").text(player.username);
			playerElement.find(".player-score").text(player.score);
			playerElement.find(".player-kills").text(player.kills);
			playerElement.find(".player-team").text("Team " + player.team_number);
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

		updateWhitelist();
	});
}