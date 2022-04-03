var _catModeEnabled = false;

$(() => {
	$(".bongo_cat").hide();
	if (localStorage.getItem("bongo_cat") === "true") {
		$(".bongo_cat").show();
		_catModeEnabled = true;
	}

	$("#no_more_cats").on("click", function () {
		setCatMode(false);
	});

	window.addEventListener('keypress', (function () {
		var strToType = 'cat',
			strTyped = '';
		return function (event) {
			var character = String.fromCharCode(event.which);
			strTyped += character;
			if (strToType.indexOf(strTyped) === -1) {
				strTyped = '';
			} else if (strTyped === strToType) {
				strTyped = '';
				setCatMode(!_catModeEnabled);
			}
		};
	}()));
});

function setCatMode(enabled) {
	_catModeEnabled = enabled;
	if (enabled) {
		localStorage.setItem("bongo_cat", true);
		$(".bongo_cat").show();
	} else {
		$(".bongo_cat").hide();
		localStorage.removeItem("bongo_cat");
	}
}