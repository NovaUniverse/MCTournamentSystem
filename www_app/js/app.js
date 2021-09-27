var sendTarget = "all";
var token = "";

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
			$.getJSON("/api/send_players?server=" + encodeURIComponent(serverName) + "&access_token=" + token, function (data) {
				console.log(data);
				if (data.success) {
					$('#select_server_modal').modal('hide');
					toastr.success("Success");
				} else {
					toastr.error(data.message);
				}
			});
		} else {
			$.getJSON("/api/send_player?server=" + encodeURIComponent(serverName) + "&player=" + encodeURIComponent(sendTarget) + "&access_token=" + token, function (data) {
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

	$(".btn_broadcast").on("click", function () {
		$("#broadcast_modal").modal("show");
	});

	$(".btn-reset-data").on("click", function () {
		$("#broadcast_reset_data").modal("show");
	})

	$("#btn_remove_playerdata").on("click", function () {
		$.getJSON("/api/clear_players?access_token=" + token, function (data) {
			console.log(data);
			if(data.success) {
				toastr.success("Player data wiped");
				$("#broadcast_reset_data").modal("hide");
			} else {
				toastr.error(data.message);
			}
		});
	});

	$("#btn_full_reset").on("click", function () {
		$.getJSON("/api/reset?access_token=" + token, function (data) {
			console.log(data);
			if(data.success) {
				toastr.success("Player data wiped");
				$("#broadcast_reset_data").modal("hide");
			} else {
				toastr.error(data.message);
			}
		});
	});

	$("#btn_broadcast").on("click", function () {
		let text = $("#broadcast_text_message").val();
		$.getJSON("/api/broadcast?message=" + encodeURIComponent(text) + "&access_token=" + token, function (data) {
			if (data.success) {
				toastr.success("Message sent");
				$("#broadcast_text_message").val("");
			} else {
				toastr.error(data.message);
			}
		});

		$('#broadcast_modal').modal('hide');
	});

	$(".btn-start-game").on("click", function () {
		$.confirm({
			title: 'Confirm start!',
			content: 'Please confirm that you want to start the game countdown',
			buttons: {
				confirm: function () {
					$.getJSON("/api/start_game" + "?access_token=" + token, function (data) {
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

	$.getJSON("/api/status" + "?access_token=" + token, function (data) {
		if (data.error == "unauthorized") {
			window.location = "/app/login/";
			return;
		}
		for (let i = 0; i < data.servers.length; i++) {
			let server = data.servers[i];
			$("#select_server").append(new Option(server.name, server.name));
		}
	});

	setInterval(function () {
		update();
	}, 1000);
	update();
});

function update() {
	$.getJSON("/api/status" + "?access_token=" + token, function (data) {
		if (data.error == "unauthorized") {
			console.error("It seems like we are no longer authorised. Maybe we should add a real error message here");
			return;
		}
		//console.log(data);

		let toRemove = [];

		$(".player-tr").each(function () {
			toRemove.push($(this).data("uuid"));
		});

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

				playerElement.find(".player-avatar").attr("src", "https://crafatar.com/avatars/" + player.uuid);
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
	});
}