package com.youtube.hempfest.srv.api;

import com.youtube.hempfest.goldeco.listeners.PlayerListener;
import com.youtube.hempfest.goldeco.util.Utility;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.UUID;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;

public class EmbeddedAssortment extends DiscordIntegration{

	private List<String> targetList;

	private int linesPerPage;
	private TextChannel p;

	/**
	* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	* Sister class to "PaginatedAssortment.java" from HempCore v1.0.3
	*   *  * * * * * * * * ** * * * * * * * * * * * * * * * * * * *
	 */

	// ------------ Formatting ------------- //
	private String listTitle = "&o-- [Specify the returned list's title here] --";
	private String listBorder = "&7&o&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬";
	// ------------------------------------- //

	protected double format(String amount) {
		BigDecimal b1 = new BigDecimal(amount);
		MathContext m = new MathContext(3);
		BigDecimal b2 = b1.round(m);
		return b2.doubleValue();
	}

	public EmbeddedAssortment(TextChannel p, List<String> targetList) {
		this.targetList = targetList;
		this.p = p;
	}

	public void setLinesPerPage(int linesPerPage) {
		this.linesPerPage = linesPerPage;
	}

	public void setListBorder(String listBorder) {
		this.listBorder = listBorder;
	}

	public void setListTitle(String listTitle) {
		this.listTitle = listTitle;
	}


	static String fastReplace( String str, String target, String replacement ) {
		int targetLength = target.length();
		if( targetLength == 0 ) {
			return str;
		}
		int idx2 = str.indexOf( target );
		if( idx2 < 0 ) {
			return str;
		}
		StringBuilder buffer = new StringBuilder( targetLength > replacement.length() ? str.length() : str.length() * 2 );
		int idx1 = 0;
		do {
			buffer.append( str, idx1, idx2 );
			buffer.append( replacement );
			idx1 = idx2 + targetLength;
			idx2 = str.indexOf( target, idx1 );
		} while( idx2 > 0 );
		buffer.append( str, idx1, str.length() );
		return buffer.toString();
	}


	/**
	 * Send the paginated list to the channel. (p = textChannel, page = pageNumber, totalPageCount = totalPageCount )
	 */
	public void export(int page)
	{
		EmbedBuilder builder = new EmbedBuilder();
		builder.setTitle(String.format(listTitle, page));

		builder.addField(listBorder, "", false);
		builder.setImage("https://media0.giphy.com/media/3o84sq21TxDH6PyYms/200.gif");
		int totalPageCount = 1;
		if((targetList.size() % linesPerPage) == 0)
		{
			if(targetList.size() > 0)
			{
				totalPageCount = targetList.size() / linesPerPage;
			}
		}
		else
		{
			totalPageCount = (targetList.size() / linesPerPage) + 1;
		}

		if(page <= totalPageCount)
		{

			if(targetList.isEmpty())
			{

			}
			else
			{
				int i = 0, k = 0;
				page--;
				for (String entry : targetList)
				{
					k++;
					if ((((page * linesPerPage) + i + 1) == k) && (k != ((page * linesPerPage) + linesPerPage + 1)))
					{
						i++;
						//send stuff
						PlayerListener money = new PlayerListener(Bukkit.getOfflinePlayer(UUID.fromString(entry)));
						builder.addField(Bukkit.getOfflinePlayer(UUID.fromString(entry)).getName(), money.get(Utility.BALANCE), false);
					}
				}
				int point; point = page + 1; if (page >= 1) {
				int last; last = point - 1; point = point + 1;

				if (page < (totalPageCount - 1)) {
					// send emotes
				}
				if (page == (totalPageCount - 1)) {
					// last page
				}
			} if (page == 0) {
				point = page + 1 + 1;

			}
			}
		}
		else
		{

		}
		page++;
		builder.addField(listBorder, "", false);
		builder.setFooter("Page " + page + " loaded", "https://i.imgur.com/6QRQQmz.png");
		p.sendMessage(builder.build()).queue();
	}



}
