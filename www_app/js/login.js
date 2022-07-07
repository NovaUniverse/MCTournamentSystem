$(() => {
	$("#login_failure").hide();

	$("#btn_login").on("click", function () {
		let username = $("#tbx_userName").val();
		let password = $("#tbx_userPassword").val();

		login(username, password, false);
	});

	if (localStorage.getItem("stored_credentials") != undefined) {
		let storedCredentials = JSON.parse(atob(localStorage.getItem("stored_credentials")));
		login(storedCredentials.username, storedCredentials.password, true);
	}
});

function login(username, password, isStored = false) {
	$.getJSON("/api/user/login?username=" + username + "&password=" + password, (data) => {
		if (!data.success) {
			if (isStored) {
				toastr.error("Stored credentials seems so be invalid");
				localStorage.removeItem("stored_credentials");
			} else {
				$("#login_failure").show();
				toastr.error("Invalid username or password");
			}
		} else {
			localStorage.setItem("token", data.token);

			if ($("#cbx_remember_me").is(':checked')) {
				let auth = {
					username: username,
					password: password
				}

				let encoded = btoa(JSON.stringify(auth));

				localStorage.setItem("stored_credentials", encoded);
			}

			window.location = "/app/";
		}
	});
}