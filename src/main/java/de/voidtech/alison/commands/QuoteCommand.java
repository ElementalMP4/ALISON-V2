package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.jsoup.Jsoup;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.ModelManager;
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
			byte[] image = getQuoteImage(user, quote);
			MessageEmbed quoteEmbed = new EmbedBuilder()
					.setTitle("An infamous quote from " + user.getName())
					.setColor(Color.ORANGE)
					.setImage("attachment://quote.png")
					.setTimestamp(Instant.now())
					.build();
			context.replyWithFile(image, "quote.png", quoteEmbed);
		} else context.reply("I couldn't find that user :(");
	}
	
	private byte[] getQuoteImage(User user, String quote) {
		try {
			String cardURL = Alison.getConfig().getImageApiUrl() + "quote/?avatar_url=" + user.getEffectiveAvatarUrl() + "?size=2048"
					+ "&username=" + URLEncoder.encode(user.getName(), StandardCharsets.UTF_8.toString())
					+ "&quote=" + URLEncoder.encode(quote, StandardCharsets.UTF_8.toString());
			URL url = new URL(cardURL);
			//Remove the data:image/png;base64 part
			String response = Jsoup.connect(url.toString()).get().toString().split(",")[1];
			byte[] imageBytes = DatatypeConverter.parseBase64Binary(response);
			return imageBytes;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
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
	public String getBriefDescription() {
		return "Quote generator";
	}

}
