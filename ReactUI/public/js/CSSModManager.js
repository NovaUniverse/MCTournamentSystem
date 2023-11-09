window.addEventListener('ts_EnableCSSMod', function (event) {
	const data = event.detail;
	const name = String(data.theme);
	const url = String(data.url)

	var link = document.createElement('link');
	link.rel = 'stylesheet';
	link.type = 'text/css';
	link.href = url;
	link.setAttribute('data-mod-name', name);
	link.classList.add('ts_css_mod');

	document.head.appendChild(link);
});

window.addEventListener('ts_DisableCSSMod', function (event) {
	const data = event.detail;
	const name = String(data.theme);
	removeCustomCss(name);
});

function removeCustomCss(name) {
	var links = document.head.getElementsByTagName('link');

	for (var i = links.length - 1; i >= 0; i--) {
		var link = links[i];

		if (link.classList.contains('ts_css_mod') && link.getAttribute('data-mod-name') == name) {
			link.parentNode.removeChild(link);
		}
	}
}