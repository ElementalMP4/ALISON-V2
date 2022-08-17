package main.java.de.voidtech.alison.commands.privacy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.entities.ButtonConsumer;
import main.java.de.voidtech.alison.utils.ModelManager;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;

public class ClearCommand extends AbstractCommand {
    
	private List<Component> createTrueFalseButtons() {
		List<Component> components = new ArrayList<>();
		components.add(Button.secondary("YES", ButtonConsumer.TRUE_EMOTE));
		components.add(Button.secondary("NO", ButtonConsumer.FALSE_EMOTE));
		return components;
	}
	
	private void getAwaitedButton(CommandContext context, String question, List<Component> actions, Consumer<ButtonConsumer> result) {
        Message msg = context.getMessage().reply(question).setActionRow(actions).mentionRepliedUser(false).complete();
        Alison.getBot().getEventWaiter().waitForEvent(ButtonClickEvent.class,
                e -> e.getUser().getId().equals(context.getAuthor().getId()),
				e -> result.accept(new ButtonConsumer(e, msg)), 30, TimeUnit.SECONDS,
                () -> context.getMessage().getChannel().sendMessage("Timed out waiting for reply. Your data has not been erased.").queue());
    }
	
    @Override
    public void execute(CommandContext context, List<String> args) {
    	getAwaitedButton(context, "Are you sure you want to delete all your data? **This cannot be undone!**", createTrueFalseButtons(), result -> {
			result.getButton().deferEdit().queue();
			switch (result.getButton().getComponentId()) {
			case "YES":
				ModelManager.deleteModel(context.getAuthor().getId());
				result.getMessage().editMessage("Your data has been cleared! If you want to stop data collection, use the `optout` command!").queue();
				break;
			case "NO":
				result.getMessage().editMessage("Your data has been left alone for now.").queue();
				break;
			}
		});		
    }
    
    @Override
    public String getName() {
        return "clear";
    }

	@Override
	public String getUsage() {
		return "clear";
	}

	@Override
	public String getDescription() {
		return "Allows you to delete all your learnt words. If you want ALISON to stop learning, use the optout command!";
	}

	@Override
	public String getShorthand() {
		return "c";
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
