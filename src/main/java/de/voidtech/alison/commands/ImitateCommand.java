package main.java.de.voidtech.alison.commands;

import java.util.List;

import main.java.de.voidtech.alison.utils.PackManager;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import main.java.de.voidtech.alison.utils.Responder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

public class ImitateCommand extends AbstractCommand {

    @Override
    public void execute(final Message message, final List<String> args) {
    	String ID;
    	if (args.isEmpty()) ID = message.getAuthor().getId();
    	else ID = args.get(0).replaceAll("([^0-9])", "");
    	
        if (!PackManager.packExists(ID)) {
        	Responder.sendAsReply(message, "I couldn't find that user :(");
        	return;
        }
        
        if (PrivacyManager.userHasOptedOut(ID)) {
    		Responder.sendAsReply(message,"This user has chosen not to be imitated.");
    		return;
    	}
        
        String msg = PackManager.getPack(ID).createSentence();
		if (msg == null) {
			Responder.sendAsReply(message, "There's no data for this user!");
			return;
		}
        
        Result<User> userResult = message.getJDA().retrieveUserById(ID).mapToResult().complete();
        if (userResult.isSuccess()) Responder.sendAsWebhook(message, msg, userResult.get().getAvatarUrl(), userResult.get().getName());
        else Responder.sendAsReply(message, msg);	
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
}
