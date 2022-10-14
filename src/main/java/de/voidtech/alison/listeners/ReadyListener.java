package main.java.de.voidtech.alison.listeners;

import main.java.de.voidtech.alison.service.BrowserUtils;
import main.java.de.voidtech.alison.service.StatusLogger;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ReadyListener implements EventListener {

	public static final Logger LOGGER = Logger.getLogger(ReadyListener.class.getSimpleName());

	@Override
	public void onEvent(@NotNull GenericEvent event) {
		if (event instanceof ReadyEvent) {
			ReadyEvent readyEvent = (ReadyEvent) event;
			LOGGER.log(Level.INFO, "Logged in as " + readyEvent.getJDA().getSelfUser().getAsTag());
			StatusLogger.sendStartupMessage();
			BrowserUtils.initialise();
		}
	}
}