window.addEventListener('ts_SetTheme', function (event) {
	const data = event.detail;
	const theme = String(data.theme).toLowerCase();
	const fix = data.fix == null ? "null" : data.fix;

	console.log("Changing theme to " + theme);

	const cssLink = document.getElementById("main_css_theme");
	cssLink.href = "/css/theme/bootstrap." + theme + ".min.css";

	const fixLink = document.getElementById("main_css_fix");
	fixLink.href = "/css/fix/" + fix + ".css";
});