package main.java.de.voidtech.alison.commands.generation;

import java.util.List;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.ModelManager;
import main.java.de.voidtech.alison.utils.ParsingUtils;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import main.java.de.voidtech.alison.utils.WebhookManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.utils.Result;

public class ImitateCommand extends AbstractCommand {

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
			context.reply("This user has chosen not to be imitated.");
			return;
		}
		
		AlisonModel model = ModelManager.getModel(ID);
		String msg = model.createSentence();
		if (msg == null) {
			context.reply("I couldn't find any data to make an imitation: (");
			return;
		}

		Webhook hook = WebhookManager.getOrCreateWebhook(context.getMessage().getTextChannel(), "ALISON",
				context.getJDA().getSelfUser().getId());
		if (model.hasMeta()) {
			WebhookManager.sendWebhookMessage(hook, msg, model.getMeta().getName(), model.getMeta().getIconUrl());
			return;
		}
		
		if (ParsingUtils.isSnowflake(ID)) {
			Result<User> userResult = context.getJDA().retrieveUserById(ID).mapToResult().complete();
			if (userResult.isSuccess())	WebhookManager.sendWebhookMessage(hook, msg,
					userResult.get().getName(), userResult.get().getAvatarUrl());
			else context.reply("I couldn't find that user :(");
		} else context.reply(msg);
	}

	@Override
	public String getName() {
		return "imitate";
	}

	@Override
	public String getUsage() {
		return "imitate [user mention or ID]";
	}

	@Override
	public String getDescription() {
		return "Allows you to use the power of ALISON to imitate someone! ALISON constantly learns from your messages,"
				+ " and when you use this command, she uses her knowledge to try and speak like you do!\n\n"
				+ "To stop ALISON from learning from you, use the optout command!";
	}

	@Override
	public String getShorthand() {
		return "i";
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
