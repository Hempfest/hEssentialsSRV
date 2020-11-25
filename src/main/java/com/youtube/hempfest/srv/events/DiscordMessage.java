package com.youtube.hempfest.srv.events;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.hempcore.formatting.string.ColoredString;
import com.youtube.hempfest.srv.HempfestSRV;
import com.youtube.hempfest.srv.api.DiscordIntegration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class DiscordMessage extends ListenerAdapter implements Listener {

	Map<String, String> numbers = new HashMap<>();

	public static final DiscordIntegration integration = new DiscordIntegration();

	@EventHandler(priority = EventPriority.HIGH)
	public void chatEvent(AsyncPlayerChatEvent e){
		String message = e.getMessage();
		String format = e.getFormat().replace(message, "");
		TextChannel textChannel = HempfestSRV.getPlugin().jda.getTextChannelById(HempfestSRV.getPlugin().getChannelID2());
	if (textChannel != null) {
		if (HempfestClans.chatMode.get(e.getPlayer()).equals("GLOBAL")) {
			String[] args = message.split(" ");
			String mention = "";
			for (String arg : args) {
				if (arg.contains("@")) {
					mention = arg;
				}
			}
			String result = "";
			for (Member m : textChannel.getMembers()) {
				if (m.getUser().getName().equalsIgnoreCase(mention.replace("@", ""))) {
					result = m.getAsMention();
				}
			}
			textChannel.sendMessage("**" + ChatColor.stripColor(format) + "**" + message.replace(mention, result)).queue();
		}
	}

	}


	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot() || event.isWebhookMessage()) {
			return;
		}

		MessageChannel channel = event.getChannel();
		String[] message = event.getMessage().getContentRaw().split(" ");
		if (event.getMember() != null) {
			User u = event.getMember().getUser();

			// ------------\/ Number Channel Formatting \/------------- //
			if (channel.getId().equals(HempfestSRV.getPlugin().getChannelID4())) {
				List<Message> msgs = channel.getHistory().retrievePast(30).complete();
				List<Message> msgsShort = channel.getHistory().retrievePast(2).complete();
				if (!StringUtils.isNumeric(event.getMessage().getContentRaw())) {
					channel.deleteMessageById(event.getMessageIdLong()).submit(true);
					channel.sendTyping().queue();
					channel.sendMessage(integration.simpleText("Numbers only! " + u.getName(), "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", "https://i.imgur.com/vz4q9mF.png").build()).queue();
					Bukkit.getScheduler().scheduleSyncDelayedTask(HempfestSRV.getPlugin(), () -> {
						for (Message msg : msgs) {
							if (msg.getAuthor().isBot()) {

								msg.delete().queue();


							}
						}
					}, 3);
					return;
				}
				for (Message msg : msgsShort) {
					if (!msg.getAuthor().isBot()) {
						if (!msg.equals(event.getMessage())) {
							String msgContentRaw = msg.getContentRaw();
							int i = Integer.parseInt(msgContentRaw);
							EmbedBuilder ss = new EmbedBuilder();
							ss.setTitle(i + "");
							channel.sendTyping().queue();
							channel.sendMessage(ss.build()).queue();
							if (Integer.parseInt(message[0]) != (i + 1)) {
								channel.deleteMessageById(event.getMessageIdLong()).submit(true);
								EmbedBuilder suggest = new EmbedBuilder();
								suggest.setTitle("Next response must be +1! " + u.getName());
								suggest.setDescription("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
								suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
								channel.sendTyping().queue();
								channel.sendMessage(suggest.build()).queue();
								Bukkit.getScheduler().scheduleSyncDelayedTask(HempfestSRV.getPlugin(), () -> {
									for (Message msgg : msgs) {
										if (msgg.getAuthor().isBot()) {

											msgg.delete().queue();


										}
									}
								}, 3);
								return;
							}
						}
					}
				}

				if (numbers.containsKey(event.getAuthor().getId())) {
					channel.deleteMessageById(event.getMessageIdLong()).submit(true);
					EmbedBuilder suggest = new EmbedBuilder();
					suggest.setTitle("You've already counted! " + u.getName());
					suggest.setDescription("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
					channel.sendTyping().queue();
					channel.sendMessage(suggest.build()).queue();
					Bukkit.getScheduler().scheduleSyncDelayedTask(HempfestSRV.getPlugin(), () -> {
						for (Message msg : msgs) {
							if (msg.getAuthor().isBot()) {

								msg.delete().queue();


							}
						}
					}, 3);
					return;
				}

				numbers.clear();
				numbers.put(event.getAuthor().getId(), event.getMessageId());
			}
			// ------------/\ Number Channel Formatting /\------------- //

			// ------------\/ Minecraft chat formatting \/------------- //
			StringBuilder builder = new StringBuilder();
			for (String s : message) {
				builder.append(s).append(" ");
			}
			String result = builder.toString().trim();
			if (channel.getId().equals(HempfestSRV.getPlugin().getChannelID2())) {
				User user = event.getAuthor();
				Bukkit.broadcastMessage(new ColoredString("&f(&7#&b" + user.getDiscriminator() + "&f) &6&l" + user.getName() + "&7&l : &f&o" + result, ColoredString.ColorType.MC).toString());
				return;
			}
			// ------------/\ Minecraft chat formatting /\------------- //


			// ------------\/ Suggestion formatting \/------------- //
			if (channel.getId().equals(HempfestSRV.getPlugin().getChannelID())) {
				EmbedBuilder suggest = new EmbedBuilder();
				suggest.setTitle("Vote by clicking on an emote below.");
				suggest.setDescription(result);
				suggest.addField("Suggestion", "sent in by user " + event.getMessage().getAuthor().getAsTag(), false);
				suggest.setColor(0x32a875);
				suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
				channel.sendTyping().queue();
				channel.sendMessage(suggest.build()).queue(message1 -> {
					message1.addReaction("U+1F44D").queue();
					message1.addReaction("U+270A").queue();
					message1.addReaction("U+1F44E").queue();
				});
				channel.deleteMessageById(event.getMessageIdLong()).submit(true);
			}
			// ------------/\ Suggestion formatting /\------------- //
		}

	}

}
