package main.java.de.voidtech.alison.commands;

import java.util.List;
import java.util.concurrent.ExecutorService;

import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.ThreadManager;
import net.dv8tion.jda.api.entities.ChannelType;

public abstract class AbstractCommand {

	public void run(CommandContext commandContext, List<String> args) {
		if (!this.isDmCapable() && commandContext.getMessage().getChannel().getType().equals(ChannelType.PRIVATE)) {
			commandContext.reply("This command only works in guilds!");
			return;
		}
		if (this.requiresArguments() && args.isEmpty()) {
			commandContext.reply("This command needs arguments but you didn't supply any!\n" + this.getUsage());
			return;
		}
		ExecutorService commandExecutor = ThreadManager.getThreadByName("T-Command");
		Runnable commandThread = () -> execute(commandContext, args);
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
}