const DEFAULT_THEME = "quartz";

var themeManagerDatastoreKey = "mcts_theme";
var themeManagerInteractiveDatastoreKey = "mcts_theme_interactive";

var activeTheme = {};

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

	console.debug("Theme manager loaded");

	setTimeout(() => updateInteractiveBackgroundDisplayState(), 1);
});

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
