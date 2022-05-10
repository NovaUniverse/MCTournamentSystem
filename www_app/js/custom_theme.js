function applyCustomTheme(url = null) {
	if (url == null) {
		localStorage.removeItem("custom_css");
	} else {
		localStorage.setItem("custom_css", url);
	}
	location.reload();
}

function disableCustomTheme() {
	applyCustomTheme(null);
}

function setCustomVibeVideoId(id = null) {
	if (id == null) {
		localStorage.removeItem("custom_vibe_video_id");
	} else {
		localStorage.setItem("custom_vibe_video_id", id)
	}
}

$(() => {
	$("#disable_custom_theme").on("click", () => disableCustomTheme());

	$("#disable_custom_theme").hide();
	if (localStorage.getItem("custom_css") != null) {
		let url = localStorage.getItem("custom_css");
		setTimeout(() => {
			toastr.warning("Loading custom css from " + url + ". You can disable the custom theme is Settings");
		}, 1000);
		$("head").append($("<link>").attr("rel", "stylesheet").attr("href", url));
		$("#disable_custom_theme").show();
	}
});