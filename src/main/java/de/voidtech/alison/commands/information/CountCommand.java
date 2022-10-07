package main.java.de.voidtech.alison.commands.information;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.ModelManager;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

import java.util.List;

public class CountCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		User user;
		int index = 0;
    	if (args.size() < 2) user = context.getAuthor();
    	else {
    		Result<User> userResult = context.getJDA().retrieveUserById(args.get(0).replaceAll("([^0-9])", "")).mapToResult().complete();
    		if (!userResult.isSuccess()) {
    			context.reply("I couldn't find that user!");
				return;
    		} else {
    			user = userResult.get();
    			index = 1;
    		}
    	}
		AlisonModel model = ModelManager.getModel(user.getId());
		String word = args.get(index);
		long count = model.getAllWords().stream().filter(w -> w.equals(word)).count();
		context.reply("**" + user.getAsTag() + "** has said **" + word + "** `" + count + "` times.");
		
	}

	@Override
	public String getName() {
		return "count";
	}

	@Override
	public String getUsage() {
		return "count [word]\n"
				+ "count [user mention/ID] [word]";
	}

	@Override
	public String getDescription() {
		return "Find out how many times you or someone else has said a word!";
	}

	@Override
	public String getShorthand() {
		return "co";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.INFORMATION;
	}

	@Override
	public boolean isDmCapable() {
		return true;
	}

	@Override
	public boolean requiresArguments() {
		return true;
	}

	@Override
	public boolean isLongCommand() {
		return true;
	}

}