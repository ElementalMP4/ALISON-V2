package main.java.de.voidtech.alison.commands;

import java.util.List;
import java.util.concurrent.ExecutorService;

import main.java.de.voidtech.alison.utils.Responder;
import main.java.de.voidtech.alison.utils.ThreadManager;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;

public abstract class AbstractCommand {
	
	public void run(Message message, List<String> args) {
		if (!this.isDmCapable() && message.getChannel().getType().equals(ChannelType.PRIVATE)) {
			Responder.sendAsReply(message, "This command only works in guilds!");
			return;
		}
		if (this.requiresArguments() && args.isEmpty()) { 
			Responder.sendAsReply(message, "This command needs arguments but you didn't supply any!\n" + this.getUsage());
			return;
		}
		ExecutorService commandExecutor = ThreadManager.getThreadByName("T-Command");
		Runnable commandThread = () -> execute(message, args);
		commandExecutor.execute(commandThread);
	}
	
	public abstract void execute(Message message, List<String> args);
	public abstract String getName();
	public abstract String getUsage();
	public abstract String getDescription();
	public abstract String getShorthand();
	public abstract boolean isDmCapable();
	public abstract boolean requiresArguments();
	
}
