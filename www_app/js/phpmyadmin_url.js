$(() => {
	$.getJSON("/api/system/phpmyadmin_url", (data) => {
		$(".phpmyadmin-link").attr("href", data.url);
	});
})