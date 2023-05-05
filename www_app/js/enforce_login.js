$(() => {
	$.getJSON("/api/v1/user/whoami", (data) => {
		if(!data.logged_in) {
			window.location = "/app/login?redirect=" + encodeURIComponent(window.location.href);
		}
	});
})