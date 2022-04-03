var accessKey = "";

const distanceMatters = ["survivalgames", "spleef", "skywars"];
const lowHealthMatters = ["survivalgames", "skywars"];

$(() => {
	setTimeout(() => {
		accessKey = localStorage.getItem("commentator_key");
		console.log(accessKey);
		if (accessKey == null) {
			console.log("No access token in localstorage");
			showLogin();
			toastr.error("Please login with your commentator token provided by the staff");
		} else {
			console.log("Found key in localstorage");
			console.log("Validating key");
			$.getJSON("/api/system/status?commentator_key=" + accessKey, function (data) {
				if (data.success !== false) {
					console.log("Key is valid");
					setInterval(() => {
						update();
					}, 500);
					update();
				} else {
					console.log("Key is invalid");
					window.localStorage.removeItem("commentator_key");
					window.location.reload();
				}
			});
		}
	}, 100);

	$("#btn_login").on("click", function () {
		let key = $("#commentator_key").val();
		$.getJSON("/api/system/status?commentator_key=" + key, function (data) {
			if (data.success !== false) {
				localStorage.setItem("commentator_key", key);
				window.location.reload();
			} else {
				toastr.error("Invalid key");
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
	$.getJSON("/api/system/status?commentator_key=" + accessKey, function (data) {
		let anyInGame = false;

		let found = [];

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

				playerElement.on("click", function() {
					let uuid = $(this).data("uuid");
					
					$.getJSON("/api/commentator/tp?commentator_key=" + accessKey + "&target=" + uuid, function (data) {
						if(data.success) {
							toastr.success("Teleport successful");
						} else {
							toastr.error("Failed to teleport to player. " + data.message);
						}
					});
				});

				$("#player_container").append(playerElement);
			}

			if (player.health <= 4) {
				if (lowHealthMatters.includes(player.server)) {
					highRisk = true;
				}
			}

			let playerExtraData = data.players.find(p2 => p2.uuid == player.uuid);

			console.log(playerExtraData);

			if(playerExtraData != null) {
				playerElement.find(".player-score").text(playerExtraData.score);
				playerElement.find(".player-team-score").text(playerExtraData.team_score);
				playerElement.find(".player-team").text("Team " + playerExtraData.team_number);				
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