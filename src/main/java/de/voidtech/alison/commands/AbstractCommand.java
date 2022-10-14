package main.java.de.voidtech.alison.commands;

import main.java.de.voidtech.alison.entities.Stopwatch;
import main.java.de.voidtech.alison.listeners.MessageListener;
import main.java.de.voidtech.alison.service.ThreadManager;
import net.dv8tion.jda.api.entities.ChannelType;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractCommand {

	public static final Logger LOGGER = Logger.getLogger(MessageListener.class.getSimpleName());

	public void run(CommandContext context, List<String> args) {
		if (!this.isDmCapable() && context.getMessage().getChannel().getType().equals(ChannelType.PRIVATE)) {
			context.reply("This command only works in guilds!");
			return;
		}
		if (this.requiresArguments() && args.isEmpty()) {
			context.reply("This command needs arguments but you didn't supply any!\n" + this.getUsage());
			return;
		}
		ExecutorService commandExecutor = ThreadManager.getThreadByName("T-Command");
		Runnable commandThread = () -> {
			Stopwatch stopwatch = new Stopwatch().start();
			LOGGER.log(Level.INFO, "Running command: " + this.getName() + " by " + context.getAuthor().getAsTag());
			if (this.isLongCommand()) context.getMessage().getChannel().sendTyping().complete();
			execute(context, args);
			LOGGER.log(Level.INFO, "Command " + this.getName() + " by " + context.getAuthor().getAsTag()
					+ " Took " + stopwatch.stop().getTime() + "ms");
		};
		commandExecutor.execute(commandThread);
	}

	public abstract void execute(CommandContext commandContext, List<String> args);
	public abstract String getName();
	public abstract String getUsage();
	public abstract String getDescription();
	public abstract String getShorthand();
	public abstract CommandCategory getCommandCategory();
	public abstract boolean isDmCapable();
	public abstract boolean requiresArguments();
	public abstract boolean isLongCommand();
}