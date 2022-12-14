package main.java.de.voidtech.alison.commands.generation;

import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.ButtonConsumer;
import main.java.de.voidtech.alison.entities.ButtonListener;
import main.java.de.voidtech.alison.service.ModelManager;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.Result;

import java.util.List;

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
    			context.reply("I couldn't find that user :(");
				return;
    		} else member = memberResult.get();
    	}
		
		AlisonModel model = ModelManager.getModel(member.getId());
		String nickname = model.createNickname();
		if (nickname == null) context.reply("I don't have enough information to make a nickname :(");
		else {
			new ButtonListener(context, "Change **" + member.getUser().getAsTag() + "'s** nickname to **" + nickname + "**?", result -> handleNicknameUpdateChoice(member, nickname, context, result));
		}
	}
	
	private void handleNicknameUpdateChoice(Member member, String nickname, CommandContext context, ButtonConsumer result ) {
		if (result.userSaidYes()) {
			if (member.isOwner()) {
	    		result.getMessage().editMessage("I can't change the owner's nickname!").queue();
	    		return;
	    	}
			if (!context.getGuild().getSelfMember().hasPermission(Permission.NICKNAME_MANAGE)) {
				result.getMessage().editMessage("I don't have permission to change nicknames! Please make sure I have the `Manage Nicknames` permission!").queue();
				return;
			}
			if (!context.getGuild().getSelfMember().canInteract(member)) {
				result.getMessage().editMessage("I don't have permission to change **" + member.getUser().getAsTag() +
						"'s**  nickname! I need my role to be above **" + member.getUser().getAsTag() + "'s** highest role!").queue();
				return;
			}
			member.modifyNickname(nickname).complete();
			result.getMessage().editMessage("**" + member.getUser().getAsTag() + "'s** Nickname changed to **" + nickname + "**").queue();	
		} else {
			result.getMessage().editMessage("**" + member.getUser().getAsTag() + "'s** Nickname has not been changed to **" + nickname + "**").queue();
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

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.TEXT_GENERATION;
	}

	@Override
	public boolean isLongCommand() {
		return false;
	}

}