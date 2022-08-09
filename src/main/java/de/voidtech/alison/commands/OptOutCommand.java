package main.java.de.voidtech.alison.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.entities.ButtonConsumer;
import main.java.de.voidtech.alison.utils.PackManager;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

public class OptOutCommand extends AbstractCommand {

	private static final String TRUE_EMOTE = "\u2705";
	private static final String FALSE_EMOTE = "\u274C";

	private void getAwaitedButton(Message message, String question, List<Component> actions, Consumer<ButtonConsumer> result) {
        Message msg = message.reply(question).setActionRow(actions).mentionRepliedUser(false).complete();
        Alison.GetBot().getEventWaiter().waitForEvent(ButtonClickEvent.class,
                e -> e.getUser().getId().equals(message.getAuthor().getId()),
				e -> result.accept(new ButtonConsumer(e, msg)), 30, TimeUnit.SECONDS,
                () -> message.getChannel().sendMessage("Timed out waiting for reply").queue());
    }
	
	@Override
	public void execute(Message message, List<String> args) {
		if (!PrivacyManager.UserHasOptedOut(message.getAuthor().getId())) {
			PrivacyManager.OptOut(message.getAuthor().getId());	
			getAwaitedButton(message, "Would you also like to delete any collected data?", createTrueFalseButtons(), result -> {
				result.getButton().deferEdit().queue();
				switch (result.getButton().getComponentId()) {
				case "YES":
					PackManager.DeletePack(message.getAuthor().getId());
					result.getMessage().editMessage("Data cleared!").queue();
					break;
				case "NO":
					result.getMessage().editMessage("Data has been left alone for now. Use the `clear` command if you change your mind!").queue();
					break;
				}
			});			
			
		} else message.reply("You have already chosen to opt out!").mentionRepliedUser(false).queue();
	}
	
	private List<Component> createTrueFalseButtons() {
		List<Component> components = new ArrayList<>();
		components.add(Button.secondary("YES", TRUE_EMOTE));
		components.add(Button.secondary("NO", FALSE_EMOTE));
		return components;
	}

	@Override
	public String getName() {
		return "optout";
	}

	@Override
	public String getUsage() {
		return "optout";
	}

	@Override
	public String getDescription() {
		return "Stops ALISON from learning from your messages. By default, you will be opted in."
				+ " You can use the optin command to let ALISON learn from you, and the clear command to delete all your learnt words.";
	}

	@Override
	public String getShorthand() {
		return "out";
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
