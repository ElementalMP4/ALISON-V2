package main.java.de.voidtech.alison.listeners;

import java.util.logging.Level;
import java.util.logging.Logger;

import main.java.de.voidtech.alison.utils.StatusLogger;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;

public class ReadyListener implements EventListener {

	public static final Logger LOGGER = Logger.getLogger(ReadyListener.class.getSimpleName());

	@Override
	public void onEvent(GenericEvent event) {
		if (event instanceof ReadyEvent) {
			ReadyEvent readyEvent = (ReadyEvent) event;
			LOGGER.log(Level.INFO, "Logged in as " + readyEvent.getJDA().getSelfUser().getAsTag());
			StatusLogger.init();
		}
	}
}