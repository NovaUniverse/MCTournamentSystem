package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.redirect;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

public class FileNotFoundHandler implements HttpHandler {
	private File webRoot;

	public FileNotFoundHandler(File webRoot) {
		this.webRoot = webRoot;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String response = "404: File not found";

		File notFoundPage = new File(webRoot.getAbsolutePath() + File.separator + "404.html");
		if (notFoundPage.exists()) {
			response = FileUtils.readFileToString(notFoundPage, "UTF-8");
			exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
		} else {
			exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
		}

		exchange.sendResponseHeaders(404, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}