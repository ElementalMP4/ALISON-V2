package main.java.de.voidtech.alison.commands;

import main.java.de.voidtech.alison.service.ThreadManager;
import net.dv8tion.jda.api.entities.ChannelType;

import java.util.List;
import java.util.concurrent.ExecutorService;

public abstract class AbstractCommand {

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
		Runnable commandThread = () -> execute(context, args);
		if (this.isLongCommand()) context.getMessage().getChannel().sendTyping().complete();
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