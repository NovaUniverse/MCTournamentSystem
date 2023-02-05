const DEFAULT_THEME = "quartz";

var themeManagerDatastoreKey = "mcts_theme";
var themeManagerInteractiveDatastoreKey = "mcts_theme_interactive";

var activeTheme = {};

var whitelistedCustomThemeHashes = [];
var customThemes = [];

function updateInteractiveBackgroundDisplayState() {
	let enabled = $("#theme_manager_enable_interactive_bg").is(":checked");
	if (enabled && activeTheme.is_dark) {
		$("#hexagon_bg_container").show();
		$("body").addClass("interactive-background-enabled");
	} else {
		$("#hexagon_bg_container").hide();
		$("body").removeClass("interactive-background-enabled");
	}
}

$(function () {
	$("#custom_theme_settings").hide();
	$("#custom_theme_disable_options").hide();
	$("#custom_theme_list").hide();

	$(".select-theme-button").on("click", function () {
		console.log("Showing theme selector");
		$("#selectThemeModal").modal("show");
	});

	if (window.localStorage.getItem(themeManagerInteractiveDatastoreKey) === "true") {
		$("#theme_manager_enable_interactive_bg").attr("checked", true);
	}

	$("#theme_manager_enable_interactive_bg").on("change", () => {
		window.localStorage.setItem(themeManagerInteractiveDatastoreKey, $("#theme_manager_enable_interactive_bg").is(":checked") ? "true" : "false");
		updateInteractiveBackgroundDisplayState();
	});

	themes.forEach(theme => {
		$("#theme-selector").append(new Option(theme.display_name, theme.name));
	});

	$("#theme-selector").on("change", function () {
		let theme = $(this).find(":selected").val();

		console.log("Changing theme to " + theme);

		applyThemeByName(theme);
	});

	let theme = localStorage.getItem(themeManagerDatastoreKey);
	if (theme != null) {
		$("#theme-selector").val(theme);
		applyThemeByName(theme);
	} else {
		let useDefault = true;

		if (typeof getUrlParameter !== 'undefined') {
			let defaultTheme = getUrlParameter("default_theme");
			if (hasTheme(defaultTheme)) {
				useDefault = false;
				applyThemeByName(defaultTheme);
				$("#theme-selector").val(defaultTheme);
			} else {
				console.error("Invalid default theme: " + defaultTheme);
			}
		}

		if (useDefault) {
			applyThemeByName(DEFAULT_THEME);
			$("#theme-selector").val(DEFAULT_THEME);
		}
	}

	try {
		let encoded = window.localStorage.getItem("custom_theme_whitelisted_hashes");
		if (encoded != null) {
			let hashes = JSON.parse(atob(encoded));
			whitelistedCustomThemeHashes = [];
			hashes.forEach((hash) => whitelistedCustomThemeHashes.push(hash));
			console.log("Whitelisted custom theme hash count from cache: " + whitelistedCustomThemeHashes.length);
		}

	} catch (err) {
		console.error(err);
		console.error("Failed to read whitelisted custom theme hashes");
	}

	$(".disable_custom_theme").on("click", () => disableCustomTheme());

	$(".disable_custom_theme").hide();
	if (localStorage.getItem("custom_css") != null) {
		let url = localStorage.getItem("custom_css");
		let hash = themeManagerGenHash(url);
		$("#custom_theme_settings").show();
		$("#custom_theme_disable_options").show();
		if (!whitelistedCustomThemeHashes.includes(hash)) {
			setTimeout(() => {
				toastr.warning("Loading external custom theme from " + url + ". You can disable the custom theme is Settings");
			}, 1000);
		}
		$("head").append($("<link>").attr("rel", "stylesheet").attr("href", url));
		$(".disable_custom_theme").show();
	}

	console.debug("Theme manager loaded");

	setTimeout(() => updateInteractiveBackgroundDisplayState(), 1);

	$.getJSON("/api/system/custom_themes", (data) => {
		console.log(data);

		whitelistedCustomThemeHashes = [];
		data.themes.forEach((theme) => {
			let hash = themeManagerGenHash(theme.url);
			whitelistedCustomThemeHashes.push(hash);
			customThemes.push(theme);
		});

		window.localStorage.setItem("custom_theme_whitelisted_hashes", btoa(JSON.stringify(whitelistedCustomThemeHashes)));

		console.log("Available custom themes:");
		console.log(whitelistedCustomThemeHashes);
		console.log(customThemes);

		if (customThemes.length > 0) {
			$("#custom_theme_settings").show();
			$("#custom_theme_list").show();
		}

		for (let i = 0; i < customThemes.length; i++) {
			let theme = customThemes[i];

			let element = $("<a></a>");
			element.text(theme.name);
			element.attr("data-custom-theme-id", "" + i);
			element.attr("href", "#");

			element.on("click", function () {
				let id = parseInt($(this).data("custom-theme-id"));

				let baseTheme = customThemes[id].base_theme;
				if (baseTheme != null) {
					let apply = true;
					if (activeTheme != null) {
						if (activeTheme.name == baseTheme) {
							apply = false;
						}
					}

					if (apply) {
						console.log("Attempting to apply requested base theme " + baseTheme);
						if (hasTheme(baseTheme)) {
							applyThemeByName(baseTheme);
						} else {
							console.error("Missing base theme: " + baseTheme);
						}
					}
				}

				applyCustomTheme(customThemes[id].url);
			});

			$("#custom_theme_list").append(element);
			$("#custom_theme_list").append($("<br>"));
		}
	});
});

function themeManagerGenHash(input) {
	return sha1(input).toString("hex");
}

function hasTheme(name) {
	let themeData = null;
	themes.forEach(t => {
		if (t.name == name) {
			themeData = t;
		}
	});
	return themeData != null;
}

function applyThemeByName(name) {
	let themeData = null;
	themes.forEach(t => {
		if (t.name == name) {
			themeData = t;
		}
	});

	if (themeData != null) {
		applyTheme(themeData);
	}
}

function applyTheme(theme) {
	$("#css_theme").remove();
	$("#css_theme_fix").remove();

	if (theme.css != undefined) {
		let cssTag = $("<link>");
		cssTag.attr("rel", "stylesheet");
		cssTag.attr("href", THEME_DIR + theme.css);
		cssTag.attr("id", "css_theme");
		$("head").prepend(cssTag);
	}

	if (theme.css_fix != undefined) {
		let cssTag = $("<link>");
		cssTag.attr("rel", "stylesheet");
		cssTag.attr("href", THEME_DIR + "fix/" + theme.css_fix);
		cssTag.attr("id", "css_theme_fix");
		$("head").prepend(cssTag);
	}

	activeTheme = theme;

	localStorage.setItem(themeManagerDatastoreKey, theme.name);

	updateInteractiveBackgroundDisplayState();
}

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