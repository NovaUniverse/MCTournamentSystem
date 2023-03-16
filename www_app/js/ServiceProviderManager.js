const ServiceProviders = {
	MojangAPIProxy: "https://mojangapi.novauniverse.net"
}

$.getJSON("/api/v1/service_providers", (data) => {
	console.log("[ServiceProviders] Using the following mojang api proxy url: " + data.mojang_api_proxy);
	ServiceProviders.MojangAPIProxy = data.mojang_api_proxy;
});