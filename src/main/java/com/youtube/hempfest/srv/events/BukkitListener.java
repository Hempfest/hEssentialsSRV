package com.youtube.hempfest.srv.events;

import com.youtube.hempfest.srv.HempfestSRV;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitListener extends ListenerAdapter implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e) {
		TextChannel textChannel = HempfestSRV.getPlugin().jda.getTextChannelById(HempfestSRV.getPlugin().getChannelID2());
		textChannel.sendMessage("**" + e.getPlayer().getName() + "**" + " *just joined.*").queue();
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(PlayerQuitEvent e) {
		TextChannel textChannel = HempfestSRV.getPlugin().jda.getTextChannelById(HempfestSRV.getPlugin().getChannelID2());
		textChannel.sendMessage("**" + e.getPlayer().getName() + "**" + " *just left.*").queue();
	}

}
