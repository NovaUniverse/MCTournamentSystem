$(() => {
	$.getJSON("/api/v1/system/phpmyadmin_url", (data) => {
		$(".phpmyadmin-link").attr("href", data.url);
	});
})