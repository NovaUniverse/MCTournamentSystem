var logged_in_user_username = "UwU";
var user_permissions = [];
var offlineModeHead = "c06f8906-4c8a-4911-9c29-ea1dbd1aab82"; // MHF_Steve
var checkedOfflineModePlayers = [];
var offlineSkinCache = new Map();

$(function () {
	if (localStorage.getItem("token") == null) {
		window.location = "/app/login/";
		return;
	} else {
		TournamentSystem.token = localStorage.getItem("token");
		setCookie("ts_access_token", TournamentSystem.token, 999999);
	}

	$.getJSON("/api/v1/service_providers", (data) => {
		TournamentSystem.skinRenderAPIUrl = data.skin_render_api;
		if (TournamentSystem.skinRenderAPIUrl != null) {
			console.log("Skin render api url: " + TournamentSystem.skinRenderAPIUrl);
		}
	});

	$.getJSON("/api/v1/system/mode", (data) => {
		if (data.offline_mode) {
			setInterval(() => {
				//console.log("Requesting skin renewal");
				checkedOfflineModePlayers = [];
			}, 1000 * 20);

			setInterval(() => {
				if (TournamentSystem.skinRenderAPIUrl == null) {
					return;
				}

				$(".player-avatar").each(function () {
					let username = $(this).data("username");

					if (username == undefined) {
						return;
					}

					let src = $(this).attr("src");

					if (!checkedOfflineModePlayers.includes(username)) {
						checkedOfflineModePlayers.push(username);
						$.getJSON("/api/skinrestorer/get_user_skin?username=" + username, (data) => {
							if (data.has_skin) {
								let skinData = JSON.parse(atob(data.skin_data));
								if (skinData.textures != null) {
									if (skinData.textures.SKIN != null) {
										let skinUrl = skinData.textures.SKIN.url;
										//console.log(username + " has the skin " + skinUrl);
										offlineSkinCache.set(username, skinUrl);
									}
								}
							}
						});
					}

					if (offlineSkinCache.has(username)) {
						let url = TournamentSystem.skinRenderAPIUrl + "/from_image/face/skin.png?resolution=256&url=" + offlineSkinCache.get(username);
						if (src != url) {
							//console.log("Changing skin url of " + username + " to " + url);
							$(this).attr("src", url);
						}
					}
				})
			}, 1500);
		}
	});

	$.getJSON("/api/v1/system/security_check", (data) => {
		if (data.dev_mode) {
			$("#alert_dev_mode").removeClass("d-none");
		}

		if (data.default_login_warning) {
			$("#alert_default_password").removeClass("d-none");
		}
	});

	$(".hidden-integration").hide();

	$(".meow").on("click", () => {
		setCatMode(true);
	});

	$(".reload-dynamic-config-url").on("click", () => {
		console.log("Reloading dynamic config");
		toastr.info("Reloading dynamic config");

		$.ajax({
			type: "POST",
			url: "/api/v1/system/dynamicconfig/reload",
			success: (data) => {
				if (data.success) {
					toastr.success("Dynamic config reloaded");
				} else {
					toastr.error(data.message);
					console.error(data.message);
				}
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 401) {
					toastr.error("Not authenticated. Try reloading the page");
				} else if (xhr.status == 403) {
					toastr.error("You dont have permission to reload the dynamic config");
				} else if (xhr.status == 500) {
					toastr.error("An error occured while reloading the dynamic config");
				} else {
					toastr.error("Could not update dynamic config due to an unknown error");
				}
				console.error(xhr);
			},
			dataType: "json"
		});
	});

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
		$.getJSON("/api/v1/snapshot/export", function (data) {

			console.log(data);
			console.log("Data collected. Downloading...");

			let dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(data, null, 4));
			let downloadAnchorNode = document.createElement('a');
			downloadAnchorNode.setAttribute("href", dataStr);
			downloadAnchorNode.setAttribute("download", "TournamentScoreSnapshot.json");
			document.body.appendChild(downloadAnchorNode); // required for firefox
			downloadAnchorNode.click();
			downloadAnchorNode.remove();

			toastr.success("Success. JSON Snapshot Download started");
		}).fail((e) => {
			if (xhr.status == 0 || xhr.status == 503) {
				toastr.error("Failed to communicate with backend server");
				return;
			}

			if (xhr.status == 500) {
				toastr.error("An error occured while exporting score snapshot. " + xhr.responseJSON.message);
			} else if (xhr.status == 401 || xhr.status == 403) {
				toastr.error("Not authenticated. Try reloading the page");
			} else {
				toastr.error("Could not export score snapshot. " + xhr.statusText);
			}
			console.error(xhr);
		});
	});

	$(".btn-reload-page").on("click", () => window.location.reload());

	$(".shutdown-proxy-button").on("click", () => {
		$.confirm({
			title: 'Confirm shutdown',
			theme: 'dark',
			content: 'WARNING! You are about to shut down the proxy server. This will disconnect all players, kill any managed servers and make the web UI unavailable!',
			buttons: {
				confirm: function () {
					TournamentSystem.shutdown();
				},
				cancel: function () { }
			}
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
						url: "/api/v1/snapshot/import",
						data: JSON.stringify(importedData),
						success: function (data) {
							toastr.info("Snapshot imported");
						},
						error: (xhr, ajaxOptions, thrownError) => {
							console.error(xhr);
							if (xhr.status == 0 || xhr.status == 503) {
								toastr.error("Failed to communicate with backend server");
								return;
							}

							if (xhr.status == 401) {
								toastr.error("Not authenticated. Try reloading the page");
							} else if (xhr.status == 400) {
								toastr.error(xhr.responseJSON.message);
							} else if (xhr.status == 500) {
								toastr.error("An error occured. " + xhr.responseJSON.message);
							} else if (xhr.status == 403) {
								toastr.error("You dont have permission to import score snapshots");
							} else {
								toastr.error("Clould not import score snapshot. " + xhr.statusText);
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
			$.ajax({
				type: "POST",
				url: "/api/v1/send/send_players?server=" + encodeURIComponent(serverName),
				success: function (data) {
					$('#select_server_modal').modal('hide');
					toastr.success("Success");
				},
				error: (xhr, ajaxOptions, thrownError) => {
					console.error(xhr);
					if (xhr.status == 0 || xhr.status == 503) {
						toastr.error("Failed to communicate with backend server");
						return;
					}

					if (xhr.status == 401) {
						toastr.error("Not authenticated. Try reloading the page");
					} else if (xhr.status == 400) {
						toastr.error(xhr.responseJSON.message);
					} else if (xhr.status == 500) {
						toastr.error("An error occured. " + xhr.responseJSON.message);
					} else if (xhr.status == 403) {
						toastr.error("You dont have permission to send players");
					} else {
						toastr.error("Failed to send players. " + xhr.statusText);
					}
				},
				dataType: "json"
			});
		} else {
			$.ajax({
				type: "POST",
				url: "/api/v1/send/send_player?server=" + encodeURIComponent(serverName) + "&player=" + encodeURIComponent(sendTarget),
				success: function (data) {
					$('#select_server_modal').modal('hide');
					toastr.success("Success");
				},
				error: (xhr, ajaxOptions, thrownError) => {
					console.error(xhr);
					if (xhr.status == 0 || xhr.status == 503) {
						toastr.error("Failed to communicate with backend server");
						return;
					}

					if (xhr.status == 401) {
						toastr.error("Not authenticated. Try reloading the page");
					} else if (xhr.status == 400) {
						toastr.error(xhr.responseJSON.message);
					} else if (xhr.status == 500) {
						toastr.error("An error occured. " + xhr.responseJSON.message);
					} else if (xhr.status == 403) {
						toastr.error("You dont have permission to send players");
					} else {
						toastr.error("Failed to send player. " + xhr.statusText);
					}
				},
				dataType: "json"
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
		$("#reset_confirmation").prop("checked", false);
		$("#broadcast_reset_data").modal("show");
	});

	$("#btn_full_reset").on("click", function () {
		if ($("#reset_confirmation").is(":checked")) {
			$.ajax({
				type: "DELETE",
				url: "/api/v1/system/reset",
				success: (data) => {
					toastr.success("Data wiped");
					$("#broadcast_reset_data").modal("hide");
				},
				error: (xhr, ajaxOptions, thrownError) => {
					if (xhr.status == 0 || xhr.status == 503) {
						toastr.error("Failed to communicate with backend server");
						return;
					}

					if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
						toastr.error(xhr.responseJSON.message);
					} else {
						toastr.error("Failed to remove data due to an unknown error");
					}
					console.error(xhr);
				},
				dataType: "json"
			});
		} else {
			toastr.warning("Please verify that you want to delete the data");
		}
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
		if ($("#cbx_add_staff_offlinemode").is(':checked')) {
			$.getJSON("/api/v1/utils/offline_username_to_uuid?username=" + $("#tbx_add_staff_username").val(), function (data) {
				let uuid = data.uuid;

				TournamentSystem.addStaffUUID = uuid;
				TournamentSystem.addStaffUsername = $("#tbx_add_staff_username").val();
				TournamentSystem.addStaffOffline = true;

				$("#add_staff_username").text(TournamentSystem.addStaffUsername);
				$("#add_staff_uuid").text(uuid);
				$("#add_staff_head").attr("src", "https://mc-heads.net/avatar/" + offlineModeHead);

				$("#add_staff_modal").modal("hide");
				$("#add_staff_role_modal").modal("show");
			}).fail(function (e) {
				toastr.error("Failed to communicate with backend server to get uuid from username");
			});
		} else {
			$.getJSON(ServiceProviders.MojangAPIProxy + "/username_to_uuid/" + $("#tbx_add_staff_username").val(), function (data) {
				let uuid = data.uuid;

				TournamentSystem.addStaffUUID = uuid;
				TournamentSystem.addStaffOffline = false;

				$.getJSON(ServiceProviders.MojangAPIProxy + "/profile/" + uuid, function (profileData) {
					TournamentSystem.addStaffUsername = profileData.name;

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
					toastr.error("Failed to fetch data from mojang api")
				}
			});
		}
	});

	$("#btn_add_staff_user").on("click", function () {
		let role = $("#staff_role_selector").val();

		TournamentSystem.staffTeam[TournamentSystem.addStaffUUID] = {
			role: role,
			username: TournamentSystem.addStaffUsername,
			uuid: TournamentSystem.addStaffUUID,
			offline_mode: TournamentSystem.addStaffOffline,
		};

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

		if ($("#cbx_add_whitelist_offlinemode").is(':checked')) {
			$.getJSON("/api/v1/utils/offline_username_to_uuid?username=" + username, function (data) {
				let uuid = data.uuid;
				$.ajax({
					type: "PUT",
					url: "/api/v1/whitelist/users?uuid=" + uuid + "&username=" + username + "&offline_mode=true",
					success: (data) => {
						toastr.info("User added");
						$("#add_whitelist_modal").modal("hide");
					},
					error: (xhr, ajaxOptions, thrownError) => {
						if (xhr.status == 0 || xhr.status == 503) {
							toastr.error("Failed to communicate with backend server");
							return;
						}

						if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
							toastr.error(xhr.responseJSON.message);
						} else {
							toastr.error("Failed to add user to whitelist due to an unknown error");
						}
						console.error(xhr);
					},
					dataType: "json"
				});
			}).fail(function (e) {
				toastr.error("Failed to communicate with backend server to get uuid from username");
			});
		} else {
			$.getJSON(ServiceProviders.MojangAPIProxy + "/username_to_uuid/" + username, function (data) {
				let uuid = data.uuid;
				$.ajax({
					type: "PUT",
					url: "/api/v1/whitelist/users?uuid=" + uuid,
					success: (data) => {
						toastr.info("User added");
						$("#add_whitelist_modal").modal("hide");
					},
					error: (xhr, ajaxOptions, thrownError) => {
						if (xhr.status == 0 || xhr.status == 503) {
							toastr.error("Failed to communicate with backend server");
							return;
						}

						if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
							toastr.error(xhr.responseJSON.message);
						} else {
							toastr.error("Failed to add user to whitelist due to an unknown error");
						}
						console.error(xhr);
					},
					dataType: "json"
				});
			}).fail(function (e) {
				if (e.status == 404) {
					toastr.error("Could not find player")
				} else if (e.status == 400) {
					toastr.error("Invalid username")
				} else {
					toastr.error("Failed to fetch data from mojang api")
				}
			});
		}
	});

	$(".set-tournament-name").on("click", function () {
		if (TournamentSystem.lastData != null) {
			$("#new_tournament_name").val(TournamentSystem.lastData.system.tournament_name);
		}
		$("#set_tournament_name_modal").modal("show");
	});

	$("#btn_set_tournament_name").on("click", function () {
		let name = $("#new_tournament_name").val();

		$.ajax({
			type: "POST",
			url: "/api/v1/system/settings/tournament_name",
			data: name,
			success: (data) => {
				$("#set_tournament_name_modal").modal("hide");
				toastr.info("Tournament name changed to " + name + ". Please restart the server for it to update in game");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to update name due to an unknown error");
				}
				console.error(xhr);
			},
			dataType: "json"
		});
	});

	$(".set-scoreboard-url").on("click", function () {
		if (TournamentSystem.lastData != null) {
			$("#new_scoreboard_url").val(TournamentSystem.lastData.system.scoreboard_url);
		}
		$("#set_scoreboard_url_modal").modal("show");
	});

	$("#btn_set_scoreboard_url").on("click", function () {
		let url = $("#new_scoreboard_url").val();

		$.ajax({
			type: "POST",
			url: "/api/v1/system/settings/scoreboard_url",
			data: url,
			contentType: 'text/plain',
			success: (data) => {
				$("#set_scoreboard_url_modal").modal("hide");
				toastr.info("Scoreboard url changed to " + url + ". Please restart the server for it to update in game");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to update scoreboard url due to an unknown error");
				}
				console.error(xhr);
			},
			dataType: "json"
		});
	});

	$(".set-motd").on("click", function () {
		if (TournamentSystem.lastData != null) {
			$("#new_motd_value").val(TournamentSystem.lastData.system.motd);
		}
		$("#set_motd_modal").modal("show");
	});

	$("#btn_set_motd").on("click", function () {
		let motd = $("#new_motd_value").val();

		$.ajax({
			type: "POST",
			url: "/api/v1/system/settings/motd",
			data: motd,
			contentType: 'text/plain',
			success: (data) => {
				$("#set_motd_modal").modal("hide");
				toastr.info("MOTD Updated");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to update motd due to an unknown error");
				}
				console.error(xhr);
			},
			dataType: "json"
		});
	});

	$.getJSON("/api/v1/system/status", function (data) {
		TournamentSystem.lastData = data;

		TournamentSystem.activeServer = data.active_server;
		TournamentSystem.isOfflineMode = data.system.offline_mode;

		if (TournamentSystem.isOfflineMode) {
			console.warn("We are running in offline mode");
		}

		for (let i = 0; i < data.servers.length; i++) {
			let server = data.servers[i];
			$("#select_server").append(new Option(server.name, server.name));
		}

		if (data.internet_cafe_settings.ggrock.enabled) {
			// Timeout prevents preloader from getting stuck if service is unreachable
			setTimeout(() => {
				console.log("Setting up iframe from ggrock integration");
				$("#integration_link_ggrock").show();
				$("#ggrock_integration_iframe").attr("src", data.internet_cafe_settings.ggrock.url);
			}, 3000);
		}

		console.log(data);

		logged_in_user_username = data.user.username;
		user_permissions = data.user.permissions;

		console.log("Logged in as " + logged_in_user_username);
		console.log("Permissions: ");
		console.log(user_permissions);

		$("[data-disable-if-missing-permission]").each(function () {
			let requiredPermission = $(this).data("disable-if-missing-permission");
			if (!hasPermission(requiredPermission)) {
				console.log("User does not have permission: " + requiredPermission);
				$(this).attr("disabled", true);
			}
		});

		$("[data-hide-if-missing-permission]").each(function () {
			let requiredPermission = $(this).data("hide-if-missing-permission");
			if (!hasPermission(requiredPermission)) {
				console.log("User does not have permission: " + requiredPermission);
				$(this).hide();
			}
		});

		if (hasPermission("VIEW_COMMENTATOR_GUEST_KEY")) {
			console.log("Fetching commentator guest key");
			$.getJSON("/api/v1/commentator/get_guest_key", (guestKeyData) => {
				$("#commentator_guest_key").val(guestKeyData.commentator_guest_key);
			}).fail(function (e) {
				toastr.error("Failed to fetch commentator guest key");
			});
		}

		TournamentSystem.update();
		TournamentSystem.updateServers();

		setInterval(() => TournamentSystem.updateServers(), 1000);
		setInterval(() => TournamentSystem.update(), 1000);

		$.getJSON("/api/v1/staff", (staffData) => {
			for (let i = 0; i < staffData.staff_roles.length; i++) {
				let role = staffData.staff_roles[i];
				TournamentSystem.staffRoles.push(role);
				let newElement = $("<option></option>");
				newElement.text(role);
				newElement.attr("value", role);
				$("#staff_role_selector").append(newElement);
			}

			TournamentSystem.staffTeam = staffData.staff;

			TournamentSystem.updateStaffTeam();
		}).fail(function (e) {
			toastr.error("Failed to fetch staff list");
		});
	}).fail(function (e) {
		if (e.status == 403 || e.status == 401) {
			window.location = "/app/login/";
		} else {
			toastr.error("Something went wrong while trying to get initial data");
			console.error(e);
		}
	});

	$("#toggle_commentator_guest_key").on("click", () => TournamentSystem.toggleCommentatorKeyVisible());

	$("#copy_commentator_guest_key").on("click", function () {
		let key = $("#commentator_guest_key").val();
		ClipboardHelper.copyText(key);
		toastr.success("Commentator guest key copied to the clipboard");
	});

	$("#btn_export_summary").on("click", () => TournamentSystem.exportSummary());

	$("#btn_open_chat_log").on("click", () => TournamentSystem.openChatLog());

	$("#btn_open_live_chat").on("click", () => TournamentSystem.openLiveChat());

	$(".btn_next_minigame").on("click", () => {
		$("#next_minigame_value").val(TournamentSystem.lastData.next_minigame);
		$("#next_minigame_model").modal("show");
	});

	$("#btn_clear_next_minigame").on("click", () => TournamentSystem.clearNextMinigame());

	$("#btn_set_next_mingame").on("click", () => TournamentSystem.setNextMinigame($("#next_minigame_value").val()));
});

function hasPermission(permission) {
	return user_permissions.includes(permission);
}


const TournamentSystem = {
	token: "",
	sendTarget: "all",
	lastData: null,
	addStaffUUID: null,
	addStaffUsername: null,
	addStaffOffline: false,
	staffRoles: [],
	staffTeam: {},
	commentatorKeyShown: false,
	activeServer: null,
	lastServerData: [],
	isOfflineMode: false,
	skinRenderAPIUrl: null,

	showServerState: (serverName) => {
		let server = TournamentSystem.lastServerData.find(s => s.name == serverName);
		if (server == null) {
			toastr.error("Could not find server data for " + serverName);
			return;
		}

		let stateReport = server.last_state_report;

		$("#server_info_software").text("Unknown");
		$("#server_info_java_version").text("Unknown");

		console.log(server);
		if (stateReport.software != null) {
			if (stateReport.software.bukkit != null) {
				$("#server_info_software").text(stateReport.software.bukkit.bukkit_version + " " + stateReport.software.bukkit.version);
			}

			if (stateReport.software.java != null) {
				$("#server_info_java_version").text(stateReport.software.java.version + " " + stateReport.software.java.vm_name);
			}
		}

		$("#serverInfoModalTitle").text("Server info: " + server.name);
		$("#server_info_plugins").find("tr").remove();
		$("#server_info_modules").find("tr").remove();

		if (stateReport.plugins != null) {
			stateReport.plugins.forEach(plugin => {
				//console.log(plugin);
				let newElement = $("<tr></tr>");
				newElement.append(
					$("<td></td>")
						.text(plugin.name)
				);
				newElement.append(
					$("<td></td>")
						.text(plugin.version)
				);

				if (plugin.enabled == false) {
					newElement.addClass("table-danger");
				}

				newElement.attr("title", (plugin.enabled == false ? "Disabled" : "Enabled") + ". Authors: " + plugin.authors);

				$("#server_info_plugins").append(newElement);
			});
		}

		if (stateReport.modules != null) {
			stateReport.modules.forEach(novamodule => {
				//console.log(novamodule);
				let newElement = $("<tr></tr>");
				newElement.append(
					$("<td></td>")
						.text(novamodule.name)
				);

				let moduleState = $("<td></td>");
				moduleState.text(novamodule.enabled ? "Enabled" : "Disabled")
				moduleState.addClass(novamodule.enabled ? "table-success" : "table-danger");

				newElement.append(moduleState);

				$("#server_info_modules").append(newElement);
			});
		}

		$("#serverInfoModal").modal("show");
		//console.log(server);
	},

	openChatLog: () => {
		let content = $("#chat_log").text();

		let win = window.open("", "Chat log", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes");
		win.document.body.innerText = content;
	},

	openLiveChat: () => {
		window.open("/app/live_chat/", "Live chat", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes");
	},

	exportSummary: () => {
		toastr.info("Exporting...");
		console.log("Data export starting");
		$.getJSON("/api/v1/system/status", function (data) {
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
		}).fail((e) => {
			console.error(e);
			if (xhr.status == 0 || xhr.status == 503) {
				toastr.error("Failed to communicate with backend server");
				return;
			}

			if (xhr.status == 401 || xhr.status == 403) {
				toastr.error("Not authenticated. Try reloading the page");
				return;
			}

			toastr.error("Export failed for unknown reason");
		});
	},

	broadcastMessage: (text) => {
		$.ajax({
			type: "POST",
			url: "/api/v1/system/broadcast",
			data: text,
			contentType: 'text/plain',
			success: (data) => {
				toastr.success("Message sent");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}


				if (xhr.status == 400) {
					toastr.error("Server failed to decode message");
					return;
				}

				if (xhr.status == 403) {
					toastr.error("You dont have permission to broadcast messages");
					return;
				}

				if (xhr.status == 401) {
					toastr.error("Not authenticated. Try reloading the page");
					return;
				}

				toastr.error("Could not broadcast message due to an error. " + xhr.statusText);
			},
			dataType: "text"
		});
	},

	shutdown: () => {
		$.ajax({
			type: "POST",
			url: "/api/v1/system/shutdown",
			success: (data) => {
				toastr.info("Shutting down proxy server");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 403) {
					toastr.error("You dont have permission to shut down the server");
					return;
				}

				if (xhr.status == 401) {
					toastr.error("Not authenticated. Try reloading the page");
					return;
				}

				toastr.error("Could not shutdown server due to an error. " + xhr.statusText);
			}
		});
	},

	clearNextMinigame: () => {
		$.ajax({
			type: "POST",
			url: "/api/v1/next_minigame",
			success: (data) => {
				toastr.success("Next minigame cleared");
				$("#next_minigame_model").modal("hide");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to set next game due to an unknown error");
				}
				console.error(xhr);
			},
			dataType: "json"
		});
	},

	setNextMinigame: (name) => {
		$.ajax({
			type: "POST",
			url: "/api/v1/next_minigame",
			data: name,
			contentType: 'text/plain',
			success: (data) => {
				toastr.success("Next minigame cleared");
				$("#next_minigame_model").modal("hide");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to set next game due to an unknown error");
				}
				console.error(xhr);
			},
			dataType: "text"
		});
	},

	startGame: () => {
		$.ajax({
			type: "POST",
			url: "/api/v1/game/start_game",
			success: (data) => {
				toastr.success("Success");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 409) {
					toastr.error("An online player is needed to be able to send the start packet to the server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to start game due to an unknown error");
					console.error(xhr);
					console.error(ajaxOptions);
					console.error(thrownError);
				}
			},
			dataType: "json"
		});
	},

	activateTrigger: (triggerId, sessionId) => {
		$.ajax({
			type: "POST",
			url: "/api/v1/game/trigger?triggerId=" + triggerId + "&sessionId=" + sessionId,
			success: (data) => {
				toastr.success("Success");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 409) {
					toastr.error("An online player is needed to be able to send the trigger packet to the server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
				} else {
					toastr.error("Failed to activate trigger due to an unknown error");
					console.error(xhr);
					console.error(ajaxOptions);
					console.error(thrownError);
				}
			},
			dataType: "json"
		});
	},

	showClearWhitelistConfirmation: () => {
		$.confirm({
			title: 'Confirm clear!',
			theme: 'dark',
			content: 'Please confirm that you want to clear the whitelist',
			buttons: {
				confirm: () => {
					$.ajax({
						type: "POST",
						url: "/api/v1/whitelist/clear",
						success: (data) => {
							toastr.success("Whitelist cleared");
						},
						error: (xhr, ajaxOptions, thrownError) => {
							if (xhr.status == 0 || xhr.status == 503) {
								toastr.error("Failed to communicate with backend server");
								return;
							}

							if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
								toastr.error(xhr.responseJSON.message);
							} else {
								toastr.error("Failed to clear whitelist due to an unknown error");
								console.error(xhr);
								console.error(ajaxOptions);
								console.error(thrownError);
							}
						},
						dataType: "json"
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

		$.ajax({
			type: "GET",
			url: "/api/v1/chat/log",
			success: (data) => {
				console.log(data);
				let messages = "";

				data.messages.forEach(message => {
					messages += "[" + message.sent_at + "] <" + message.username + "> " + message.content + "\n";
				});

				$("#chat_log").text(messages);
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					$("#chat_log").text("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					$("#chat_log").text(xhr.responseJSON.message);
				} else {
					$("#chat_log").text("Failed to open chat log due to an unknown error");
					console.error(xhr);
					console.error(ajaxOptions);
					console.error(thrownError);
				}
			},
			dataType: "json"
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

			let userData = TournamentSystem.staffTeam[uuid];

			let newElement = $("#staff_team_tr_template").clone();

			newElement.removeAttr("id");
			newElement.addClass("staff-tr");

			newElement.attr("data-uuid", uuid);

			newElement.find(".staff-offline").text(userData.offline_mode ? "yes" : "no");

			if (userData.offline_mode) {
				newElement.find("td").addClass("text-warning");
			}

			newElement.find(".staff-uuid").text(uuid);

			for (let j = 0; j < TournamentSystem.staffRoles.length; j++) {
				let role = TournamentSystem.staffRoles[j];
				let newOption = $("<option></option>");
				newOption.text(role);
				newOption.attr("value", role);
				newElement.find(".staff-role-selector").append(newOption);
			}

			newElement.find(".staff-avatar").attr("src", "https://mc-heads.net/avatar/" + (userData.offline_mode ? offlineModeHead : uuid));

			newElement.find(".staff-role-selector").val(userData.role);

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


			newElement.find(".staff-name").text(userData.username);

			if (!userData.offline_mode) {
				$.getJSON(ServiceProviders.MojangAPIProxy + "/profile/" + uuid, function (data) {
					newElement.find(".staff-name").text(data.name);
				});
			}

			$("#staff_tbody").append(newElement);
		}

		if (update) {
			TournamentSystem.staffTeam = {};

			$(".staff-tr").each(function () {
				let uuid = $(this).data("uuid");
				let username = $(this).find(".staff-name").text();
				let offlineMode = $(this).find(".staff-offline").text() == "yes";
				let role = $(this).find(".staff-role-selector").val();

				TournamentSystem.staffTeam[uuid] = {
					uuid: uuid,
					role: role,
					username: username,
					offline_mode: offlineMode
				}
			});

			$.ajax({
				type: "PUT",
				url: "/api/v1/staff",
				data: JSON.stringify(TournamentSystem.staffTeam),
				success: function (data) {
					toastr.info("Staff updated");
				},
				error: (xhr) => {
					if (xhr.status == 0 || xhr.status == 503) {
						toastr.error("Failed to communicate with backend server");
						return;
					}

					if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
						toastr.error(xhr.responseJSON.message);
					} else {
						toastr.error("Failed to update staff list for unknown reason");
					}
					console.error(xhr);
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

			if (!TournamentSystem.lastData.whitelist.some(e => e.uuid == uuid)) {
				console.log("Removing whitelist entry: " + uuid);
				$(this).remove();
			}
		});

		TournamentSystem.lastData.whitelist.forEach(entry => {
			if (!found.includes(entry.uuid)) {
				console.log("Adding whitelist entry: " + entry.uuid);

				let newElement = $("#whitelist_tr_template").clone();

				newElement.removeAttr("id");
				newElement.attr("data-uuid", entry.uuid);
				newElement.addClass("whitelist-tr");
				newElement.find(".player-avatar").attr("src", "https://mc-heads.net/avatar/" + (entry.offline_mode ? offlineModeHead : entry.uuid));
				newElement.find(".uuid").text(entry.uuid);

				newElement.find(".offline_mode").text(entry.offline_mode ? "yes" : "no");

				if (entry.offline_mode) {
					newElement.find("td").addClass("text-warning");
					newElement.find(".name").text(entry.username);
				} else {
					$.getJSON(ServiceProviders.MojangAPIProxy + "/profile/" + entry.uuid, function (data) {
						newElement.find(".name").text(data.name);
					});
				}

				if (!hasPermission("MANAGE_WHITELIST")) {
					newElement.find(".btn-whitelist-remove").attr("disabled", true);
				}

				newElement.find(".btn-whitelist-remove").on("click", function () {
					let uuidToRemove = $(this).parent().parent().data("uuid");

					console.log("Remove clicked for " + uuidToRemove);

					$.ajax({
						type: "DELETE",
						url: "/api/v1/whitelist/users?uuid=" + uuidToRemove,
						success: (data) => {
							toastr.success("Success");
						},
						error: (xhr, ajaxOptions, thrownError) => {
							if (xhr.status == 0 || xhr.status == 503) {
								toastr.error("Failed to communicate with backend server");
								return;
							}

							if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
								toastr.error(xhr.responseJSON.message);
							} else {
								toastr.error("Failed to remove user whitelist due to an unknown error");
								console.error(xhr);
								console.error(ajaxOptions);
								console.error(thrownError);
							}
						},
						dataType: "json"
					});
				});

				$("#whitelist_entries").append(newElement);
			}
		});
	},

	updateServers: () => {
		$.getJSON("/api/v1/servers/get_servers", (data) => {
			let servers = [];
			data.servers.forEach(server => {
				servers.push(server);
			});

			let displayedServers = [];

			$(".server-col").each(function () {
				let name = $(this).data("server-name");
				if (servers.filter(t => t.name == name).length == 0) {
					$(this).remove();
				}
				displayedServers.push(name);
			});

			TournamentSystem.lastServerData = data.servers;

			servers.forEach(server => {
				if (!displayedServers.includes(server.name)) {
					let newElement = $("#server_sample").clone();
					newElement.removeAttr("id");
					newElement.addClass("server-col");
					newElement.attr("data-server-name", server.name);

					newElement.find(".server-name").text(server.name);

					newElement.find(".start-server-button").attr("data-server-name", server.name);
					newElement.find(".kill-server-button").attr("data-server-name", server.name);
					newElement.find(".stop-server-button").attr("data-server-name", server.name);
					newElement.find(".get-server-logs-button").attr("data-server-name", server.name);
					newElement.find(".start-server-console-button").attr("data-server-name", server.name);
					newElement.find(".get-server-info").attr("data-server-name", server.name);


					if (!hasPermission("MANAGE_SERVERS")) {
						newElement.find(".start-server-button").attr("disabled", true);
						newElement.find(".kill-server-button").attr("disabled", true);
						newElement.find(".stop-server-button").attr("disabled", true);
						newElement.find(".get-server-logs-button").attr("disabled", true);
						newElement.find(".start-server-console-button").attr("disabled", true);
					}

					newElement.find(".start-server-console-button").on("click", function () {
						let serverName = $(this).data("server-name");
						ServerConsole.openConsole(serverName);
					});

					newElement.find(".get-server-info").on("click", function () {
						let serverName = $(this).data("server-name");
						TournamentSystem.showServerState(serverName);
					});

					newElement.find(".start-server-button").on("click", function () {
						let serverName = $(this).data("server-name");
						$.confirm({
							title: 'Confirm start',
							theme: 'dark',
							content: 'Do you want to start the server ' + serverName,
							buttons: {
								confirm: function () {
									$.ajax({
										type: "POST",
										url: "/api/v1/servers/start?server=" + serverName,
										data: $("#json_output").text(),
										success: function (data) {
											toastr.success("Success");
										},
										error: (xhr, ajaxOptions, thrownError) => {
											if (xhr.status == 0 || xhr.status == 503) {
												toastr.error("Backend communication failure");
											} else if (xhr.status == 409) {
												toastr.error("Server already running");
											} else if (xhr.status == 500) {
												toastr.error("Failed to start server. " + xhr.responseJSON.message);
											} else if (xhr.status == 404) {
												toastr.error("Server not found");
											} else if (xhr.status == 401) {
												toastr.error("You are not logged in. Please refresh the page");
											} else if (xhr.status == 403) {
												toastr.error("You dont have permission to start this server");
											} else {
												toastr.error("Failed to start server from unknown reasons");
											}
											console.error(xhr);
										},
										dataType: "json"
									});
								},
								cancel: function () { }
							}
						});
					});

					newElement.find(".kill-server-button").on("click", function () {
						let serverName = $(this).data("server-name");
						$.confirm({
							title: 'Confirm stop',
							theme: 'dark',
							content: 'Do you want to stop the server ' + serverName,
							buttons: {
								confirm: function () {
									$.ajax({
										type: "POST",
										url: "/api/v1/servers/stop?server=" + serverName,
										success: function (data) {
											toastr.success("Success");
										},
										error: (xhr, ajaxOptions, thrownError) => {
											if (xhr.status == 0 || xhr.status == 503) {
												toastr.error("Backend communication failure");
											} else if (xhr.status == 409) {
												toastr.error("Server not running");
											} else if (xhr.status == 404) {
												toastr.error("Server not found");
											} else if (xhr.status == 500) {
												toastr.error("Failed to kill server. " + xhr.responseJSON.message);
											} else if (xhr.status == 401) {
												toastr.error("You are not logged in. Please refresh the page");
											} else if (xhr.status == 403) {
												toastr.error("You dont have permission to kill this server");
											} else {
												toastr.error("Failed to kill server from unknown reasons");
											}
											console.error(xhr);
										},
										dataType: "json"
									});
								},
								cancel: function () { }
							}
						});
					});

					newElement.find(".stop-server-button").on("click", function () {
						let serverName = $(this).data("server-name");
						$.confirm({
							title: 'Confirm stop',
							theme: 'dark',
							content: 'Do you want to send the stop command to the server ' + serverName,
							buttons: {
								confirm: function () {
									$.ajax({
										type: "POST",
										url: "/api/v1/servers/run_command?server=" + serverName,
										data: "stop",
										contentType: 'text/plain',
										success: (data) => {
											toastr.info("Ran command: stop on server " + serverName);
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

											if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
												toastr.error("Failed to execute command. " + xhr.responseJSON.message);
											} else {
												toastr.error("Failed to execute command due to unknown error");
												toastr.error("Could not run server command due to an error. " + xhr.statusText);
											}
											$("#console_input_field").val(command);
										}
									});
								},
								cancel: function () { }
							}
						});
					});

					newElement.find(".get-server-logs-button").on("click", function () {
						let serverName = $(this).data("server-name");
						$.getJSON("/api/v1/servers/logs?server=" + serverName, (response) => {
							if (response.success) {
								toastr.success("Opening logs in popup");
								let content = "";

								response.log_data.forEach(line => {
									content += line + "\n";
								});

								let win = window.open("", "Server log", "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=yes");
								win.document.body.innerText = content;
							} else {
								toastr.error(response.message);
							}
						}).fail((e) => {
							if (e.status == 0 | e.status == 503) {
								toastr.error("Failed to communicate with backend server");
							} else if (e.status == 404) {
								toastr.error("Server not found");
							} else if (xhr.status == 401) {
								toastr.error("You are not logged in. Please refresh the page");
							} else if (xhr.status == 403) {
								toastr.error("You dont have permission to to do this");
							} else {
								toastr.error("Could not fetch server logs for unknown reason");
							}
						});;
					});

					$("#server_list").append(newElement);
				}
			});

			$(".server-col").each(function () {
				let name = $(this).data("server-name");
				let server = servers.filter(s => s.name == name)[0];

				if (server == null) {
					return;
				}

				if (server.is_running) {
					$(this).find(".server-running-status").removeClass("bg-danger");
					$(this).find(".server-running-status").addClass("bg-success");
					$(this).find(".server-running-status").text("Running");
				} else {
					$(this).find(".server-running-status").addClass("bg-danger");
					$(this).find(".server-running-status").removeClass("bg-success");
					$(this).find(".server-running-status").text("Not running");
				}

				if (hasPermission("MANAGE_SERVERS")) {
					if (server.is_running) {
						$(this).find(".kill-server-button").attr("disabled", false);
						$(this).find(".stop-server-button").attr("disabled", false);
						$(this).find(".start-server-button").attr("disabled", true);
					} else {
						$(this).find(".kill-server-button").attr("disabled", true);
						$(this).find(".stop-server-button").attr("disabled", true);
						$(this).find(".start-server-button").attr("disabled", false);
					}
				}

				/*$(this).find(".trigger-flags").text(trigger.flags.join(", "));

				if (trigger.ticks_left == null) {
					$(this).find(".trigger-ticks-left").hide();
					$(this).find(".trigger-seconds-left").hide();
				} else {
					$(this).find(".trigger-ticks-left").show();
					$(this).find(".trigger-seconds-left").show();
					$(this).find(".trigger-ticks-left").text(trigger.ticks_left);
					$(this).find(".trigger-seconds-left").text(Math.trunc(trigger.ticks_left / 20));
				}*/
			});
		});
	},

	update: () => {
		$.getJSON("/api/v1/system/status", (data) => {
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

			if (data.system.cores == 1) {
				$("#stats_cores").addClass("text-danger");
				$("#stats_cores").attr("title", "Running the tournament system on a single core VM or device is not recommended");
			}

			$("#stats_public_ip").text(data.system.public_ip);

			if (data.system.dynamic_config_url == null) {
				$("#dynamic_config_url").text("[Disabled]");
				$("#dynamic_config_url").addClass("text-danger");
			} else {
				$("#dynamic_config_url").text(data.system.dynamic_config_url);
				$("#dynamic_config_url").removeClass("text-danger");
			}

			if (data.system.linux_distro == null) {
				$("#distro_info_full").hide();
			} else {
				$("#distro_info").text(data.system.linux_distro);
			}

			$("#stats_torurnament_name").text(data.system.tournament_name);
			$("#stats_scoreboard_link").text(data.system.scoreboard_url);

			if (data.next_minigame == undefined) {
				$("#span_next_minigame").addClass("text-danger");
				$("#span_next_minigame").removeClass("text-info");
				$("#span_next_minigame").text("none");
			} else {
				$("#span_next_minigame").removeClass("text-danger");
				$("#span_next_minigame").addClass("text-info");
				$("#span_next_minigame").text(data.next_minigame);
			}

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

					playerElement.find(".player-avatar").attr("src", "https://mc-heads.net/avatar/" + (TournamentSystem.isOfflineMode ? offlineModeHead : player.uuid));
					playerElement.find(".player-avatar").attr("data-username", player.username);
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

					if (hasPermission("SEND_PLAYERS")) {
						playerElement.find(".player-send-to").attr("disabled", false);
					} else {
						playerElement.find(".player-send-to").attr("disabled", true);
					}
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

			let triggers = [];

			data.player_server_data.forEach(psd => {
				if (psd.metadata.triggers != null) {
					psd.metadata.triggers.forEach(trigger => {
						if (triggers.filter(t => t.session_id == trigger.session_id && t.name == trigger.name).length == 0) {
							triggers.push(trigger);
						}
					});
				}
			});

			let displayedTriggers = [];

			$(".trigger-col").each(function () {
				let session_id = $(this).data("trigger-sid");
				let name = $(this).data("trigger-name");
				if (triggers.filter(t => t.session_id == session_id && t.name == name).length == 0) {
					$(this).remove();
				}
				displayedTriggers.push(session_id + "_" + name);
			});

			triggers.forEach(trigger => {
				if (!displayedTriggers.includes(trigger.session_id + "_" + trigger.name)) {
					let newElement = $("#trigger_sample").clone();
					newElement.removeAttr("id");
					newElement.addClass("trigger-col");
					newElement.attr("data-trigger-sid", trigger.session_id);
					newElement.attr("data-trigger-name", trigger.name);


					newElement.find(".trigger-server-name").text(trigger.server);
					newElement.find(".trigger-name").text(trigger.name);
					newElement.find(".trigger-description").text(trigger.description);
					if ((trigger.description + "").trim().length == 0) {
						newElement.find(".trigger-description").hide();
						newElement.find(".description-hr").hide();
					}

					if (!hasPermission("MANAGE_TRIGGERS")) {
						newElement.find(".trigger-button").remove();
					} else {
						newElement.find(".trigger-button").attr("data-trigger-name", trigger.name);
						newElement.find(".trigger-button").attr("data-trigger-sid", trigger.session_id);
						newElement.find(".trigger-button").on("click", function () {
							let name = $(this).data("trigger-name");
							let sessionId = $(this).data("trigger-sid");
							TournamentSystem.activateTrigger(name, sessionId);
						});
					}

					$("#game_trigger_list").append(newElement);
				}
			});

			$(".trigger-col").each(function () {
				let session_id = $(this).data("trigger-sid");
				let name = $(this).data("trigger-name");
				let trigger = triggers.filter(t => t.session_id == session_id && t.name == name)[0];

				$(this).find(".trigger-activation-count").text(trigger.trigger_count);

				if (trigger.running == null) {
					$(this).find(".trigger-running-status").hide();
				} else {
					$(this).find(".trigger-running-status").show();
					if (trigger.running) {
						$(this).find(".trigger-running-status").removeClass("bg-danger");
						$(this).find(".trigger-running-status").addClass("bg-success");
						$(this).find(".trigger-running-status").text("Running");
					} else {
						$(this).find(".trigger-running-status").addClass("bg-danger");
						$(this).find(".trigger-running-status").removeClass("bg-success");
						$(this).find(".trigger-running-status").text("Stopped");
					}
				}

				$(this).find(".trigger-flags").text(trigger.flags.join(", "));

				if (trigger.ticks_left == null) {
					$(this).find(".trigger-ticks-left").hide();
					$(this).find(".trigger-seconds-left").hide();
				} else {
					$(this).find(".trigger-ticks-left").show();
					$(this).find(".trigger-seconds-left").show();
					$(this).find(".trigger-ticks-left").text(trigger.ticks_left);
					$(this).find(".trigger-seconds-left").text(Math.trunc(trigger.ticks_left / 20));
				}

				//console.log(trigger);
			});
		}).fail((e) => {
			if (e.status == 401 || e.status == 403) {
				$("#disconnected_modal").modal("show");
			}
		});
	}
}