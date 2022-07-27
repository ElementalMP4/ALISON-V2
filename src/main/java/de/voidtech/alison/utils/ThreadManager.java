package main.java.de.voidtech.alison.utils;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

public class ThreadManager {
	private static HashMap<String, ExecutorService> threadMap = new HashMap<String, ExecutorService>();

	private static ExecutorService findOrSpawnThread(String threadID) {
		
		if (!threadMap.containsKey(threadID)) {
			BasicThreadFactory factory = new BasicThreadFactory.Builder()
				     .namingPattern(threadID + "-%d")
				     .daemon(true)
				     .priority(Thread.NORM_PRIORITY)
				     .build();
			threadMap.put(threadID, Executors.newSingleThreadExecutor(factory));	
		}
		
		return threadMap.get(threadID);
	}
	
	public static ExecutorService getThreadByName(String name) {
		return findOrSpawnThread(name);
	}
}