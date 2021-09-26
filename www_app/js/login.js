$(function () {
	$("#login_failure").hide();

	$("#btn_login").on("click", function () {
		let username = $("#tbx_userName").val();
		let password = $("#tbx_userPassword").val();

		$.getJSON("/api/login?username=" + username + "&password=" + password, function (data) {
			if (!data.success) {
				$("#login_failure").show();
				toastr.error("Invalid username or password");
			} else {
				localStorage.setItem("token", data.token);
				window.location = "/app/";
			}
		});
	})
});