const HEAD_SIZE_KEY = "livestats_headsize";

var playerHeadSize = 64;
var offlineMode = false;
var skinRenderAPIUrl = null;
var hasSkinRestorer = false;
var checkedOfflineModePlayers = [];
var offlineSkinCache = new Map();

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

	$.getJSON("/api/v1/service_providers", (data) => {
		skinRenderAPIUrl = data.skin_render_api;
		console.log("Skin render api is: " + skinRenderAPIUrl);
		$.getJSON("/api/v1/system/mode", (data) => {
			$("#connecting_message").hide();
			$("#main").removeClass("d-none");
			
			offlineMode = data.offline_mode;
			hasSkinRestorer = data.has_skin_restorer;
			
			console.log("Offline mode: " + offlineMode);
			console.log("Has skin restorer: " + hasSkinRestorer);
			
			setInterval(() => update(), 1000);
			update();

			if(offlineMode) {
				setInterval(() => {
					console.log("Requesting skin renewal");
					checkedOfflineModePlayers = [];
				}, 1000 * 20);
			}
		}).fail((err) => {
			console.error(err);
			toastr.error("An error occured while connecting to the tournament system api");
			$("#connecting_message").text("Could not connect to tournament system");
			$("#error_message").text(err.responseText);
		});
	}).fail((err) => {
		console.error(err);
		toastr.error("An error occured while connecting to the tournament system api");
		$("#connecting_message").text("Could not connect to tournament system");
		$("#error_message").text(err.responseText);
	});
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

function updateOfflineModePlayerSkinCache(username) {
	if (hasSkinRestorer) {
		$.getJSON("/api/skinrestorer/get_user_skin?username=" + username, (data) => {
			if (data.has_skin) {
				let skinData = JSON.parse(atob(data.skin_data));
				if (skinData.textures != null) {
					if (skinData.textures.SKIN != null) {
						let skinUrl = skinData.textures.SKIN.url;
						console.log(username + " has the skin " + skinUrl);
						offlineSkinCache.set(username, skinUrl);
					}
				}
			}
		});
	}
}

const update = () => {
	$.getJSON("/api/v1/public/status", (data) => {
		// Sorting
		data.teams.sort((a, b) => b.score - a.score);
		data.players.sort((a, b) => b.score - a.score);

		//console.log(data);

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

			let initialSkinUrl = "https://mc-heads.net/avatar/" + (offlineMode ? "MHF_Steve" : player.uuid);

			if (offlineMode) {
				if (hasSkinRestorer) {
					if (skinRenderAPIUrl != null) {
						if (offlineSkinCache.has(player.username)) {
							initialSkinUrl = skinRenderAPIUrl + "/from_image/face/ts_player_skin.png?resolution=256&url=" + offlineSkinCache.get(player.username);
						}

						if (!checkedOfflineModePlayers.includes(player.username)) {
							checkedOfflineModePlayers.push(player.username);
							updateOfflineModePlayerSkinCache(player.username);
						}
					}
				}
			}

			let skinImage = $("<img>");
			skinImage.attr("src", initialSkinUrl);
			skinImage.attr("width", playerHeadSize);
			skinImage.attr("height", playerHeadSize);
			skinImage.attr("data-username", player.username);
			skinImage.addClass("player-head");

			$("#players_tbody").append(
				$("<tr></tr>")
					.append(skinImage)
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