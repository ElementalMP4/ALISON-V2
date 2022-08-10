package main.java.de.voidtech.alison.entities;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class CommandContext {
	private User author;
	private Member member;
	private Guild guild;
	private JDA jda;
	private Message message;
	
	public CommandContext(Message message) {
		this.message = message;
		this.guild = message.getGuild();
		this.author = message.getAuthor();
		this.member = message.getMember();
		this.jda = message.getJDA();		
	}
	
	public void reply(String content) {
		this.message.reply(content).mentionRepliedUser(false).queue();
	}
	
	public void reply(MessageEmbed embed) {
		this.message.replyEmbeds(embed).mentionRepliedUser(false).queue();
	}
	
	public User getAuthor() {
		return this.author;
	}
	
	public Guild getGuild() {
		return this.guild;
	}
	
	public Member getMember() {
		return this.member;
	}
	
	public JDA getJDA() {
		return this.jda;
	}
	
	public Message getMessage() {
		return this.message;
	}
}
