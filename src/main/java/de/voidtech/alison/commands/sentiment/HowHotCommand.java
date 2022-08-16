package main.java.de.voidtech.alison.commands.sentiment;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.entities.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class HowHotCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		int seed = 0;
		if (context.getMessage().getAttachments().isEmpty()) {
			context.reply("You need to upload an image to use this command!");
			return;
		}
		if (!context.getMessage().getAttachments().get(0).isImage()) {
			context.reply("This command only works with images!");
			return;
		}
		Attachment attachment = context.getMessage().getAttachments().get(0);
		seed = attachment.getHeight() + attachment.getWidth() + attachment.getSize();
		int rating = new Random(seed).nextInt(10);
		
		MessageEmbed hotnessEmbed = new EmbedBuilder()
				.setColor(getColor(rating))
				.setTitle("I rate you a " + rating + " out of 10. " + getPhrase(rating))
				.build();
		context.reply(hotnessEmbed);
	}
	
	private Color getColor(int rating)
	{
		return rating > 6 
				? Color.GREEN 
				: rating > 3 
				? Color.ORANGE 
				: Color.RED;
	}
	
	private String getPhrase(int rating)
	{
		return rating > 6
				? "What's cookin' good lookin' 😎" 
				: rating > 3
				? "Not too shabby..." 
				: "I may need to bleach my eyes.";
	}

	@Override
	public String getName() {
		return "howhotami";
	}

	@Override
	public String getUsage() {
		return "howhotami";
	}

	@Override
	public String getDescription() {
		return "Upload an image and use this command to see how hot you are! (ALISON will be very honest."
				+ " You may not like the result.)";
	}

	@Override
	public String getShorthand() {
		return "hhme";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.SENTIMENT_ANALYSIS;
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
