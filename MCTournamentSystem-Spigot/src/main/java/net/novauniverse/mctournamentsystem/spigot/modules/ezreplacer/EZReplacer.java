package net.novauniverse.mctournamentsystem.spigot.modules.ezreplacer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = false)
public class EZReplacer extends NovaModule implements Listener {
	private List<String> replace;
	private List<String> replaceWith;

	private Random random;

	public EZReplacer() {
		super("TournamentSystem.EZReplacer");
	}

	@Override
	public void onLoad() {
		this.replace = new ArrayList<String>();
		this.replaceWith = new ArrayList<String>();

		this.random = new Random();

		replace.add("easy");
		replace.add("e.z");
		replace.add("ez");
		replace.add("eazy");
		replace.add("e a s y");
		replace.add("gg ez");
		replace.add("gg easy");
		replace.add("esy");
		replace.add("essy");
		replace.add("ezy");
		replace.add("e z");

		replaceWith.add("r/roastme");
		replaceWith.add("Lasagna is just spaghetti flavored cake");
		replaceWith.add("Surgery is just stabbing someone to life");
		replaceWith.add("Ketchup is a liquid made from a fruit and contains 20% sugar, therefore it is a sports drink");
		replaceWith.add("Friends are like potatoes. If you eat them, they die!");
		replaceWith.add("The chances of getting killed by a cow are low but never zero");
		replaceWith.add("life is soup, i am fork");
	}

	public List<String> getReplace() {
		return replace;
	}

	public List<String> getReplaceWith() {
		return replaceWith;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		replace.forEach(text -> {
			if (e.getMessage().equalsIgnoreCase(text)) {
				e.setMessage(replaceWith.get(random.nextInt(replaceWith.size())));
			}
		});
	}
}