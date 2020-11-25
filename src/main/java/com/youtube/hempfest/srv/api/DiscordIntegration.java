package com.youtube.hempfest.srv.api;

import com.youtube.hempfest.srv.HempfestSRV;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

public class DiscordIntegration {


	public EmbedBuilder simpleText(String title, String description) {
		EmbedBuilder dummy = new EmbedBuilder();
		dummy.setTitle(title);
		dummy.setDescription(description);
		return dummy;
	}

	public EmbedBuilder simpleSignedText(String title, String description, String thumbnailUrl, String[] Author) {
		EmbedBuilder dummy = new EmbedBuilder();
		dummy.setTitle(title);
		dummy.setDescription(description);
		dummy.setThumbnail(thumbnailUrl);
		dummy.setAuthor(Author[0], Author[1], Author[2]);
		return dummy;
	}

	public EmbedBuilder simpleText(String title, String description, String thumbnailUrl) {
		EmbedBuilder dummy = new EmbedBuilder();
		dummy.setTitle(title);
		dummy.setDescription(description);
		dummy.setThumbnail(thumbnailUrl);
		return dummy;
	}

	public EmbedBuilder simpleText(String title, String description, String thumbnailUrl, String imageUrl) {
		EmbedBuilder dummy = new EmbedBuilder();
		dummy.setTitle(title);
		dummy.setDescription(description);
		dummy.setThumbnail(thumbnailUrl);
		dummy.setImage(imageUrl);
		return dummy;
	}

	public void sendMessage(String channelID, EmbedBuilder message) {
		TextChannel channel = HempfestSRV.getPlugin().jda.getTextChannelById(channelID);
		channel.sendMessage(message.build()).queue();
	}

}
