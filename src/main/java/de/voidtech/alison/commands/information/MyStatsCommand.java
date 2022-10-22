package main.java.de.voidtech.alison.commands.information;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.service.ModelManager;
import main.java.de.voidtech.alison.service.PrivacyManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyStatsCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		String ID;
    	if (args.isEmpty()) ID = context.getAuthor().getId();
    	else ID = args.get(0).replaceAll("([^0-9])", "");
    	
        if (ID.equals("")) context.reply("I couldn't find that user :(");
        else {
        	Result<User> userResult = context.getJDA().retrieveUserById(ID).mapToResult().complete();
            if (userResult.isSuccess()) {
            	if (PrivacyManager.userHasOptedOut(ID)) {
            		context.reply("This user has chosen not to have their data analysed.");
            		return;
            	}
                MessageEmbed statsEmbed = createStatsEmbed(userResult.get());
                context.reply(statsEmbed);
            } else context.reply("User " + ID + " could not be found");	
        }
	}

	private MessageEmbed createStatsEmbed(User user) {
		AlisonModel pack = ModelManager.getModel(user.getId());
		Map<String, Long> topFive = pack.getTopFiveWords();
        long wordCount = pack.getWordCount();
        String topFiveFormatted = topFive.entrySet().stream()
        		.map(e -> e.getKey() + " - " + e.getValue()).collect(Collectors.joining("\n"));
        EmbedBuilder statsEmbedBuilder = new EmbedBuilder()
        		.setColor(Color.ORANGE)
        		.setTitle("Stats for " + user.getAsTag())
        		.setThumbnail(user.getAvatarUrl())
        		.addField("Top 5 words", "```\n" + topFiveFormatted + "\n```", false)
        		.addField("Total Words", "```\n" + wordCount + "\n```", false);
		return statsEmbedBuilder.build();
	}

	@Override
	public String getName() {
		return "mystats";
	}

	@Override
	public String getUsage() {
		return "mystats\n"
				+ "mystats [user mention or ID]";
	}

	@Override
	public String getDescription() {
		return "Shows some stats about your stored data, as well as your 5 most commonly used words!";
	}

	@Override
	public String getShorthand() {
		return "me";
	}

	@Override
	public boolean isDmCapable() {
		return true;
	}

	@Override
	public boolean requiresArguments() {
		return false;
	}
	
	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.INFORMATION;
	}

	@Override
	public boolean isLongCommand() {
		return false;
	}

}