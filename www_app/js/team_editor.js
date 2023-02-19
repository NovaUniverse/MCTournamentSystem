const TEAM_COUNT = 12;

var addPlayerUUID = null;
var addPlayerUsername = null;

var sortDirection = true;

var showMetadata = false;

function expandTeamSize(size) {
	if (size > 12) {
		size = size - 12;
		for (let i = 1; i <= size; i++) {
			$(".player-team-select").append(new Option("Team " + (i + 12), i + 12));
		}
	}
}

function updateMetadataDisplayState() {
	$(".player-metadata-objects").each(function () {
		if (showMetadata) {
			$(this).show();
		} else {
			$(this).hide();
		}
	});
}

$(function () {
	if (localStorage.getItem("token") != null) {
		setCookie("ts_access_token", localStorage.getItem("token"), 999999);
	}

	$("#back_to_admin_li").hide();
	$("#btn_upload_team_data").hide();

	for (let i = 1; i <= TEAM_COUNT; i++) {
		$(".player-team-select").append(new Option("Team " + i, i));
	}

	$("#btn_add_player_open").on("click", function () {
		$('#add_player_modal').modal('show');
		setTimeout(function () {
			$("#add_player_username").trigger('focus');
		}, 500);
	});

	$("#btn_search_player").on("click", function () {
		searchPlayer();
	});

	$("#add_player_username").on("input propertychange paste", function () {
		$("#player_preview_div").hide();
		$("#btn_add_player").prop('disabled', true);
		addPlayerUUID = null;
		addPlayerUsername = null;
	});

	$("#btn_add_player").on("click", function () {
		if ($('.player-tr[data-uuid="' + addPlayerUUID + '"]').length > 0) {
			toastr.error("That player has already been added");
		} else {
			$("#btn_add_player").prop('disabled', true);
			//console.log("add player " + addPlayerUUID + " " + addPlayerUsername);

			$("#add_player_username").val("");
			$("#player_preview_div").hide();

			addPlayer(addPlayerUUID, addPlayerUsername, -1);

			$("#player_preview_div").hide();
			addPlayerUUID = null;
			addPlayerUsername = null;
			$('#add_player_modal').modal('hide');

			$("#player_table").stupidtable_build();
			sortTable();

			setTimeout(function () {
				$("#btn_add_player_open").trigger('focus');
			}, 100);
		}
	});

	$("#add_player_username").on("keypress", function (e) {
		if (e.key == "Enter") {
			searchPlayer();
		}
	});

	$("#btn_update_usernames").on("click", function () {
		$.confirm({
			title: 'Please confirm',
			theme: 'dark',
			content: 'Do you want to fetch the latest username for all players',
			buttons: {
				confirm: function () {
					updateUsernames();
				},
				cancel: function () { }
			}
		});
	});

	$("#col_team_number").on("click", function () {
		sortDirection = !sortDirection;
		sortTable();
	});

	$("#btn_export_json").on("click", function () {
		exportJSON();
	});

	$("#btn_download_json").on("click", function () {
		var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent($("#json_output").text());
		var downloadAnchorNode = document.createElement('a');
		downloadAnchorNode.setAttribute("href", dataStr);
		downloadAnchorNode.setAttribute("download", "teams.json");
		document.body.appendChild(downloadAnchorNode); // required for firefox
		downloadAnchorNode.click();
		downloadAnchorNode.remove();
	});

	$("#player_preview_div").hide();
	$(".sort-direction").hide();

	$("#player_table").stupidtable();

	$("#player_table").on('aftertablesort', function (event, data) {
		$(".sort-direction").hide();
		$(".sort-direction-" + data.direction).show();
	});

	$("#btn_upload_team_data").on("click", function () {
		$.ajax({
			type: "POST",
			url: "/api/v1/team/upload_team?access_token=" + localStorage.getItem("token"),
			data: $("#json_output").text(),
			success: function (data) {
				console.log(data);
				toastr.info("Team uploaded to TournamentSystem");
			},
			error: (xhr, ajaxOptions, thrownError) => {
				if (xhr.status == 0 || xhr.status == 503) {
					toastr.error("Failed to communicate with backend server");
					return;
				}

				if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
					toastr.error(xhr.responseJSON.message);
					return;
				}

				toastr.error("Upload failed. Download the data, reload the page and try again");
			},
			dataType: "json"
		});
	});

	sortTable();

	setCookie("exported_team_data", "", 0);

	$.getJSON("/api/v1/system/status", function (data) {
		console.log("It seems like the team editor is running on the same web server as TournamentSystem");
		$("#back_to_admin_li").show();
		$("#btn_upload_team_data").show();

		if (data.system.team_size > 12) {
			expandTeamSize(data.system.team_size);
		}

		$.getJSON("/api/v1/team/export_team_data", function (data) {
			data.teams_data.forEach(element => {
				addPlayer(element.uuid, element.username, element.team_number, element.metadata);
			});

			toastr.info("Team data loaded from TournamentSystem");
		}).fail((e) => {
			if (e.status == 0 | e.status == 503) {
				toastr.error("Failed to communicate with backend server");
			} else {
				toastr.error("Could not fetch team data from tournament system. Please check that you are logged in");
			}
		});
	}).fail((e) => {
		if (e.status == 0 | e.status == 503) {
			toastr.error("Failed to communicate with backend server");
		} else {
			toastr.error("Running in offline mo");
		}
	});

	$("#link_back_to_admin").on("click", function () {
		if (confirm("Any unsaved changes will be lost!")) {
			window.location = "/app/";
		}
	});

	$("#link_import_team").on("click", function () {
		$("#import_team_modal").modal('show');
	});

	$("#json_file_upload").on("change", function () {
		let files = $("#json_file_upload").get(0).files;

		//console.log(files);

		if (files.length > 0) {
			let f = files[0];

			let reader = new FileReader();

			reader.onload = (function (theFile) {
				return function (e) {
					jQuery('#team_json_data').val(e.target.result);
				};
			})(f);

			reader.readAsText(f)
		} else {
			console.log("No file selected");
		}
	});

	$("#btn_import_team").on("click", function () {
		try {
			let jsonData = JSON.parse($("#team_json_data").val());

			loadData(jsonData);

			$("#import_team_modal").modal('hide');

			$("#team_json_data").val("");
			$("#json_file_upload").val("");
		} catch (err) {
			toastr.error("Invalid JSON provided. Check if the data is valid and try again");
			console.error(err);
		}
	});

	$("#btn_cancel_import_team").on("click", function () {
		$("#team_json_data").val("");
		$("#json_file_upload").val("");
	});

	if (localStorage.getItem("editor_show_metadata") == "true") {
		showMetadata = true;
	}

	if (showMetadata) {
		$("#showMetadataCheckbox").attr("checked", true);
	}
	updateMetadataDisplayState();

	$("#showMetadataCheckbox").on("change", function () {
		showMetadata = $("#showMetadataCheckbox").is(":checked");
		localStorage.setItem("editor_show_metadata", showMetadata ? "true" : "false");
		updateMetadataDisplayState();
	});

	$(".hidden-until-loaded").removeClass("hidden-until-loaded");
});

function setCookie(cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
	var expires = "expires=" + d.toUTCString();
	document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
	var name = cname + "=";
	var ca = document.cookie.split(';');
	for (var i = 0; i < ca.length; i++) {
		var c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}

function addPlayer(uuid, username, teamNumber, metadata = {}) {
	let newPlayer = $("#player_tr_template").clone();

	newPlayer.removeAttr('id');
	newPlayer.addClass("player-tr");

	newPlayer.attr("data-uuid", uuid);
	newPlayer.attr("data-username", username);

	newPlayer.find(".player-uuid").text(uuid);
	newPlayer.find(".player-username").text(username);
	newPlayer.find(".metadata-input").val(JSON.stringify(metadata));

	newPlayer.find(".player-avatar").attr("src", "https://crafatar.com/avatars/" + uuid);

	newPlayer.find(".btn-remove-player").on("click", function () {
		$(this).parent().parent().remove();
	});

	newPlayer.find(".player-team-select").on("change", function () {
		let value = $(this).children("option:selected").val();

		$(this).parent().attr("data-sort-value", value);
		$(this).parent().parent().attr("data-team-number", value);

		$(this).parent().updateSortVal(value);

		sortTable();
	});

	$("#player_thead").append(newPlayer);

	newPlayer.find(".player-team-select").val(teamNumber).change();

	updateMetadataDisplayState();
}

function searchPlayer() {
	let username = $("#add_player_username").val();

	if (username.length > 0) {
		$.getJSON("https://mojangapi.novauniverse.net/username_to_uuid/" + username, function (data) {
			let uuid = data.uuid
			//console.log("The uuid of " + username + " is " + uuid);
			$.getJSON("https://mojangapi.novauniverse.net/profile/" + uuid, function (profileData) {
				let realUsername = profileData.name;

				//console.log("The real username is " + realUsername);

				if (uuid == "980dbf7d-0904-426f-9c02-d9af3c099fb2") {
					toastr.warning("Warning: This player has to be in team 4. NO EXCEPTIONS");
				}

				$("#preview_image").attr("src", "https://crafatar.com/avatars/" + uuid);
				$("#preview_uuid").text(uuid);
				$("#preview_username").text(realUsername);

				addPlayerUUID = uuid;
				addPlayerUsername = realUsername;

				$("#btn_add_player").prop('disabled', false);

				$("#player_preview_div").show();

				$("#btn_add_player").trigger('focus');
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
	} else {
		toastr.error("Please provide a username");
	}
}

function exportJSON() {
	let data = getData();
	console.log(data);
	var hlt = hljs.highlight('json', print_r(data));
	$("#json_output").html(hlt.value)

	$("#json_export_modal").modal('show');
}

function print_r(object, html) {
	if (html) return '<pre>' + JSON.stringify(object, null, 4) + '</pre>';
	else return JSON.stringify(object, null, 4);
}


function loadData(data) {
	$(".player-tr").remove();

	for (let i = 0; i < data.length; i++) {
		let player = data[i];

		addPlayer(player.uuid, player.username, player.team_number, data.metadata);
	}
}

function updateUsernames() {
	toastr.info("Fetching latest username for all players");
	$(".player-tr").each(function () {
		let uuid = $(this).attr("data-uuid");

		let element = $(this);

		$.getJSON("https://mojangapi.novauniverse.net/profile/" + uuid, function (profileData) {
			let realUsername = profileData.username;
			element.find(".player-username").text(realUsername);
			element.attr("data-username", realUsername);
		});
	});
}

function getData() {
	let data = [];

	$(".player-tr").each(function () {
		let uuid = $(this).attr("data-uuid");
		let username = $(this).attr("data-username");
		let teamNumber = $(this).attr("data-team-number");
		let metadata = "" + $(this).find(".metadata-input").val();

		if (metadata.length == 0) {
			metadata = "{}";
		}

		metadata = JSON.parse(metadata);

		if (teamNumber == -1) {
			return;
		}

		data.push({
			uuid: uuid,
			username: username,
			team_number: teamNumber,
			metadata: metadata
		});
	});

	return data;
}

function sortTable() {
	$("#col_team_number").stupidsort(sortDirection ? "asc" : "desc");
}

function fixUUID(uuid) {
	return uuid.substr(0, 8) + "-" + uuid.substr(8, 4) + "-" + uuid.substr(12, 4) + "-" + uuid.substr(16, 4) + "-" + uuid.substr(20);
}