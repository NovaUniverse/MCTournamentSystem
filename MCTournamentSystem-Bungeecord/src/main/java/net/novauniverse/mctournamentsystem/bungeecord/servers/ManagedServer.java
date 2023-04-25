package net.novauniverse.mctournamentsystem.bungeecord.servers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nullable;

import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;
import net.zeeraa.novacore.commons.log.Log;

public class ManagedServer {
	public static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");

	private String name;

	private String javaExecutable;
	private String jvmArguments;
	private String jar;

	private File workingDirectory;

	private boolean autoStart;

	private Process process;
	private Exception lastException;

	private String lastSessionId;

	private boolean passServerName;

	private JSONObject lastStateReport;

	private String stateReportingKey;

	private String lastLogName = "UNKNOWN";

	private Random random;

	private String internalAPIAccessKey;

	@Nullable
	private ServerAutoRegisterData serverAutoRegisterData;

	private String getRandomHexString(Random random, int numchars) {
		StringBuffer sb = new StringBuffer();
		while (sb.length() < numchars) {
			sb.append(Integer.toHexString(random.nextInt()));
		}

		return sb.toString().substring(0, numchars);
	}

	public ManagedServer(String name, String javaExecutable, String jvmArguments, String jar, File workingDirectory, boolean autoStart, @Nullable ServerAutoRegisterData serverAutoRegisterData, boolean passServerName) {
		this.name = name;

		this.javaExecutable = javaExecutable;
		this.jvmArguments = jvmArguments;
		this.jar = jar;

		this.workingDirectory = workingDirectory;
		this.lastSessionId = null;

		this.autoStart = autoStart;

		this.process = null;
		this.lastException = null;

		this.serverAutoRegisterData = serverAutoRegisterData;

		this.lastStateReport = new JSONObject();

		this.passServerName = passServerName;

		this.random = new SecureRandom();

		this.generateStatusReportingKey();
		this.generateInternalAPIAccessKey();
	}

	public void generateInternalAPIAccessKey() {
		this.internalAPIAccessKey = getRandomHexString(random, 64);
	}

	public String getInternalAPIAccessKey() {
		return internalAPIAccessKey;
	}

	public void generateStatusReportingKey() {
		this.stateReportingKey = getRandomHexString(random, 64);
	}

	public String getStateReportingKey() {
		return stateReportingKey;
	}

	public JSONObject getLastStateReport() {
		return lastStateReport;
	}

	public void setLastStateReport(JSONObject lastStateReport) {
		this.lastStateReport = lastStateReport;
	}

	public String getName() {
		return name;
	}

	public String getJvmArguments() {
		return jvmArguments;
	}

	public String getJar() {
		return jar;
	}

	public Process getProcess() {
		return process;
	}

	public String getJavaExecutable() {
		return javaExecutable;
	}

	public File getWorkingDirectory() {
		return workingDirectory;
	}

	public String getLastSessionId() {
		return lastSessionId;
	}

	public boolean isAutoStart() {
		return autoStart;
	}

	public Exception getLastException() {
		return lastException;
	}

	@Nullable
	public ServerAutoRegisterData getServerAutoRegisterData() {
		return serverAutoRegisterData;
	}

	public boolean sendCommand(String command) {
		if (!isRunning()) {
			return false;
		}

		PrintWriter writer = new PrintWriter(process.getOutputStream());
		writer.println(command);
		writer.flush();

		return true;
	}

	public boolean isServerAutoRegisterEnabled() {
		if (serverAutoRegisterData != null) {
			return serverAutoRegisterData.isEnabled();
		}
		return false;
	}

	public boolean register() {
		if (!isServerAutoRegisterEnabled()) {
			return false;
		}

		Log.info("ManagedServer", "Registering server " + name + " pointing to " + serverAutoRegisterData.getHost() + ":" + serverAutoRegisterData.getPort());
		if (ProxyServer.getInstance().getServers().remove(name) != null) {
			Log.info("ManagedServer", "Removed old server named " + name);
		}
		String motd = TournamentSystem.formatMOTD(TournamentSystem.getInstance().getMotd());
		InetSocketAddress inetAddress = new InetSocketAddress(serverAutoRegisterData.getHost(), serverAutoRegisterData.getPort());
		ServerInfo serverInfo = ProxyServer.getInstance().constructServerInfo(name, inetAddress, motd, false);
		ProxyServer.getInstance().getServers().put(name, serverInfo);
		Log.info("ManagedServer", name + " registered successfully");

		return true;
	}

	public boolean start() throws MissingServerJarException {
		if (process != null) {
			if (process.isAlive()) {
				return false;
			}
		}

		generateStatusReportingKey();
		generateInternalAPIAccessKey();

		Log.info("ManagedServer", "Trying to start server " + name);

		lastSessionId = UUID.randomUUID().toString();

		TournamentSystem.getInstance().getServerLogFolder().mkdirs();

		lastLogName = name + "_" + LOG_DATE_FORMAT.format(Calendar.getInstance().getTime()) + "_" + lastSessionId.toString();

		File log = new File(TournamentSystem.getInstance().getServerLogFolder() + File.separator + lastLogName + ".stdout.log");
		File err = new File(TournamentSystem.getInstance().getServerLogFolder() + File.separator + lastLogName + ".stderr.log");

		ProcessBuilder builder = new ProcessBuilder();
		Log.info("Starting process builder for server " + name);

		List<String> command = new ArrayList<String>();

		command.add(javaExecutable);

		if (TournamentSystem.getInstance().isAutoAppendAikarFlags()) {
			AikarFlags.AIKAR_FLAGS.forEach(command::add);
		}

		TournamentSystem.getInstance().getGlobalCustomLaunchFlags().forEach(command::add);

		if (!TournamentSystem.getInstance().isDisableParentPidMonitoring()) {
			try {
				int pid = ProcessUtils.getOwnPID();
				Log.debug("ManagedServer", "Own pid: " + pid);
				command.add("-DtournamentServerParentProcessID=" + pid);
			} catch (Exception e) {
				Log.error("ManagedServer", "Failed to fetch own PID. " + e.getClass().getName() + " " + e.getMessage());
			}
		}

		if (passServerName) {
			command.add("-DtournamentServerNetworkName=" + name);
		}

		command.add("-DtournamentStatusReportingKey=" + stateReportingKey);
		command.add("-DtournamentInternalAPIAccessKey=" + internalAPIAccessKey);
		command.add("-DtournamentAdminUIPort=" + TournamentSystem.getInstance().getWebServer().getPort());

		for (String string : jvmArguments.split("\\s")) {
			command.add(string);
		}

		File jarFile = new File(workingDirectory.getAbsolutePath() + File.separator + jar);
		if (!jarFile.exists()) {
			Log.error("ManagedServer", "Could not find server jar at location " + jarFile.getAbsolutePath());
			throw new MissingServerJarException("Could not find server jar at location " + jarFile.getAbsolutePath());
		}

		command.add("-jar");
		command.add(jar);

		if (isServerAutoRegisterEnabled()) {
			int port = serverAutoRegisterData.getPort();
			command.add("--port");
			command.add("" + port);
			Log.debug("ManagedServer", "Using port " + port + " for server " + name);
		}

		builder.command(command);
		builder.directory(workingDirectory);

		builder.redirectOutput(log);
		builder.redirectError(err);

		String commandSafeToLog = command.toString()
				.replace(stateReportingKey, "[State report key hidden]")
				.replace(internalAPIAccessKey, "[API access key hidden]");

		Log.trace("ManagedServer", "Starting process " + commandSafeToLog + " in directory " + workingDirectory);
		try {
			process = builder.start();
			return true;
		} catch (IOException e) {
			Log.error("ServerRunner", "An exception occured while trying to start a server. " + e.getClass().getName() + " " + e.getMessage());
			lastException = e;
			e.printStackTrace();

		}
		return false;
	}

	public boolean stop() {
		if (process != null) {
			if (process.isAlive()) {
				Log.info("ManagedServer", "Trying to stop server " + name);
				process.destroyForcibly();
				return true;
			}
		}
		return false;
	}

	public boolean isRunning() {
		if (process != null) {
			return process.isAlive();
		}
		return false;
	}

	public int getExitCode() {
		if (process != null) {
			if (!process.isAlive()) {
				return process.exitValue();
			}
		}
		return 0;
	}

	public List<String> getLogFileLines() throws IOException {
		if (lastSessionId != null) {
			File logFile = new File(TournamentSystem.getInstance().getServerLogFolder() + File.separator + lastLogName + ".stdout.log");
			if (logFile.exists()) {
				return Files.readAllLines(Paths.get(logFile.getAbsolutePath()));
			}
		}
		return new ArrayList<String>();
	}
}