package main.java.de.voidtech.alison.commands;

import java.awt.Color;
import java.util.List;

import main.java.de.voidtech.alison.entities.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PingCommand extends AbstractCommand{

	@Override
	public void execute(CommandContext context, List<String> args) {
		long time = System.currentTimeMillis();
			
		MessageEmbed beforePingHasBeenProcessedEmbed = new EmbedBuilder()
				.setAuthor("Ping?")
				.setColor(Color.RED)
				.build();
		
		context.getMessage().getChannel().sendMessageEmbeds(beforePingHasBeenProcessedEmbed).queue(response -> {
			MessageEmbed pingEmbed = new EmbedBuilder()//
				.setAuthor("Pong!")
				.setColor(Color.GREEN)
				.setDescription(String.format("Latency: %sms\nGateway Latency: %sms",
						(System.currentTimeMillis() - time),
						context.getMessage().getJDA().getGatewayPing()))
				.build();
			response.editMessageEmbeds(pingEmbed).queue();
		});
	}

	@Override
	public String getName() {
		return "ping";
	}

	@Override
	public String getUsage() {
		return "ping";
	}

	@Override
	public String getDescription() {
		return "Allows you to see Alison's current response time and Discord API latency";
	}

	@Override
	public String getShorthand() {
		return "p";
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
	public String getBriefDescription() {
		return "Bot response time";
	}

}
