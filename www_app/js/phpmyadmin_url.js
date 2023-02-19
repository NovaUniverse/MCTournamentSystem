$(() => {
	$.getJSON("/api/v1/system/web/phpmyadmin_url", (data) => {
		$(".phpmyadmin-link").attr("href", data.url);
	});
})