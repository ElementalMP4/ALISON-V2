package main.java.de.voidtech.alison.commands;

import java.util.ArrayList;
import java.util.List;

import main.java.de.voidtech.alison.commands.generation.ImitateCommand;
import main.java.de.voidtech.alison.commands.generation.NicknameCommand;
import main.java.de.voidtech.alison.commands.generation.QuoteCommand;
import main.java.de.voidtech.alison.commands.generation.SearchCommand;
import main.java.de.voidtech.alison.commands.information.CountCommand;
import main.java.de.voidtech.alison.commands.information.HelpCommand;
import main.java.de.voidtech.alison.commands.information.InfoCommand;
import main.java.de.voidtech.alison.commands.information.MyStatsCommand;
import main.java.de.voidtech.alison.commands.information.PingCommand;
import main.java.de.voidtech.alison.commands.privacy.ClearCommand;
import main.java.de.voidtech.alison.commands.privacy.ModifyCommand;
import main.java.de.voidtech.alison.commands.privacy.OptInCommand;
import main.java.de.voidtech.alison.commands.privacy.OptOutCommand;
import main.java.de.voidtech.alison.commands.sentiment.HowToxicAmICommand;
import main.java.de.voidtech.alison.commands.sentiment.HowToxicIsThisCommand;
import main.java.de.voidtech.alison.commands.sentiment.HowToxicIsThisServerCommand;
import main.java.de.voidtech.alison.commands.sentiment.LeaderboardCommand;

public class CommandRegistry {
	
	private static List<AbstractCommand> Commands = new ArrayList<AbstractCommand>();
	
	private static void populateCommands() {
		Commands.add(new ClearCommand());
		Commands.add(new CountCommand());
		Commands.add(new HelpCommand());
		Commands.add(new HowToxicAmICommand());
		Commands.add(new HowToxicIsThisCommand());
		Commands.add(new HowToxicIsThisServerCommand());
		Commands.add(new ImitateCommand());
		Commands.add(new InfoCommand());
		Commands.add(new LeaderboardCommand());
		Commands.add(new ModifyCommand());
		Commands.add(new MyStatsCommand());
		Commands.add(new NicknameCommand());
		Commands.add(new OptInCommand());
		Commands.add(new OptOutCommand());
		Commands.add(new PingCommand());
		Commands.add(new QuoteCommand());
		Commands.add(new SearchCommand());
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