package main.java.de.voidtech.alison.commands.privacy;

import java.util.List;
import java.util.stream.Collectors;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.entities.AlisonModel;
import main.java.de.voidtech.alison.entities.AlisonWord;
import main.java.de.voidtech.alison.entities.CommandContext;
import main.java.de.voidtech.alison.utils.ModelManager;

public class ModifyCommand extends AbstractCommand {

	@Override
	public void execute(CommandContext context, List<String> args) {
		if (!context.getAuthor().getId().equals(Alison.getConfig().getMasterId())) return;
		AlisonModel model = ModelManager.getModel(args.get(0));
		if (model == null) {
			context.reply("That ain't it chief (model not found)");
			return;
		}
		
		switch (args.get(1)) {
			case "add":
				addToModel(model, context, args);
				break;
			case "delete":
				deleteFromModel(model, context, args);
				break;
			default:
				context.reply("Wrong subcommand dumbass:\n" + this.getUsage());
				break;
		}
	}

	private void deleteFromModel(AlisonModel model, CommandContext context, List<String> args) {
		if (args.size() < 3) {
			context.reply("You need at least 3 arguments smh:\n" + this.getUsage());
			return;
		}
		String toDelete = args.get(2);
		List<AlisonWord> newWordList;
		if (toDelete.contains("-")) {
			String[] parts = toDelete.split("-");
			String word = parts[0];
			String next = parts[1];
			newWordList = model.getAllAlisonWords().stream()
					.filter(w -> !w.getWord().equals(word) & !w.getNext().equals(next))
					.collect(Collectors.toList());
		} else {
			newWordList = model.getAllAlisonWords().stream()
					.filter(w -> !w.getWord().equals(toDelete))
					.collect(Collectors.toList());
		}
		model.overwriteModel(newWordList);
		context.reply("Removed all instances of " + toDelete + " from model " + args.get(0));		
	}

	private void addToModel(AlisonModel model, CommandContext context, List<String> args) {
		if (args.size() < 3) {
			context.reply("You need at least 3 arguments smh:\n" + this.getUsage());
			return;
		}
		int instances = 1;
		if (args.size() > 3) instances = Integer.parseInt(args.get(3));
		String newWord = args.get(2);
		if (!newWord.contains("-")) {
			context.reply("You need to supply a word pair seperated by a `-`");
			return;
		}
		String[] parts = newWord.split("-");
		String word = parts[0];
		String next = parts[1];
		List<AlisonWord> words = model.getAllAlisonWords();
		for (int i = 0; i < instances; i++) {
			words.add(new AlisonWord(word, next));
		}
		model.overwriteModel(words);
		context.reply("Added " + newWord + " " + instances + " times to model " + args.get(0));
	}

	@Override
	public String getName() {
		return "modify";
	}

	@Override
	public String getUsage() {
		return "mod [pack] [add/delete] [word-next] [instances]";
	}

	@Override
	public String getDescription() {
		return "Allows the botmaster to modify packs."
				+ " WARNING: Can cause issues with models if used in production."
				+ " Pull models, modify, then redeploy first for safety.";
	}

	@Override
	public String getShorthand() {
		return "mod";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.PRIVACY;
	}

	@Override
	public boolean isDmCapable() {
		return true;
	}

	@Override
	public boolean requiresArguments() {
		return true;
	}
}