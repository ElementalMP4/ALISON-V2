package main.java.de.voidtech.alison.utils;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadManager {
	private static final HashMap<String, ExecutorService> threadMap = new HashMap<>();

	private static ExecutorService findOrSpawnThread(String threadID) {
		
		if (!threadMap.containsKey(threadID)) {
			BasicThreadFactory factory = new BasicThreadFactory.Builder()
				     .namingPattern(threadID + "-%d")
				     .daemon(true)
				     .priority(Thread.NORM_PRIORITY)
				     .build();
			threadMap.put(threadID, Executors.newCachedThreadPool(factory));
		}
		
		return threadMap.get(threadID);
	}
	
	public static ExecutorService getThreadByName(String name) {
		return findOrSpawnThread(name);
	}
}