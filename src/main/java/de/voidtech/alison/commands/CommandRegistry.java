package main.java.de.voidtech.alison.commands;

import java.util.ArrayList;
import java.util.List;

public class CommandRegistry {
	
	private static List<AbstractCommand> Commands = new ArrayList<AbstractCommand>();
	
	private static void populateCommands() {
		Commands.add(new ImitateCommand());
		Commands.add(new HowToxicIsThisCommand());
		Commands.add(new HowToxicAmICommand());
		Commands.add(new OptInCommand());
		Commands.add(new OptOutCommand());
		Commands.add(new InfoCommand());
		Commands.add(new HowToxicIsThisServerCommand());
		Commands.add(new HelpCommand());
		Commands.add(new ClearCommand());
	}
	
	public static List<AbstractCommand> GetAllCommands() {
		return Commands;
	}
	
	public static AbstractCommand GetCommand(String name) {
		if (Commands.isEmpty()) populateCommands();
		return Commands.stream()
				.filter(command -> command.getName().equals(name) | command.getShorthand().equals(name))
				.findFirst()
				.orElse(null);
	}
}
