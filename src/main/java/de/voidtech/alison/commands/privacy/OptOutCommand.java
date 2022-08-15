package main.java.de.voidtech.alison.commands.privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.entities.ButtonConsumer;
import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.ModelManager;
import main.java.de.voidtech.alison.utils.PrivacyManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

public class OptOutCommand extends AbstractCommand {

	private void getAwaitedButton(CommandContext context, String question, List<Component> actions, Consumer<ButtonConsumer> result) {
        Message msg = context.getMessage().reply(question).setActionRow(actions).mentionRepliedUser(false).complete();
        Alison.getBot().getEventWaiter().waitForEvent(ButtonClickEvent.class,
                e -> e.getUser().getId().equals(context.getAuthor().getId()),
				e -> result.accept(new ButtonConsumer(e, msg)), 30, TimeUnit.SECONDS,
                () -> context.getMessage().getChannel().sendMessage("Timed out waiting for reply").queue());
    }
	
	@Override
	public void execute(CommandContext context, List<String> args) {
		if (!PrivacyManager.userHasOptedOut(context.getAuthor().getId())) {
			PrivacyManager.optOut(context.getAuthor().getId());	
			getAwaitedButton(context, "Would you also like to delete any collected data?", createTrueFalseButtons(), result -> {
				result.getButton().deferEdit().queue();
				switch (result.getButton().getComponentId()) {
				case "YES":
					ModelManager.deleteModel(context.getAuthor().getId());
					result.getMessage().editMessage("Your data has been cleared!").queue();
					break;
				case "NO":
					result.getMessage().editMessage("Your data has been left alone for now. Use the `clear` command if you change your mind!").queue();
					break;
				}
			});			
			
		} else context.reply("You have already chosen to opt out!");
	}
	
	private List<Component> createTrueFalseButtons() {
		List<Component> components = new ArrayList<>();
		components.add(Button.secondary("YES", ButtonConsumer.TRUE_EMOTE));
		components.add(Button.secondary("NO", ButtonConsumer.FALSE_EMOTE));
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
				+ " You can use the optin command if you change your mind, and the clear command to delete all your data."
				+ " Once you are opted out, your data will no longer be used for imitation and other commands.";
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

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.PRIVACY;
	}

}
