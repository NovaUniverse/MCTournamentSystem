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
	$.ajax({
		type: "POST",
		url: "/api/v1/user/login",
		data: JSON.stringify({
			username: username,
			password: password
		}),
		success: (data) => {
			//console.log(data);
			localStorage.setItem("token", data.token);

			if ($("#cbx_remember_me").is(':checked')) {
				let auth = {
					username: username,
					password: password
				}

				let encoded = btoa(JSON.stringify(auth));

				localStorage.setItem("stored_credentials", encoded);
			}

			let customRedirectURL = getUrlParameter("redirect");

			debugger;
			window.location = customRedirectURL == null ? "/app/" : decodeURIComponent(customRedirectURL);
		},
		error: (xhr, ajaxOptions, thrownError) => {
			if (xhr.status == 0 || xhr.status == 503) {
				toastr.error("Failed to communicate with backend server");
				return;
			}

			if (xhr.status == 401) {
				if (isStored) {
					toastr.error("Stored credentials seems so be invalid");
					localStorage.removeItem("stored_credentials");
				} else {
					$("#login_failure").show();
					toastr.error("Invalid username or password");
				}
			} else {
				toastr.error("Could not log in due to an error. " + xhr.statusText);
				console.error(xhr);
				console.error(ajaxOptions);
				console.error(thrownError);
			}
		},
		dataType: "json"
	});
}