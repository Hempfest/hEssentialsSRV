package com.youtube.hempfest.srv.events;

import com.youtube.hempfest.clans.HempfestClans;
import com.youtube.hempfest.clans.util.construct.Clan;
import com.youtube.hempfest.clans.util.data.Config;
import com.youtube.hempfest.clans.util.data.ConfigType;
import com.youtube.hempfest.clans.util.data.DataManager;
import com.youtube.hempfest.goldeco.data.PlayerData;
import com.youtube.hempfest.goldeco.listeners.PlayerListener;
import com.youtube.hempfest.hempcore.formatting.SortedDoubleMap;
import com.youtube.hempfest.srv.api.EmbeddedAssortment;
import com.youtube.hempfest.srv.tps.RuntimeSpeed;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.UUID;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand extends ListenerAdapter {

	private boolean isInteger(String s, int radix) {
		Scanner sc = new Scanner(s.trim());
		if(!sc.hasNextInt(radix)) return false;
		// we know it starts with a valid int, now make sure
		// there's nothing left!
		sc.nextInt(radix);
		return !sc.hasNext();
	}

	private List<String> getLeaderboard() {
		PlayerListener el = new PlayerListener();
		List<String> array = new ArrayList<>();
		HashMap<String, Double> players = new HashMap<String, Double>();
		for (String playerName : el.getAllPlayers()) {
			PlayerData data = new PlayerData(UUID.fromString(playerName));
			FileConfiguration fc = data.getConfig();
			players.put(playerName, fc.getDouble("player." + "world" + ".balance"));
		}
		SortedDoubleMap comp = new SortedDoubleMap(players);
		TreeMap<String, Double> sorted_map = new TreeMap<>(comp);


		sorted_map.putAll(players);
		String nextTop = "";
		Double nextTopBal = 0.0;

		for (Map.Entry<String, Double> player : sorted_map.entrySet()) {

			if (player.getValue() > nextTopBal) {
				nextTop = player.getKey();
			}
			array.add(nextTop);
		}
		return array;
	}


	@Override
	public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
		if(event.getAuthor().isBot() || event.isWebhookMessage()) {
			return;
		}
		MessageChannel channel = event.getChannel();
		String[] message = event.getMessage().getContentRaw().split(" ");

		if (event.getMember() != null) {
			User u = event.getMember().getUser();
			// ------------\/ Command formatting \/------------- //
			if (message.length == 1) {
				if (message[0].equalsIgnoreCase("-leaderboard")) {
					EmbeddedAssortment assortment = new EmbeddedAssortment(event.getChannel(), getLeaderboard());
					assortment.setLinesPerPage(5);
					assortment.setListTitle("CLANS | Richest players | Page #%s");
					assortment.setListBorder("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					assortment.export(1);
				}
				if (message[0].equalsIgnoreCase("-online")) {
					EmbedBuilder suggest = new EmbedBuilder();
					suggest.setTitle("Online players (" + Bukkit.getOnlinePlayers().size() + ")");
					suggest.setDescription("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					StringBuilder stringBuilder = new StringBuilder();
					if (Bukkit.getOnlinePlayers().size() > 0) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							stringBuilder.append("**").append(p.getName()).append("**").append(", ");
						}
						suggest.addField("Names: ", stringBuilder.toString().trim(), false);
						List<String> clanNames = new ArrayList<>();
						for (Player p : Bukkit.getOnlinePlayers()) {
							if (Clan.clanUtil.getClan(p) != null) {
								Clan clan = HempfestClans.clanManager(p);
								if (!clanNames.contains(clan.getClanTag())) {
									clanNames.add(clan.getClanTag());
								}
							}
						}
						suggest.addField("Clans online: (" + clanNames.size() + ")", clanNames.toString(), false);
					}
					Clan dummy = new Clan(null);
					suggest.addField("Current tps:", dummy.format(String.valueOf(RuntimeSpeed.getTPS())) + "", false);
					suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
					channel.sendTyping().queue();
					channel.sendMessage(suggest.build()).queue();
				}
				if (message[0].equalsIgnoreCase("-clan")) {
					String name = u.getName();
					if (event.getMember().getNickname() != null) {
						name = event.getMember().getNickname();
					}
					if (Clan.clanUtil.getUserID(name) == null) {
						EmbedBuilder suggest = new EmbedBuilder();
						suggest.setTitle("You have no clan stats to display. " + name);
						suggest.setDescription("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
						suggest.addField("Play to get", "Log on and create a clan to then be able to use this command. Or double check your discord username matches your in game name.", false);
						suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
						channel.sendTyping().queue();
						channel.sendMessage(suggest.build()).queue();
						return;
					}
					DataManager dm = new DataManager(Clan.clanUtil.getUserID(name).toString(), "");
					Config data = dm.getFile(ConfigType.USER_FILE);
					if (data.exists()) {
						String id = data.getConfig().getString("Clan");

						if (id != null) {
							Clan clan = new Clan(id);
							EmbedBuilder suggest = new EmbedBuilder();
							suggest.setTitle("Clan stats for " + u.getName());
							suggest.setDescription("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							suggest.setColor(0x32a875);
							suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
							suggest.addField("Name:", clan.getClanTag(), false);
							suggest.addField("Power level:", clan.format(String.valueOf(clan.getPower())) + "", false);
							suggest.addField("Color:", clan.getChatColor(), false);
							suggest.addField("Claims:", clan.getOwnedClaims().length + " chunks", false);
							StringBuilder builder = new StringBuilder();
							for (String member : Arrays.asList(clan.getMembers())) {
								builder.append(member + ", ");
							}
							suggest.addField(u.getName() + " KD: ", Clan.clanUtil.getKD(UUID.fromString(data.getName().replace(".yml", ""))) + "", false);
							suggest.addField("Members:", builder.toString().trim() + " chunks", false);
							suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
							channel.sendTyping().queue();
							channel.sendMessage(suggest.build()).queue();

						} else {
							EmbedBuilder suggest = new EmbedBuilder();
							suggest.setTitle("You have no clan stats to display.");
							suggest.setDescription("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
							suggest.addField("Play to get", "Log on and create a clan to then be able to use this command.", false);
							suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
							channel.sendTyping().queue();
							channel.sendMessage(suggest.build()).queue();
							return;
						}
					}
				}
				if (message[0].equalsIgnoreCase("-ip")) {
					EmbedBuilder suggest = new EmbedBuilder();
					suggest.setTitle("Current server address:");
					suggest.setDescription("cortex.ggs.gg");
					suggest.setColor(0x32a875);
					suggest.setThumbnail("https://i.imgur.com/vz4q9mF.png");
					suggest.addField("Dynmap render", "http://cortex.ggs.gg:8420/", false);
					channel.sendTyping().queue();
					channel.sendMessage(suggest.build()).queue();
					return;
				}
				if (message[0].equalsIgnoreCase("-help")) {
					EmbedBuilder suggest = new EmbedBuilder();
					suggest.setTitle("What did you expect? Help? Pfftt", "https://www.dafk.net/what/");
					suggest.setDescription("Nothing to see here.");
					suggest.setColor(0x32a875);
					suggest.setThumbnail("https://i.imgur.com/SXUZkng.png");
					suggest.setImage("https://media2.giphy.com/media/Ju7l5y9osyymQ/giphy.gif");
					channel.sendTyping().queue();
					channel.sendMessage(suggest.build()).queue();
					return;
				}
				return;
			}
			if (message.length == 2) {

				if (message[0].equalsIgnoreCase("-leaderboard")) {
					String page = message[1];
					if (Integer.parseInt(page) == 0) {
						page = String.valueOf(1);
					}
					if (!isInteger(page, 10)) {
						channel.sendMessage(DiscordMessage.integration.simpleText("Not a valid page.", "Use -leaderboard #page for more.").build()).queue();
						return;
					}
					EmbeddedAssortment assortment = new EmbeddedAssortment(event.getChannel(), getLeaderboard());
					assortment.setLinesPerPage(5);
					assortment.setListTitle("CLANS | Richest players | Page #%s");
					assortment.setListBorder("▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
					assortment.export(Integer.parseInt(page));
				}
			}
			// ------------/\ Command formatting /\------------- //


		}
	}
}
