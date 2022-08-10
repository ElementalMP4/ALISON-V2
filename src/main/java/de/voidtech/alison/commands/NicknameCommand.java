package main.java.de.voidtech.alison.commands;

import java.util.List;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.utils.PackManager;
import main.java.de.voidtech.alison.utils.Responder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.Result;

public class NicknameCommand extends AbstractCommand {

	@Override
	public void execute(Message message, List<String> args) {
		Member member;
    	if (args.isEmpty()) member = message.getMember();
    	else {
    		if (!message.getMember().getPermissions().contains(Permission.NICKNAME_MANAGE)) {
    			Responder.sendAsReply(message, "You don't have permission to change other people's nicknames!");
				return;
    		}
    		Result<Member> memberResult = message.getGuild().retrieveMemberById(args.get(0).replaceAll("([^0-9])", "")).mapToResult().complete();
    		if (!memberResult.isSuccess()) {
    			Responder.sendAsReply(message, "I couldn't find that user!");
				return;
    		} else member = memberResult.get();
    	}
		
		AlisonModel model = PackManager.getPack(member.getId());
		String nickname = model.createNickname();
		if (nickname == null) Responder.sendAsReply(message, "I don't have enough information to make a nickname!");
		else {
			if (!message.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE)) {
				Responder.sendAsReply(message, "I don't have permission to change nicknames! Please make sure I have the `Manage Nicknames` permission!");
				return;
			}
			if (!message.getGuild().getSelfMember().canInteract(member)) {
				Responder.sendAsReply(message, "I don't have permission to change **" + member.getUser().getAsTag() +
						"'s**  nickname! I need my role to be above **" + member.getUser().getAsTag() + "'s** highest role!");
				return;
			}
			member.modifyNickname(nickname).complete();
			Responder.sendAsReply(message, "**" + member.getUser().getAsTag() + "'s** Nickname changed to **" + nickname + "**");
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
