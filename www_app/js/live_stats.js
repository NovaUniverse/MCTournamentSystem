const HEAD_SIZE_KEY = "livestats_headsize";

var playerHeadSize = 64;

$(() => {
	$("#settings").hide();
	$("#navbar_brand").on("click", () => $("#settings").show());
	$("#close_settings").on("click", () => $("#settings").hide());

	$("#reset_settings").on("click", () => {
		localStorage.removeItem(themeManagerDatastoreKey);
		localStorage.removeItem(HEAD_SIZE_KEY);
		window.location.reload();
	});

	$("#head_size_selector").on("change", () => {
		setHeadSize($("#head_size_selector").val());
		update();
	});

	if (localStorage.getItem(HEAD_SIZE_KEY) != null) {
		playerHeadSize = parseInt(localStorage.getItem(HEAD_SIZE_KEY));
		validateHeadSize();
	}

	$("#head_size_selector").val(playerHeadSize);

	setInterval(() => update(), 1000);
	update();
});

function validateHeadSize() {
	if (isNaN(playerHeadSize)) {
		toastr.error("Invalid head size. Restoring to defaults");
		setHeadSize(64);
	}
}

function setHeadSize(size) {
	playerHeadSize = size;
	localStorage.setItem(HEAD_SIZE_KEY, size);
	validateHeadSize();
}

const update = () => {
	$.getJSON("/api/public/status", (data) => {
		// Sorting
		data.teams.sort((a, b) => b.score - a.score);
		data.players.sort((a, b) => b.score - a.score);

		console.log(data);

		$("#teams_tbody").children().remove();
		let teamPlacement = 0;
		data.teams.forEach(team => {
			teamPlacement++;

			$("#teams_tbody").append(
				$("<tr></tr>")
					.append(
						$("<td></td>").text(ordinal_suffix_of(teamPlacement) + " place")
					)
					.append(
						$("<td></td>").text("Team " + team.team_number)
					)
					.append(
						$("<td></td>").text(team.score)
					)
			);
		});

		$("#players_tbody").children().remove();
		let playerPlacement = 0;
		data.players.forEach(player => {
			playerPlacement++;

			$("#players_tbody").append(
				$("<tr></tr>")
					.append(
						$("<img>")
							.attr("src", "https://mc-heads.net/avatar/" + player.uuid)
							.attr("width", playerHeadSize)
							.attr("height", playerHeadSize)
					)
					.append(
						$("<td></td>").text(ordinal_suffix_of(playerPlacement) + " place")
					)
					.append(
						$("<td></td>").text(player.username)
					)
					.append(
						$("<td></td>").text("Team " + player.team_number)
					)
					.append(
						$("<td></td>").text(player.score)
					)
					.append(
						$("<td></td>").text(player.kills)
					)
			);
		});
	});
}

const ordinal_suffix_of = (i) => {
	var j = i % 10,
		k = i % 100;
	if (j == 1 && k != 11) {
		return i + "st";
	}
	if (j == 2 && k != 12) {
		return i + "nd";
	}
	if (j == 3 && k != 13) {
		return i + "rd";
	}
	return i + "th";
}