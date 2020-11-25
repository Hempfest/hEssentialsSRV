package com.youtube.hempfest.srv.events;

import com.youtube.hempfest.srv.HempfestSRV;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ReactionEvent extends ListenerAdapter {

	Map<String, String> hasVoted = new HashMap<>();

	@Override
	public void onMessageReactionAdd(MessageReactionAddEvent event) {

		MessageChannel channel = event.getChannel();
		String chID = channel.getId();
		String logID = HempfestSRV.getPlugin().getChannelID3();
		TextChannel ch = HempfestSRV.getPlugin().jda.getTextChannelById(logID);
		if (chID.equals(HempfestSRV.getPlugin().getChannelID())) {

			//hasVoted.put(event.getMessageId(), event.getUserId());
			List<Message> msgs = channel.getHistory().retrievePast(30).complete();
			for (Message msg : msgs) {

				if (!event.getUser().isBot()) {
					List<MessageReaction> reacts = msg.getReactions();
					if (reacts.size() >= 2) {
						if (reacts.get(2).getCount() >= 10) {
							announceMessageLog(Resolution.FAIL, ch, msg);
							announceMessageDelete(Resolution.FAIL, channel, msg);
							msg.delete().queue();

						}
						if (reacts.get(0).getCount() >= 20) {
							announceMessageLog(Resolution.SUCCESS, ch, msg);
							announceMessageDelete(Resolution.SUCCESS, channel, msg);
							msg.delete().queue();
						}
					}
				}
			}
		}
		}

		public enum Resolution {
		SUCCESS, FAIL
		}

		public boolean hasVoted(String mId, String id) {
			if (!hasVoted.containsKey(mId)) {
				return false;
			}
			return hasVoted.get(mId).equals(id);
		}

		public void announceMessageDelete(Resolution type, MessageChannel channel, Message message) {
			EmbedBuilder suggest = new EmbedBuilder();
			List<MessageReaction> reactions = message.getReactions();
		switch (type) {
			case FAIL:
				suggest.setTitle("Voting results:");
				suggest.setDescription("Vote FAIL");
				suggest.setColor(0x32a875);
				suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
				break;
			case SUCCESS:
			suggest.setTitle("Voting results:");
			suggest.setDescription("Vote PASS");
			suggest.setColor(0x32a875);
			suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
			break;
		}
			channel.sendTyping().queue();
		String value = "";
		String sugg = "";
			for (MessageEmbed msg : message.getEmbeds()) {
				sugg = msg.getDescription();
				value = msg.getFields().get(0).getValue();
			}
			suggest.addField("Suggestion", sugg + " - " + value, false);
			suggest.addField("Votes:", "Up (" + reactions.get(0).getCount() + ") Down (" + reactions.get(1).getCount() + ")", false);
			channel.sendMessage(suggest.build()).queue();
		}

	public void announceMessageLog(Resolution type, MessageChannel channel, Message message) {
		EmbedBuilder suggest = new EmbedBuilder();
		List<MessageReaction> reactions = message.getReactions();
		switch (type) {
			case FAIL:
				suggest.setTitle("Voting results:");
				suggest.setDescription("Vote FAIL");
				suggest.setColor(0x32a875);
				suggest.setThumbnail("https://upload.wikimedia.org/wikipedia/en/thumb/b/ba/Red_x.svg/1200px-Red_x.svg.png");
				break;
			case SUCCESS:
				suggest.setTitle("Voting results:");
				suggest.setDescription("Vote PASS");
				suggest.setColor(0x32a875);
				suggest.setThumbnail("https://upload.wikimedia.org/wikipedia/commons/thumb/b/bd/Checkmark_green.svg/1200px-Checkmark_green.svg.png");
				break;
		}
		channel.sendTyping().queue();
		suggest.addField("Votes:", "Up (" + reactions.get(0).getCount() + ") Down (" + reactions.get(1).getCount() + ")", false);
		channel.sendMessage(suggest.build()).queue();
	}


}
