var bg_light = null;

$(() => {
	$("#hexagon_bg_container").hide();

	bg_light = document.querySelector("#hexagon_bg_light");

	document.onmousemove = (e) => {
		bg_light.style.top = `${e.clientY}px`;
		bg_light.style.left = `${e.clientX}px`;
	};
});