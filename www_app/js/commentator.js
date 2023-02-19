var accessKey = "";

const distanceMatters = ["survivalgames", "spleef", "skywars"];
const lowHealthMatters = ["survivalgames", "skywars"];

$(() => {
	setTimeout(() => {
		accessKey = localStorage.getItem("commentator_key");
		//console.log(accessKey);
		if (accessKey == null) {
			console.log("No access token in localstorage");
			showLogin();
			toastr.error("Please login with your commentator token provided by the staff");
		} else {
			console.log("Found key in localstorage");
			console.log("Validating key");
			$.getJSON("/api/v1/system/status?commentator_key=" + accessKey, function (data) {
				console.log("Key is valid");
				setInterval(() => {
					update();
				}, 500);
				update();
			}).fail((e) => {
				if (e.status == 401 || e.status == 403) {
					console.log("Key is invalid");
					window.localStorage.removeItem("commentator_key");
					window.location.reload();
				} else {
					toastr.error("Server communication failure. Please refresh the page");
				}
			});
		}
	}, 100);

	$("#btn_login").on("click", function () {
		let key = $("#commentator_key").val();
		$.getJSON("/api/v1/system/status?commentator_key=" + key, function (data) {
			localStorage.setItem("commentator_key", key);
			window.location.reload();
		}).fail((e) => {
			if (e.status == 401 || e.status == 403) {
				toastr.error("Invalid key");
			} else {
				toastr.error("Server communication failure");
			}
		});
	});

	$(".btn-logout").on("click", function () {
		$.confirm({
			title: 'Confirm logout',
			theme: 'dark',
			content: 'Do you really want to logout',
			buttons: {
				confirm: function () {
					localStorage.removeItem("commentator_key");
					window.location.reload();
				},
				cancel: function () { }
			}
		});
	});
});

function update() {
	$.getJSON("/api/v1/system/status?commentator_key=" + accessKey, function (data) {
		let anyInGame = false;

		let found = [];

		let topTeamId = -1;
		let topTeamScore = 0;

		let topPlayerName = null;
		let topPlayerScore = 0;

		data.players.forEach(player => {
			if (player.team_score > topTeamScore) {
				topTeamScore = player.team_score;
				topTeamId = player.team_number;
			}

			if (player.score > topPlayerScore) {
				topPlayerScore = player.score;
				topPlayerName = player.username;
			}
		});

		if (topTeamId == -1) {
			$("#top_team").text("N/A");
		} else {
			let topTeamInfo = $("<span></span>");
			topTeamInfo.text("Team " + topTeamId);
			let team = data.teams.find(team => team.team_number == topTeamId);
			if (team != null) {
				if (team.display_name != ("Team " + topTeamId)) {
					topTeamInfo.append($("<span></span>").text(" (" + team.display_name + ")").css('color', "rgb(" + team.color.r + "," + team.color.g + "," + team.color.b + ")"))
				}
			}

			$("#top_team").html(topTeamInfo);
		}

		if (topPlayerName == null) {
			$("#top_player").text("N/A");
		} else {
			$("#top_player").text(topPlayerName + " with " + topPlayerScore + " points");
		}

		data.player_server_data.forEach(player => {
			if (player.game_enabled && player.in_game) {
				anyInGame = true;
			}
		});

		$(".player").each(function () {
			let uuid = $(this).data("uuid");

			found.push(uuid);
		});

		data.player_server_data.forEach(player => {
			let highRisk = false;

			if (anyInGame) {
				if (!player.game_enabled || !player.in_game) {
					return;
				}
			}

			let playerElement = null;

			//console.log(player);

			$(".player").each(function () {
				if ($(this).data("uuid") == player.uuid) {
					playerElement = $(this);
				}
			});

			if (playerElement == null) {
				playerElement = $("#player_template").clone();
				playerElement.removeAttr("id");
				playerElement.attr("data-uuid", player.uuid);
				playerElement.addClass("player");

				playerElement.find(".player-head").attr("src", "https://mc-heads.net/avatar/" + player.uuid);

				playerElement.on("click", function () {
					let uuid = $(this).data("uuid");

					$.ajax({
						type: "POST",
						url: "/api/v1/commentator/tp?commentator_key=" + accessKey + "&target=" + uuid,
						success: (data) => {
							toastr.success("Player data wiped");
							$("#broadcast_reset_data").modal("hide");
						},
						error: (xhr, ajaxOptions, thrownError) => {
							if (xhr.status == 0 || xhr.status == 503) {
								toastr.error("Failed to communicate with backend server");
								return;
							}

							if (xhr.status == 405 || xhr.status == 403 || xhr.status == 401 || xhr.status == 500) {
								toastr.error("Failed to teleport to player. " + xhr.responseJSON.message);
							} else {
								toastr.error("Failed to remove data due to an unknown error");
							}
							console.error(xhr);
						},
						dataType: "json"
					});
				});

				$("#player_container").append(playerElement);
			}

			if (player.health <= 4) {
				if (lowHealthMatters.includes(player.server)) {
					highRisk = true;
				}
			}

			if (player.metadata.tnttag_tagged != undefined) {
				if (player.metadata.tnttag_tagged) {
					highRisk = true;
				}
			}

			let playerExtraData = data.players.find(p2 => p2.uuid == player.uuid);

			//console.log(playerExtraData);

			if (playerExtraData != null) {
				playerElement.find(".player-score").text(playerExtraData.score);
				playerElement.find(".player-team-score").text(playerExtraData.team_score);

				//console.log(playerExtraData);

				let playerTeamInfo = $("<span></span>");
				playerTeamInfo.text("Team " + playerExtraData.team_number);
				let team = data.teams.find(team => team.team_number == playerExtraData.team_number);
				if (team != null) {
					if (team.display_name != ("Team " + playerExtraData.team_number)) {
						playerTeamInfo.append($("<span></span>").text(" (" + team.display_name + ")").css('color', "rgb(" + team.color.r + "," + team.color.g + "," + team.color.b + ")"))
					}
				}

				playerElement.find(".player-team").html(playerTeamInfo);
			} else {
				playerElement.find(".player-score").text("N/A");
				playerElement.find(".player-team-score").text("N/A");
				playerElement.find(".player-team").text("N/A");
			}

			playerElement.find(".player-name").text(player.username);
			playerElement.find(".player-health").text(round(player.health, 1));
			playerElement.find(".player-max-health").text(player.max_health);
			playerElement.find(".player-food").text(player.food);
			playerElement.find(".player-server").text(player.server);
			if (player.closest_enemy_distance == 2147483647) {
				playerElement.find(".player-enemy-distance").text("N/A");
			} else {
				playerElement.find(".player-enemy-distance").text(round(player.closest_enemy_distance, 1));

				if (player.closest_enemy_distance <= 16) {
					if (distanceMatters.includes(player.server)) {
						highRisk = true;
					}
				}
			}

			if (highRisk) {
				playerElement.find(".td-bg").addClass("table-danger");
			} else {
				playerElement.find(".td-bg").removeClass("table-danger");
			}


			if (found.includes(player.uuid)) {
				found.remove(player.uuid);
			}
		});

		found.forEach((uuid) => {
			$(".player").each(function () {
				if ($(this).data("uuid") == uuid) {
					console.log("Removing " + uuid);
					$(this).remove();
				}
			});
		});
	});
}

function round(value, precision) {
	var multiplier = Math.pow(10, precision || 0);
	return Math.round(value * multiplier) / multiplier;
}

function showLogin() {
	$("#loginModal").modal("show");
}