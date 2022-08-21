package main.java.de.voidtech.alison.commands.sentiment;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import main.java.de.voidtech.alison.Alison;
import main.java.de.voidtech.alison.commands.AbstractCommand;
import main.java.de.voidtech.alison.commands.CommandCategory;
import main.java.de.voidtech.alison.commands.CommandContext;
import main.java.de.voidtech.alison.utils.DatabaseInterface;
import main.java.de.voidtech.alison.utils.ParsingUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.Result;

public class HowHotCommand extends AbstractCommand {

	//User SQL
	private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS RiggedHotness (userID TEXT, hotness TEXT)";
	private static final String UNRIG = "DELETE FROM RiggedHotness WHERE userID = '%s'";
	private static final String RIG = "INSERT INTO RiggedHotness VALUES ('%s', '%s')";
	private static final String GET_RIGGED = "SELECT * FROM RiggedHotness WHERE userID = '%s'";
	
	private static Connection DatabaseConnection = null;
	
	public HowHotCommand() {
		try {
			DatabaseConnection = DriverManager.getConnection("jdbc:sqlite:Alison.db");
			DatabaseInterface.executeUpdate(DatabaseConnection, CREATE_TABLE);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void execute(CommandContext context, List<String> args) {
		if (context.getAuthor().getId().equals(Alison.getConfig().getMasterId()) & !args.isEmpty()) {
			if (!ParsingUtils.isInteger(args.get(0).replaceAll("([^0-9a-zA-Z])", ""))) {
				handleRigging(context, args);
				return;
			}
		}
		String ID;
		if (args.isEmpty()) ID = context.getAuthor().getId();
		else ID = args.get(0).replaceAll("([^0-9a-zA-Z])", "");
		Result<User> userResult = context.getJDA().retrieveUserById(ID).mapToResult().complete();
		if (userResult.isSuccess()) {
			int rating = getRating(userResult.get());
			MessageEmbed hotnessEmbed = new EmbedBuilder()
					.setColor(getColor(rating))
					.setTitle("I rate you a " + rating + " out of 10. " + getPhrase(rating))
					.setImage(userResult.get().getAvatarUrl() + "?size=2048")
					.build();
			context.reply(hotnessEmbed);
		} else context.reply("I couldn't find that user :(");	
	}
	
	private int getRating(User user) {
		if (userIsRigged(user)) return getUserRigging(user);
		return user.getId().equals(Alison.getConfig().getMasterId()) ? 10 :
			new Random(user.getAvatarId().hashCode()).nextInt(10);
	}

	private int getUserRigging(User user) {
		try {
			ResultSet result = DatabaseInterface.queryDatabase(DatabaseConnection,
					String.format(GET_RIGGED, user.getId()));
			return Integer.parseInt(result.getString("hotness"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 5;
	}

	private boolean userIsRigged(User user) {
		try {
			ResultSet result = DatabaseInterface.queryDatabase(DatabaseConnection,
					String.format(GET_RIGGED, user.getId()));
			return !result.isClosed();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void handleRigging(CommandContext context, List<String> args) {
		if (args.size() < 2) context.reply("You need at least 2 arguments dummy. rig/unrig and user ID");
		else {
			switch (args.get(0)) {
			case "rig":
				rigUser(context, args);
				break;
			case "unrig":
				unrigUser(context, args);
				break;
			default:
				context.reply("You can only rig or unrig you cretin");
				break;
			}
		}
	}

	private void unrigUser(CommandContext context, List<String> args) {
		DatabaseInterface.executeUpdate(DatabaseConnection, String.format(UNRIG, args.get(1).replaceAll("([^0-9a-zA-Z])", "")));
		context.reply("<@" + args.get(1).replaceAll("([^0-9a-zA-Z])", "") + "> has been unrigged.");
	}

	private void rigUser(CommandContext context, List<String> args) {
		if (args.size() < 3) context.reply("You need to specify a hotness to rig at you buffoon");
		else {
			DatabaseInterface.executeUpdate(DatabaseConnection, String.format(UNRIG, args.get(1).replaceAll("([^0-9a-zA-Z])", "")));
			DatabaseInterface.executeUpdate(DatabaseConnection, String.format(RIG, args.get(1).replaceAll("([^0-9a-zA-Z])", ""), args.get(2)));
			context.reply("<@" + args.get(1).replaceAll("([^0-9a-zA-Z])", "") + "> has been rigged at a hotness of " + args.get(2) + "/10");			
		}
	}

	private Color getColor(int rating)
	{
		return rating > 6 
				? Color.GREEN 
				: rating > 3 
				? Color.ORANGE 
				: Color.RED;
	}
	
	private String getPhrase(int rating)
	{
		return rating > 6
				? "What's cookin' good lookin' :smirk:" 
				: rating > 3
				? "Not too shabby..." 
				: "I may need to bleach my eyes.";
	}

	@Override
	public String getName() {
		return "howhotami";
	}

	@Override
	public String getUsage() {
		return "howhotami\n"
				+ "howhotami [user ID/mention]";
	}

	@Override
	public String getDescription() {
		return "See how hot you are (based on your pfp)! (ALISON will be very honest. You may not like the result.)";
	}

	@Override
	public String getShorthand() {
		return "hhme";
	}

	@Override
	public CommandCategory getCommandCategory() {
		return CommandCategory.SENTIMENT_ANALYSIS;
	}

	@Override
	public boolean isDmCapable() {
		return true;
	}

	@Override
	public boolean requiresArguments() {
		return false;
	}

}
