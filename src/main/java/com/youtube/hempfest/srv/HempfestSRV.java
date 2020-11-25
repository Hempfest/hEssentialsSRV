package com.youtube.hempfest.srv;

import com.youtube.hempfest.hempcore.event.EventBuilder;
import com.youtube.hempfest.srv.events.DiscordCommand;
import com.youtube.hempfest.srv.events.DiscordMessage;
import com.youtube.hempfest.srv.events.BukkitListener;
import com.youtube.hempfest.srv.events.ReactionEvent;
import com.youtube.hempfest.srv.tps.RuntimeSpeed;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.hessentials.configuration.Config;
import org.spigotmc.hessentials.configuration.DataManager;

public final class HempfestSRV extends JavaPlugin {

	public JDA jda;
	private static HempfestSRV plugin;

	@Override
	public void onEnable() {
		// Plugin startup logic
		plugin = this;
		startBot();
		new RuntimeSpeed().run();
		jda.addEventListener(new DiscordMessage());
		jda.addEventListener(new DiscordCommand());
		jda.addEventListener(new BukkitListener());
		jda.addEventListener(new ReactionEvent());
		new EventBuilder(this).compileFields("com.youtube.hempfest.srv.events");
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
	}


	private void startBot() {
		DataManager dm = new DataManager();
		Config jd = dm.getMisc("Discord");
		if (!Bukkit.getPluginManager().isPluginEnabled("JDA")) {
			getLogger().severe("- Severe issue. JDA hook missing. Unable to register needed listeners.");
			Bukkit.getPluginManager().disablePlugin(plugin);
		} else {
			getLogger().info("- JDA hook found! Loading instance.");
		}
		if (!jd.exists()) {
			InputStream is = getResource("Discord.yml");
			Config.copy(is, jd.getFile());
		}
		String token = jd.getConfig().getString("DISCORD_TOKEN");
		Collection<GatewayIntent> collection = new ArrayList<>(Arrays.asList(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MESSAGES));
	try {
		jda = JDABuilder.createDefault(token, collection).setChunkingFilter(ChunkingFilter.ALL).setActivity(Activity.streaming("Cortex stuff | -help", "http://cortex.gg.ggs:8420")).build();;
	} catch (LoginException e) {
		e.printStackTrace();
	}
	}

	/**
	 * Gets the channel id for the suggestion feature.
	 * @return CHANNEL_ID for suggestion feature.
	 */
	public String getChannelID() {
		DataManager dm = new DataManager();
		Config jd = dm.getMisc("Discord");
		return jd.getConfig().getString("CHANNEL_ID");
	}

	/**
	 * Gets the channel id for Minecraft chat.
	 * @return MC_CHAT connectivity to a specified chat to communicate in-between.
	 */
	public String getChannelID2() {
		DataManager dm = new DataManager();
		Config jd = dm.getMisc("Discord");
		return jd.getConfig().getString("MC_CHAT");
	}

	/**
	 * Gets the channel id for staff log chat
	 * @return LOG_CHAT id for communication with discord staff members.
	 */
	public String getChannelID3() {
		DataManager dm = new DataManager();
		Config jd = dm.getMisc("Discord");
		return jd.getConfig().getString("LOG_CHAT");
	}

	/**
	 * Gets the channel id for the numbers feature.
	 * @return NUMBER_CHAT id for numbers feature.
	 */
	public String getChannelID4() {
		DataManager dm = new DataManager();
		Config jd = dm.getMisc("Discord");
		return jd.getConfig().getString("NUMBER_CHAT");
	}

	public static HempfestSRV getPlugin() {
		return plugin;
	}

}
