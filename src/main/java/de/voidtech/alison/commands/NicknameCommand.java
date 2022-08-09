package main.java.de.voidtech.alison.commands;

import java.util.List;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.PackManager;
import main.java.de.voidtech.alison.utils.Responder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class NicknameCommand extends AbstractCommand {

	@Override
	public void execute(Message message, List<String> args) {
		AlisonModel model = PackManager.getPack(message.getAuthor().getId());
		String nickname = model.createNickname();
		if (nickname == null) Responder.sendAsReply(message, "I don't have enough information to make a nickname for you!");
		else {
			if (!message.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE)) {
				Responder.sendAsReply(message, "I don't have permission to change your nickname!");
				return;
			}
			message.getMember().modifyNickname(nickname);
			Responder.sendAsReply(message, "Nickname changed to **" + nickname + "**");
		}
	}

	@Override
	public String getName() {
		return "nickname";
	}

	@Override
	public String getUsage() {
		return "nickname";
	}

	@Override
	public String getDescription() {
		return "Get Alison to generate a nickname for you based on things you've said";
	}

	@Override
	public String getShorthand() {
		return "nn";
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
