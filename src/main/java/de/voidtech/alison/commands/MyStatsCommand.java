package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.PackManager;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import main.java.de.voidtech.alison.utils.Responder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

public class MyStatsCommand extends AbstractCommand {

	@Override
	public void execute(Message message, List<String> args) {
		String ID;
    	if (args.isEmpty()) ID = message.getAuthor().getId();
    	else ID = args.get(0).replaceAll("([^0-9])", "");
    	
        if (ID.equals("")) Responder.SendAsReply(message, "I couldn't find that user :(");
        else {
        	Result<User> userResult = message.getJDA().retrieveUserById(ID).mapToResult().complete();
            if (userResult.isSuccess()) {
            	if (PrivacyManager.UserHasOptedOut(ID)) {
            		Responder.SendAsReply(message,"This user has chosen not to have their data analysed.");
            		return;
            	}
                MessageEmbed statsEmbed = createStatsEmbed(userResult.get()); 
                Responder.SendAsReply(message, statsEmbed);
            } else Responder.SendAsReply(message, "User " + ID + " could not be found");	
        }
	}

	private MessageEmbed createStatsEmbed(User user) {
		AlisonModel pack = PackManager.GetPack(user.getId());
		Map<String, Long> topFive = pack.getTopFiveWords();
        long wordCount = pack.getWordCount();
        String topFiveFormatted = String.join("\n", topFive.entrySet().stream()
        		.map(e -> e.getKey() + " - " + e.getValue()).collect(Collectors.toList()));
        EmbedBuilder statsEmbedBuilder = new EmbedBuilder()
        		.setColor(Color.ORANGE)
        		.setTitle("Stats for " + user.getAsTag())
        		.setThumbnail(user.getAvatarUrl())
        		.addField("Top 5 words", "```\n" + topFiveFormatted + "\n```", false)
        		.addField("Total Words", "```\n" + String.valueOf(wordCount) + "\n```", false);
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
		return "Shows some stats about your stored data!";
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

}
