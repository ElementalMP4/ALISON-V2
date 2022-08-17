package main.java.de.voidtech.alison.commands.generation;

import java.awt.Color;
import java.util.List;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.ModelManager;
import main.java.de.voidtech.alison.utils.NodeUtils;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

public class QuoteCommand extends AbstractCommand {

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
		String quote = model.createQuote();
		if (quote == null) {
			context.reply("I couldn't find any data for that user :(");
			return;
		}
		
		Result<Member> userResult = context.getGuild().retrieveMemberById(ID).mapToResult().complete();
		if (userResult.isSuccess()) {
			context.getMessage().getChannel().sendTyping().complete();
			User user = userResult.get().getUser();
			byte[] image = NodeUtils.getQuoteImage(user, quote);
			MessageEmbed quoteEmbed = new EmbedBuilder()
					.setTitle("An infamous quote from " + user.getName())
					.setColor(Color.ORANGE)
					.setImage("attachment://quote.png")
					.build();
			context.replyWithFile(image, "quote.png", quoteEmbed);
		} else context.reply("I couldn't find that user :(");
	}

	@Override
	public String getName() {
		return "quote";
	}

	@Override
	public String getUsage() {
		return "quote";
	}

	@Override
	public String getDescription() {
		return "Create an image with a quote from either yourself or a member of your server!";
	}

	@Override
	public String getShorthand() {
		return "q";
	}

	@Override
	public boolean isDmCapable() {
		return false;
	}

	@Override
	public boolean requiresArguments() {
		return false;
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.TEXT_GENERATION;
	}

}
