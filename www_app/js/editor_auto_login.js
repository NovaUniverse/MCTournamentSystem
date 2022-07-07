function attemptAutoLogin() {
	console.log("Auto login attempt");
	toastr.info("Attempting to log in using stored credentials...");

	let storedCredentials = JSON.parse(atob(localStorage.getItem("stored_credentials")));
	$.getJSON("/api/user/login?username=" + storedCredentials.username + "&password=" + storedCredentials.password, (data) => {
		if (!data.success) {
			console.warn("Auto login failed");
			toastr.error("Auto login failed");
		} else {
			console.log("Auto login success");
			toastr.info("Auto login successful. Reloading...");
			localStorage.setItem("token", data.token);
			setTimeout(() => window.location.reload(), 2000);
		}
	});
}

$(() => {
	setTimeout(() => {
		if (localStorage.getItem("stored_credentials") != undefined) {
			let token = localStorage.getItem("token");
			if (token == null) {
				attemptAutoLogin();
			} else {
				$.getJSON("/api/user/whoami?access_token=" + token, (data) => {
					if (data.logged_in == false) {
						attemptAutoLogin();
					}
				});
			}
		}
	}, 500);
});