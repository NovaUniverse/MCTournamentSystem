var hasVibe = false;
function vibe() {
	if (hasVibe) {
		return;
	}
	hasVibe = true;
	var tag = document.createElement('script');

	tag.src = "https://www.youtube.com/iframe_api";
	var firstScriptTag = document.getElementsByTagName('script')[0];
	firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

	// 3. This function creates an <iframe> (and YouTube player)
	//    after the API code downloads.
	var player;
}

function onYouTubeIframeAPIReady() {
	let vibeVideoId = localStorage.getItem("custom_vibe_video_id");

	if (vibeVideoId == null) {
		vibeVideoId = "Q_5HCPgFgW8";
	}

	player = new YT.Player('yt_player', {
		videoId: vibeVideoId,
		controls: 0,
		disablekb: 1,
		playerVars: {
			'playsinline': 1,
		},
		events: {
			'onReady': function (event) {
				event.target.playVideo();
			}
		}
	});
}

$(() => {
	window.addEventListener('keypress', (function () {
		var strToType = 'vibe',
			strTyped = '';
		return function (event) {
			var character = String.fromCharCode(event.which);
			strTyped += character;
			if (strToType.indexOf(strTyped) === -1) {
				strTyped = '';
			} else if (strTyped === strToType) {
				strTyped = '';
				vibe();
			}
		};
	}()));
});