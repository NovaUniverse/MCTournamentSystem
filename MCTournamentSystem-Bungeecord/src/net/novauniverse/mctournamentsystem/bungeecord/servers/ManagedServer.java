package net.novauniverse.mctournamentsystem.bungeecord.servers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;
import net.zeeraa.novacore.commons.log.Log;

public class ManagedServer {
	private String name;

	private String javaExecutable;
	private String jvmArguments;
	private String jar;

	private File workingDirectory;

	private boolean autoStart;

	private Process process;
	private Exception lastException;

	private String lastSessionId;

	public ManagedServer(String name, String javaExecutable, String jvmArguments, String jar, File workingDirectory, boolean autoStart) {
		this.name = name;

		this.javaExecutable = javaExecutable;
		this.jvmArguments = jvmArguments;
		this.jar = jar;

		this.workingDirectory = workingDirectory;
		this.lastSessionId = null;

		this.autoStart = autoStart;

		this.process = null;
		this.lastException = null;
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
	
	public boolean sendCommand(String command) {
		if(!isRunning()) {
			return false;
		}
		
		PrintWriter writer = new PrintWriter(process.getOutputStream());
		writer.println(command);
		writer.flush();
		
		return true;
	}

	public boolean start() {
		if (process != null) {
			if (process.isAlive()) {
				return false;
			}
		}

		Log.info("ManagedServer", "Trying to start server " + name);

		lastSessionId = UUID.randomUUID().toString();

		File log = new File(TournamentSystem.getInstance().getServerLogFolder() + File.separator + lastSessionId + ".log");
		File err = new File(TournamentSystem.getInstance().getServerLogFolder() + File.separator + lastSessionId + ".err.log");

		ProcessBuilder builder = new ProcessBuilder();
		Log.info("Starting process builder for server " + name);

		List<String> command = new ArrayList<String>();

		command.add(javaExecutable);

		if (!TournamentSystem.getInstance().isDisableParentPidMonitoring()) {
			try {
				int pid = ProcessUtils.getOwnPID();
				Log.trace("ManagedServer", "Own pid: " + pid);
				command.add("-DtournamentServerParentProcessID=" + pid);
			} catch (Exception e) {
				Log.error("ManagedServer", "Failed to fetch own PID. " + e.getClass().getName() + " " + e.getMessage());
			}
		}

		for (String string : jvmArguments.split("\\s")) {
			command.add(string);
		}

		command.add("-jar");
		command.add(jar);

		builder.command(command);
		builder.directory(workingDirectory);

		builder.redirectOutput(log);
		builder.redirectError(err);

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
			File logFile = new File(TournamentSystem.getInstance().getServerLogFolder() + File.separator + lastSessionId + ".log");
			if (logFile.exists()) {
				return Files.readAllLines(Paths.get(logFile.getAbsolutePath()));
			}
		}

		return new ArrayList<String>();
	}
}