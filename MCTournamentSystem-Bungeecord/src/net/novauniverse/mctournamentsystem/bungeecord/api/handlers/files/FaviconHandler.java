package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.zeeraa.novacore.commons.utils.StreamUtils;

@SuppressWarnings({ "restriction" })
public class FaviconHandler implements HttpHandler {
	private String dataFolder;

	public FaviconHandler(String dataFolder) {
		this.dataFolder = dataFolder;
	}

	public void handle(HttpExchange he) throws IOException {
		File iconFile = new File(dataFolder + File.separator + "favicon.ico");

		// System.out.println("icon: " + iconFile.getPath() + " exists: " +
		// iconFile.exists());

		if (!iconFile.exists()) {
			URL inputUrl = getClass().getResource("/favicon.ico");
			// System.out.println(inputUrl);
			FileUtils.copyURLToFile(inputUrl, iconFile);
		}

		he.sendResponseHeaders(200, iconFile.length());
		OutputStream os = he.getResponseBody();
		FileInputStream fis = new FileInputStream(iconFile);
		StreamUtils.copyStream(fis, os);
		os.close();
		fis.close();
	}
}