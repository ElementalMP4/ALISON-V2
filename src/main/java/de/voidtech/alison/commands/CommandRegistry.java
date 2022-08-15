package main.java.de.voidtech.alison.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {
	
	private static List<AbstractCommand> Commands = new ArrayList<AbstractCommand>();
	
	private static void populateCommands() {
		Commands.add(new ClearCommand());
		Commands.add(new HelpCommand());
		Commands.add(new HowToxicAmICommand());
		Commands.add(new HowToxicIsThisCommand());
		Commands.add(new HowToxicIsThisServerCommand());
		Commands.add(new ImitateCommand());
		Commands.add(new InfoCommand());
		Commands.add(new LeaderboardCommand());
		Commands.add(new MyStatsCommand());
		Commands.add(new NicknameCommand());
		Commands.add(new OptInCommand());
		Commands.add(new OptOutCommand());
		Commands.add(new PingCommand());
		Commands.add(new QuoteCommand());
	}
	
	public static List<AbstractCommand> getAllCommands() {
		if (Commands.isEmpty()) populateCommands();
		return Commands;
	}
	
	public static AbstractCommand getCommand(String name) {
		return getAllCommands().stream()
				.filter(command -> command.getName().equals(name) | command.getShorthand().equals(name))
				.findFirst()
				.orElse(null);
	}
}