package main.java.de.voidtech.alison.commands.generation;

import java.awt.Color;
import java.util.List;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.BrowserUtils;
import main.java.de.voidtech.alison.utils.ModelManager;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

public class SearchCommand extends AbstractCommand {

	private static final String SEARCH_URL = "https://duckduckgo.com/?ia=web&iax=web&q=";
	private static final String SAFE_MODE_ENABLED = "&kp=1";
	private static final String SAFE_MODE_DISABLED = "&kp=-2";
	private static final String MISC_OPTS_SUFFIX = "&kae=d&k9=b";
	
	@Override
	public void execute(CommandContext context, List<String> args) {
		String ID;
		if (args.isEmpty()) ID = context.getAuthor().getId();
		else ID = args.get(0).replaceAll("([^0-9a-zA-Z])", "");

		if (!ModelManager.modelExists(ID)) {
			context.reply("I couldn't find any data for that user :(");
			return;
		}

		if (PrivacyManager.userHasOptedOut(ID)) {
			context.reply("This user has chosen not to be quoted.");
			return;
		}
		
		AlisonModel model = ModelManager.getModel(ID);
		String search = model.createSentence();
		if (search == null) {
			context.reply("I couldn't find any data for that user :(");
			return;
		}
		
		Result<Member> userResult = context.getGuild().retrieveMemberById(ID).mapToResult().complete();
		if (userResult.isSuccess()) {
			context.getMessage().getChannel().sendTyping().complete();
			User user = userResult.get().getUser();
			String url = SEARCH_URL + search
					+ (canSendNsfw(context.getMessage().getChannel()) ? SAFE_MODE_DISABLED : SAFE_MODE_ENABLED) 
					+ MISC_OPTS_SUFFIX;
			byte[] image = BrowserUtils.searchAndScreenshot(url);
			MessageEmbed searchEmbed = new EmbedBuilder()
					.setTitle(user.getName() + "'s search history probably contains:")
					.setColor(Color.ORANGE)
					.setImage("attachment://search.png")
					.build();
			context.replyWithFile(image, "search.png", searchEmbed);
		} else context.reply("I couldn't find that user :(");
	}
	
	private boolean canSendNsfw(MessageChannel messageChannel) {
		return messageChannel.getType().equals(ChannelType.PRIVATE) || ((TextChannel) messageChannel).isNSFW();
	}

	@Override
	public String getName() {
		return "search";
	}

	@Override
	public String getUsage() {
		return "search";
	}

	@Override
	public String getDescription() {
		return "Alison will search the internet for some of your greatest thoughts";
	}

	@Override
	public String getShorthand() {
		return "s";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.TEXT_GENERATION;
	}

	@Override
	public boolean isDmCapable() {
		return true;
	}

	@Override
	public boolean requiresArguments() {
		return false;
	}

}
