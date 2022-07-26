package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.misc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.xxmicloxx.NoteBlockAPI.model.RepeatMode;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;

public class GameEndSoundtrackManager extends NovaModule implements Listener {
	private RadioSongPlayer endSongPlayer;

	public GameEndSoundtrackManager() {
		super("TournamentSystem.GameEndSoundtrackManager");
	}

	@Override
	public void onEnable() throws Exception {
		endSongPlayer = new RadioSongPlayer(TournamentSystem.getInstance().getGameEndMusic());
		endSongPlayer.setRepeatMode(RepeatMode.NO);
		endSongPlayer.setAutoDestroy(false);
		endSongPlayer.setCategory(SoundCategory.RECORDS);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameStart(GameStartEvent e) {
		Bukkit.getServer().getOnlinePlayers().stream().filter(p -> endSongPlayer.getPlayerUUIDs().contains(p.getUniqueId())).forEach(p -> endSongPlayer.addPlayer(p));
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		Log.info("GameEndSoundtrackManager", "Starting end soundtrack");
		endSongPlayer.setPlaying(true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent e) {
		endSongPlayer.addPlayer(e.getPlayer());
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		endSongPlayer.removePlayer(e.getPlayer());
	}
}