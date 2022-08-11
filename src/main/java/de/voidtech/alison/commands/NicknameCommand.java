package main.java.de.voidtech.alison.commands;

import java.util.List;

import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.PackManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.Result;

public class NicknameCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		Member member;
    	if (args.isEmpty()) member = context.getMember();
    	else {
    		if (!context.getMember().getPermissions().contains(Permission.NICKNAME_MANAGE)) {
    			context.reply("You don't have permission to change other people's nicknames!");
				return;
    		}
    		Result<Member> memberResult = context.getGuild().retrieveMemberById(args.get(0).replaceAll("([^0-9])", "")).mapToResult().complete();
    		if (!memberResult.isSuccess()) {
    			context.reply("I couldn't find that user!");
				return;
    		} else member = memberResult.get();
    	}
    	
    	if (member.isOwner()) {
    		context.reply("I Can't change the owner's nickname!");
    		return;
    	}
		
		AlisonModel model = PackManager.getPack(member.getId());
		String nickname = model.createNickname();
		if (nickname == null) context.reply("I don't have enough information to make a nickname!");
		else {
			if (!context.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE)) {
				context.reply("I don't have permission to change nicknames! Please make sure I have the `Manage Nicknames` permission!");
				return;
			}
			if (!context.getGuild().getSelfMember().canInteract(member)) {
				context.reply("I don't have permission to change **" + member.getUser().getAsTag() +
						"'s**  nickname! I need my role to be above **" + member.getUser().getAsTag() + "'s** highest role!");
				return;
			}
			member.modifyNickname(nickname).complete();
			context.reply("**" + member.getUser().getAsTag() + "'s** Nickname changed to **" + nickname + "**");
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
